package com.hbm.devices.scan;

import java.io.IOException;

/**
 * Convenience class to receive response multicast messages.
 * <p>
 *
 * @since 1.0
 */
public class ResponseReceiver extends MulticastMessageReceiver {

	public static final String RESPONSE_ADDRESS = "239.255.77.77";
	public static final int RESPONSE_PORT = 31417;

	public ResponseReceiver() throws IOException {
		super(RESPONSE_ADDRESS, RESPONSE_PORT);
	}
}
