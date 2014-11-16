package com.hbm.devices.scan;

/**
 * Convenience class to receive response multicast messages.
 * <p>
 *
 * @since 1.0
 */
public class ScanConstants {

    public static final String RESPONSE_ADDRESS = "239.255.77.77";
    public static final int RESPONSE_PORT = 31417;

    public static final String ANNOUNCE_ADDRESS = "239.255.77.76";
    public static final int ANNOUNCE_PORT = 31416;

    public static final String LOGGER_NAME = "scan";

    private ScanConstants() {
    }
}
