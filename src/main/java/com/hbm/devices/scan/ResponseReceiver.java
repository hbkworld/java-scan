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
