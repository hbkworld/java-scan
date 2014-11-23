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

package com.hbm.devices.scan.events;

import com.hbm.devices.scan.messages.CommunicationPath;
import com.hbm.devices.scan.DeviceMonitor;

/**
 * This event is emitted by an {@link DeviceMonitor}.
 * <p>
 * The event is notified when an announce method from a device that is unknown upto now is received.
 * This event is not fired when the device was already announced but some data in the announce
 * message has changed. In this case {@link UpdateDeviceEvent} is fired.
 *
 * @since 1.0
 */
public class NewDeviceEvent {

    private CommunicationPath communicationPath;

    public NewDeviceEvent(CommunicationPath ap) {
        communicationPath = ap;
    }

    public CommunicationPath getAnnouncePath() {
        return communicationPath;
    }
}
