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
 * This event is emitted by an {@link DeviceMonitor} when an annouce
 * messages wasn't refreshed during the expiration time.
 * <p>
 *
 * @since 1.0
 */

public class LostDeviceEvent {

    private CommunicationPath communicationPath;

    public LostDeviceEvent(CommunicationPath ap) {
        communicationPath = ap;
    }

    public CommunicationPath getAnnouncePath() {
        return communicationPath;
    }
}
