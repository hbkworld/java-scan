package com.hbm.devices.scan.messages;

/**
 *  The default gateway describes the configured default gateway of a device. Only a single default gateway is possible.
 */
public class DefaultGateway {

	private DefaultGateway() {
	}

	public DefaultGateway(String ipv4Address, String ipv6Address) {
	    this();
	    this.ipv4Address = ipv4Address;
	    this.ipv6Address = ipv6Address;
	}
    /**
     * @return  A string containing the IPv4 address of the configured default gateway.
     */	
	public String getIpv4Address() {
		return ipv4Address;
	}

	/**
	 * @return     A string containing the IPv6 address of the configured default gateway.
	 */
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
