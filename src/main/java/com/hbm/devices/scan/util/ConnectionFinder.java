/**
 * This class finds the IP addresses of an announce messages the
 * receiving device is able to connect to.
 */
package com.hbm.devices.scan.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;

import com.hbm.devices.scan.messages.Announce;

/**
 * Convenience class for checking if an IP connection is possible to an announced device.
 * 
 * @since 1.0
 */
public class ConnectionFinder {

	private boolean preferIPv6;
	private IPv4ConnectionFinder ipv4ConnectionFinder;
	private IPv6ConnectionFinder ipv6ConnectionFinder;

	public ConnectionFinder(Collection<NetworkInterface> interfaces, boolean preferIPv6) {
		this.preferIPv6 = preferIPv6;
		this.ipv4ConnectionFinder = new IPv4ConnectionFinder(interfaces);
		this.ipv6ConnectionFinder = new IPv6ConnectionFinder(interfaces);
	}

	public InetAddress getConnectableAddress(Announce announce) {
		if (preferIPv6) {
			InetAddress address = ipv6ConnectionFinder.getConnectableAddress(announce);
			if (address != null) {
				return address;
			} else {
				return ipv4ConnectionFinder.getConnectableAddress(announce);
			}
		} else {
			InetAddress address = ipv4ConnectionFinder.getConnectableAddress(announce);
			if (address != null) {
				return address;
			} else {
				return ipv6ConnectionFinder.getConnectableAddress(announce);
			}
		}
	}
}
