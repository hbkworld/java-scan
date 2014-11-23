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

package com.hbm.devices.scan;

import java.io.IOException;

/**
 * Convenience class to receive response multicast messages.
 * <p>
 *
 * @since 1.0
 */
public class ResponseReceiver extends MulticastMessageReceiver {

    public ResponseReceiver() throws IOException {
        super(ScanConstants.CONFIGURATION_ADDRESS, ScanConstants.CONFIGURATION_PORT);
    }
}
