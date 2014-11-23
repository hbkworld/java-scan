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
import java.util.Properties;






import java.io.InputStream;




/**
 * Convenience class to receive response multicast messages.
 * <p>
 *
 * @since 1.0
 */
public class ScanConstants {

    public static final String ANNOUNCE_ADDRESS;
    public static final int ANNOUNCE_PORT;

    public static final String CONFIGURATION_ADDRESS;
    public static final int CONFIGURATION_PORT;

    public static final String LOGGER_NAME = "scan";

    private ScanConstants() {
    }

    static {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("scan.properties");
            Properties props = new Properties();
            props.load(is);

            ANNOUNCE_ADDRESS = props.getProperty("scan.announce.address");
            ANNOUNCE_PORT = Integer.parseInt(props.getProperty("scan.announce.port"));
            CONFIGURATION_ADDRESS = props.getProperty("scan.configure.address");
            CONFIGURATION_PORT = Integer.parseInt(props.getProperty("scan.configure.port"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
