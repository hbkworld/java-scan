package com.hbm.devices.scan;

import java.io.IOException;
import java.util.Properties;

/**
 * Convenience class to receive response multicast messages.
 * <p>
 *
 * @since 1.0
 */
public class ScanConstants {

    public static final String RESPONSE_ADDRESS;
    public static final int RESPONSE_PORT = 31417;

    public static final String ANNOUNCE_ADDRESS;
    public static final int ANNOUNCE_PORT = 31416;

    public static final String LOGGER_NAME = "scan";

    private ScanConstants() {
    }

    static {
        try {
            Properties props = new Properties();
            props.load(ClassLoader.getSystemResourceAsStream("scan.properties"));
            RESPONSE_ADDRESS = props.getProperty("scan.announce.address");
            ANNOUNCE_ADDRESS = props.getProperty("scan.configure.address");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
