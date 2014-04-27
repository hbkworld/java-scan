package com.hbm.devices.scan;

import com.hbm.devices.scan.ScanConstants;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AnnounceReceiver extends StringMessageMulticastReceiver {

	public AnnounceReceiver() throws UnknownHostException, SocketException, IOException {
		super(ScanConstants.SCAN_ADDRESS, ScanConstants.SCAN_PORT);
	}
}
