package com.hbm.devices.scan;

import java.io.IOException;

/**
 * Convenience class to receive announce multicast messages.
 * <p>
 *
 * @since 1.0
 */
public class AnnounceReceiver extends MulticastMessageReceiver {

    public AnnounceReceiver() throws IOException {
        super(ScanConstants.ANNOUNCE_ADDRESS, ScanConstants.ANNOUNCE_PORT);
    }
}
