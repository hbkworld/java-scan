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

    public static final String ANNOUNCE_ADDRESS;
    public static final int ANNOUNCE_PORT;

    public static final String RESPONSE_ADDRESS;
    public static final int RESPONSE_PORT;

    public static final String LOGGER_NAME = "scan";

    private ScanConstants() {
    }

    static {
        try {
            Properties props = new Properties();
            props.load(ClassLoader.getSystemResourceAsStream("scan.properties"));
            ANNOUNCE_ADDRESS = props.getProperty("scan.announce.address");
            ANNOUNCE_PORT = Integer.parseInt(props.getProperty("scan.announce.port"));
            RESPONSE_ADDRESS = props.getProperty("scan.configure.address");
            RESPONSE_PORT = Integer.parseInt(props.getProperty("scan.configure.port"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
