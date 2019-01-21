/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Hottinger Baldwin Messtechnik GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hbm.devices.scan.announce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.FakeMessageReceiver;

public class DeviceMonitorTest {

    private FakeMessageReceiver fsmmr;

    private boolean newDevice;
    private boolean updateDevice;
    private boolean lostDevice;
    private Object event;
    private DeviceMonitor monitor;

    @BeforeEach
    public void setUp() {
        this.newDevice = false;
        this.updateDevice = false;
        this.lostDevice = false;
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
        fsmmr.addObserver(parser);
        monitor = new DeviceMonitor();
        parser.addObserver(monitor);
        monitor.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof NewDeviceEvent) {
                    newDevice = true;
                } else if (arg instanceof UpdateDeviceEvent) {
                    updateDevice = true;
                } else if (arg instanceof LostDeviceEvent) {
                    lostDevice = true;
                }
                event = arg;
            }
        });
    }

    @Test
    public void newDeviceEvent() {
        fsmmr.emitSingleCorrectMessage();
        assertTrue(newDevice && !updateDevice && !lostDevice, "No new device event fired");
        assertTrue(((NewDeviceEvent)event).getAnnounce() instanceof Announce, "No anounce object in event");
    }

    @Test
    public void updateDeviceEvent() {
        fsmmr.emitSingleCorrectMessage();
        assertTrue(newDevice && !updateDevice && !lostDevice, "No new device event fired");
        newDevice = false;

        fsmmr.emitSingleCorrentMessageDifferentIP();
        assertTrue(updateDevice && !newDevice && !lostDevice, "No update device event fired");
        assertTrue(((UpdateDeviceEvent)event).getOldAnnounce() instanceof Announce, "No old anounce object in event");
        assertTrue(((UpdateDeviceEvent)event).getNewAnnounce() instanceof Announce, "No new anounce object in event");

        updateDevice = false;
        newDevice = false;
        lostDevice = false;

        // Check if the event is not fired again
        fsmmr.emitSingleCorrentMessageDifferentIP();
        assertFalse(updateDevice || newDevice || lostDevice, "Update device event fired twice");
    }

    @Test
    public void testLostDevice() {
        fsmmr.emitSingleCorrectMessageShortExpire();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        assertTrue(lostDevice && newDevice && !updateDevice, "No lost device event fired");
        assertTrue(((LostDeviceEvent)event).getAnnounce() instanceof Announce, "No anounce object in event");
    }

    @Test
    public void stopTestWithoutRunningTimer() {
        assertFalse(monitor.isClosed(), "monitor stopped after creation");
        monitor.close();
        assertTrue(monitor.isClosed(), "monitor not stopped");
    }

    @Test
    public void stopTestWithRunningTimer() {
        assertFalse(monitor.isClosed(), "monitor stopped after creation");
        fsmmr.emitSingleCorrectMessage();
        monitor.close();
        assertTrue(monitor.isClosed(), "monitor not stopped");
    }

    @Test
    public void messageAfterClose() {
        assertFalse(monitor.isClosed(), "monitor stopped after creation");
        monitor.close();
        assertTrue(monitor.isClosed(), "monitor not stopped");

        fsmmr.emitSingleCorrectMessage();
        assertFalse(updateDevice || newDevice || lostDevice, "Got update after closed device monitor");
    }
}
