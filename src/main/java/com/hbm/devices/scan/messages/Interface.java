package com.hbm.devices.scan.messages;

import java.util.List;

/**
 * The interface describes the properties of an network interface.
 * 
 * @since 1.0
 */
public class Interface {

	private Interface() {
	}

	/**
	 * @param name
	 *            A string containing the interface name that should be configured. The interface
	 *            name must be gathered from an announce datagram.
	 * @param method
	 *            A string enumeration describing how the network settings configured on the device
	 *            during the startup. Currently the values *manual*, *dhcp* and *RouterSolicitation*
	 *            are valid.
	 * @param ipv4
	 *           A List containing all valid IPv4 addresses for the interface.
	 * @param ipv6
	 *           A List containing all valid IPv6 addresses for the interface.
	 */
	public Interface(String name, Method method, List<IPv4Entry> ipv4, List<IPv6Entry> ipv6) {
		this();
		this.name = name;
		this.configurationMethod = method.toString();
		this.ipv4 = ipv4;
		this.ipv6 = ipv6;
	}

	/**
	 * @return A string containing the name of the interface. For embedded Linux systems typically
	 *         something like eth0, eth1, ... .
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return An optional string containing the type of the interface. For QuantumX systems it
	 *         might be useful to distinguish Ethernet and Firewire interfaces.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return An optional string containing some additional information. QuantumX devices report
	 *         whether the interface is on the front or back side.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return A string enumeration describing how the network settings configured on the device
	 *         during the startup. Currently the values *manual*, *dhcp* and *RouterSolicitation*
	 *         are valid.
	 */
	public String getConfigurationMethod() {
		return configurationMethod;
	}

	/**
	 * @return An array containig all IPv4 addresses of the interface with their netmask.
	 */
	public List<IPv4Entry> getIPv4() {
		return ipv4;
	}

	/**
	 * @return An array containig all IPv6 addresses of the interface with their prefix.
	 */
	public List<IPv6Entry> getIPv6() {
		return ipv6;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Interface:");
		if (name != null) {
			sb.append("\n\t\tname: " + name);
		}
		if (type != null) {
			sb.append("\n\t\ttype: " + type);
		}
		if (description != null) {
			sb.append("\n\t\tdescription: " + description);
		}
		if (configurationMethod != null) {
			sb.append("\n\t\tconfigurationMethod: " + configurationMethod);
		}
		if (ipv4 != null) {
			sb.append("\n\t\tIPv4 addresses:");
			if (ipv4 instanceof List<?>) {
				for (Object e : (List<?>) ipv4) {
					sb.append("\n\t\t\t" + e);
				}
			} else {
				sb.append("\n\t\t\t" + ipv4);
			}
		}
		if (ipv6 != null) {
			sb.append("\n\t\tIPv6 addresses:");
			if (ipv6 instanceof List<?>) {
				for (Object e : (List<?>) ipv6) {
					sb.append("\n\t\t\t" + e);
				}
			} else {
				sb.append("\n\t\t\t" + ipv6);
			}
		}
		return sb.toString();
	}

	private String name;
	private String type;
	private String description;
	private String configurationMethod;
	private List<IPv4Entry> ipv4;
	private List<IPv6Entry> ipv6;

	public enum Method {
		MANUAL {
			@Override
		    public String toString() {
				return "manual";
			}
		},
		DHCP {
			@Override
		    public String toString() {
				return "dhcp";
			}
		},
		ROUTER_SOLICITATION {
			@Override
		    public String toString() {
				return "routerSolicitation";
			}
		};

		public static Method fromString(String s) {
			if (s.equals("manual")) {
				return MANUAL;
			} else if (s.equals("dhcp")) {
				return DHCP;
			} else if (s.equals("routerSolicitation")) {
				return ROUTER_SOLICITATION;
			} else {
				return null;
			}
		}
	}
}
