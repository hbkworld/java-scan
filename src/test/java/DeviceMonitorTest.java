/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
 */

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.DeviceMonitor;
import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.events.NewDeviceEvent;
import com.hbm.devices.scan.events.UpdateDeviceEvent;
import com.hbm.devices.scan.messages.MessageParser;

public class DeviceMonitorTest {

    private FakeMessageReceiver fsmmr;

    private boolean newDevice;
    private boolean updateDevice;

    @Before
    public void setup() {
        this.newDevice = false;
        this.updateDevice = false;
        fsmmr = new FakeMessageReceiver();
        MessageParser jf = new MessageParser();
        fsmmr.addObserver(jf);
        DeviceMonitor af = new DeviceMonitor();
        jf.addObserver(af);
        af.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof NewDeviceEvent) {
                    newDevice = true;
                } else if (arg instanceof UpdateDeviceEvent) {
                    updateDevice = true;
                }
            }
        });
    }

    @Test
    public void NewDeviceEvent() {
        fsmmr.emitSingleCorrectMessage();
        assertTrue(newDevice);
        assertFalse(updateDevice);
    }

    @Test
    public void UpdateDeviceEvent() {
        fsmmr.emitSingleCorrectMessage();
        assertTrue(newDevice);
        newDevice = false;

        fsmmr.emitSingleCorrentMessageDifferentIP();
        assertTrue(updateDevice && !newDevice);
        updateDevice = false;

        // Check if the event is not fired again
        fsmmr.emitSingleCorrentMessageDifferentIP();
        assertFalse(updateDevice || newDevice);
    }

}
