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

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.DeviceMonitor;

/**
 * This event is emitted by an {@link DeviceMonitor}.
 * <p>
 * The event is notified when an announce method from a device that is known, but has changed some
 * data in the announce message, is received. The event contains the old and the new announce
 * information stored in a {@link CommunicationPath}
 *
 * @since 1.0
 */
public class UpdateDeviceEvent {

    private CommunicationPath oldCommunicationPath;
    private CommunicationPath newCommunicationPath;

    public UpdateDeviceEvent(CommunicationPath oldPath, CommunicationPath newPath) {
        this.oldCommunicationPath = oldPath;
        this.newCommunicationPath = newPath;
    }

    public CommunicationPath getOldCommunicationPath() {
        return this.oldCommunicationPath;
    }

    public CommunicationPath getNewCommunicationPath() {
        return this.newCommunicationPath;
    }

}
