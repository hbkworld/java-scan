package com.hbm.devices.scan;

import java.io.IOException;

/**
 * Convenience class to receive announce multicast messages.
 * <p>
 *
 * @since 1.0
 */
public class AnnounceReceiver extends MulticastMessageReceiver {

    public static final String ANNOUNCE_ADDRESS = "239.255.77.76";
    public static final int ANNOUNCE_PORT = 31416;

    public AnnounceReceiver() throws IOException {
        super(ANNOUNCE_ADDRESS, ANNOUNCE_PORT);
    }
}
