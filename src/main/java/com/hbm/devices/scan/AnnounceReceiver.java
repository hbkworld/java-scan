package com.hbm.devices.scan;

import com.hbm.devices.scan.ScanConstants;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Convenience class to receive announce multicast messages.
 *
 * @since 1.0
 */
public class AnnounceReceiver extends MulticastMessageReceiver {

	public AnnounceReceiver() throws UnknownHostException, SocketException, IOException {
		super(ScanConstants.SCAN_ADDRESS, ScanConstants.SCAN_PORT);
	}
}
