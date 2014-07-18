package com.hbm.devices.scan.messages;

import java.util.LinkedList;

/**
 * The interface describes the properties of an network interface.
 */
public class Interface<T_IPV4, T_IPV6> {

	private Interface() {
	}
	
	/**
	 * @param name     A string containing the interface name that should be configured. 
	 *                 The interface name must be gathered from an announce datagram.
	 * @param method   A string enumeration describing how the network settings configured on
	 *                 the device during the startup. Currently the values *manual*, *dhcp* 
	 *                 and *RouterSolicitation* are valid.
	 * @param ipv4
	 * @param ipv6
	 */
//	public Interface(String name, Method method, LinkedList<IPv4Entry> ipv4, LinkedList<IPv6Entry> ipv6) {
//	    this();
//	    this.name = name;
//	    this.configurationMethod = method.name();
//	    this.ipv4 = ipv4;
//	    this.ipv6 = ipv6;
//	}
	public Interface(String name, Method method, T_IPV4 ipv4, T_IPV6 ipv6) {
	    this();
	    this.name = name;
	    this.configurationMethod = method.name();
	    this.ipv4 = ipv4;
	    this.ipv6 = ipv6;
	}	
	/**
	 * @return     A string containing the name of the interface. For embedded Linux systems
	 *             typically something like eth0, eth1, ... .
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return     An optional string containing the type of the interface. For QuantumX systems it 
	 *             might be useful to distinguish Ethernet and Firewire interfaces.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return     An optional string containing some additional information. 
	 *             QuantumX devices report whether the interface is on the front or back side.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return     A string enumeration describing how the network settings configured on the device 
	 *             during the startup. Currently the values *manual*, *dhcp* and 
	 *             *RouterSolicitation* are valid.
	 */
	public String getConfigurationMethod() {
		return configurationMethod;
	}

	/**
	 * @return     An array containig all IPv4 addresses of the interface with their netmask.
	 */
	public T_IPV4 getIPv4() {
		return ipv4;
	}

	/**
	 * @return     An array containig all IPv6 addresses of the interface with their prefix.
	 */
	public T_IPV6 getIPv6() {
		return ipv6;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Interface:");
		if (name != null)
			sb.append("\n\t\tname: " + name);
		if (type != null)
			sb.append("\n\t\ttype: " + type);
		if (description != null)
			sb.append("\n\t\tdescription: " + description);
		if (configurationMethod != null)
			sb.append("\n\t\tconfigurationMethod: " + configurationMethod);
		if (ipv4 != null) {
			sb.append("\n\t\tIPv4 addresses:");
			if(ipv4 instanceof LinkedList<?>) {
    			for (Object e : (LinkedList<?>)ipv4) {
    				sb.append("\n\t\t\t" + e);
    			}
			} else {
			    sb.append("\n\t\t\t" + ipv4);
			}
		}
		if (ipv6 != null) {
			sb.append("\n\t\tIPv6 addresses:");
			if(ipv6 instanceof LinkedList<?>) {
    			for (Object e : (LinkedList<?>)ipv6) {
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
//	private LinkedList<IPv4Entry> ipv4;
//	private LinkedList<IPv6Entry> ipv6;
	private T_IPV4 ipv4;
	private T_IPV6 ipv6;
	
	public enum Method {
	    manual,
	    dhcp,
	    RouterSolicitation;
	}
}
