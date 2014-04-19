/**
 * This class finds the IP addresses of an announce messages the
 * receiving device is able to connect to.
 */
package com.hbm.devices.scan.util;

import com.hbm.devices.scan.messages.Announce;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;

public class ConnectionFinder {
	
	private boolean preferIPv6;
	private IPv4ConnectionFinder ipv4ConnectionFinder;
	private IPv6ConnectionFinder ipv6ConnectionFinder;

	public ConnectionFinder(Announce announce, boolean preferIPv6) {
		this.preferIPv6 = preferIPv6;
		this.ipv4ConnectionFinder = new IPv4ConnectionFinder(announce);
		this.ipv6ConnectionFinder = new IPv6ConnectionFinder(announce);
	}

	public InetAddress getConnectableAddress(Collection<NetworkInterface> interfaces) {
		if (preferIPv6) {
			InetAddress address = ipv6ConnectionFinder.getConnectableAddress(interfaces);
			if (address != null) {
				return address;
			} else {
				return ipv4ConnectionFinder.getConnectableAddress(interfaces);
			}
		} else {
			InetAddress address = ipv4ConnectionFinder.getConnectableAddress(interfaces);
			if (address != null) {
				return address;
			} else {
				return ipv6ConnectionFinder.getConnectableAddress(interfaces);
			}
		}
	}
}
