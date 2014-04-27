package com.hbm.devices.scan.messages;

public class DefaultGateway {

	private DefaultGateway() {
	}

	public String getIpv4Address() {
		return ipv4Address;
	}

	public String getIpv6Address() {
		return ipv6Address;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Gateway: ");
		if (ipv4Address != null)
			sb.append("IPv4: " + ipv4Address);
		if (ipv6Address != null)
			sb.append("IPv6: " + ipv6Address);
		return sb.toString();
	}

	private String ipv4Address;
	private String ipv6Address;

}
