/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Stephan Gatzka
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.FakeMessageReceiver;

public class DeviceMonitorTest {

    private FakeMessageReceiver fsmmr;

    private boolean newDevice;
    private boolean updateDevice;
    private boolean lostDevice;
    private Object event;
    private DeviceMonitor monitor;

    @Before
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
        assertTrue("No new device event fired", newDevice && !updateDevice && !lostDevice);
        assertTrue("No anounce object in event", ((NewDeviceEvent)event).getAnnounce() instanceof Announce);
    }

    @Test
    public void updateDeviceEvent() {
        fsmmr.emitSingleCorrectMessage();
        assertTrue("No new device event fired", newDevice && !updateDevice && !lostDevice);
        newDevice = false;

        fsmmr.emitSingleCorrentMessageDifferentIP();
        assertTrue("No update device event fired", updateDevice && !newDevice && !lostDevice);
        assertTrue("No old anounce object in event", ((UpdateDeviceEvent)event).getOldAnnounce() instanceof Announce);
        assertTrue("No new anounce object in event", ((UpdateDeviceEvent)event).getNewAnnounce() instanceof Announce);

        updateDevice = false;
        newDevice = false;
        lostDevice = false;

        // Check if the event is not fired again
        fsmmr.emitSingleCorrentMessageDifferentIP();
        assertFalse("Update device event fired twice", updateDevice || newDevice || lostDevice);
    }

    @Test
    public void testLostDevice() {
        fsmmr.emitSingleCorrectMessageShortExpire();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        assertTrue("No lost device event fired", lostDevice && newDevice && !updateDevice);
        assertTrue("No anounce object in event", ((LostDeviceEvent)event).getAnnounce() instanceof Announce);
    }

    @Test
    public void stopTestWithoutRunningTimer() {
        assertFalse("monitor stopped after creation", monitor.isClosed());
        monitor.close();
        assertTrue("monitor not stopped", monitor.isClosed());
    }

    @Test
    public void stopTestWithRunningTimer() {
        assertFalse("monitor stopped after creation", monitor.isClosed());
        fsmmr.emitSingleCorrectMessage();
        monitor.close();
        assertTrue("monitor not stopped", monitor.isClosed());
    }
}
