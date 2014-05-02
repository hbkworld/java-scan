package com.hbm.devices.scan;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Convenience class to receive announce multicast messages.
 * <p>
 *
 * @since 1.0
 */
public class AnnounceReceiver extends MulticastMessageReceiver {

	public static final String ANNOUNCE_ADDRESS = "239.255.77.76";
	public static final int ANNOUNCE_PORT = 31416;

	public AnnounceReceiver() throws UnknownHostException, SocketException, IOException {
		super(ANNOUNCE_ADDRESS, ANNOUNCE_PORT);
	}
}
