package com.hbm.devices.scan.messages;

import java.util.Iterator;
import java.util.LinkedList;

public class Interface {

	private Interface() {
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getConfigurationMethod() {
		return configurationMethod;
	}

	public Iterable<IPv4Entry> getIPv4() {
		return ipv4;
	}

	public Iterable<IPv6Entry> getIPv6() {
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
			Iterator<IPv4Entry> i = ipv4.iterator();
			while (i.hasNext()) {
				IPv4Entry e = i.next();
				sb.append("\n\t\t\t" + e);
			}
		}
		if (ipv6 != null) {
			sb.append("\n\t\tIPv6 addresses:");
			Iterator<IPv6Entry> i = ipv6.iterator();
			while (i.hasNext()) {
				IPv6Entry e = i.next();
				sb.append("\n\t\t\t" + e);
			}
		}
		return sb.toString();
	}

	private String name;
	private String type;
	private String description;
	private String configurationMethod;
	private LinkedList<IPv4Entry> ipv4;
	private LinkedList<IPv6Entry> ipv6;
}
