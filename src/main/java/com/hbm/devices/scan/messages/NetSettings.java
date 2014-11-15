package com.hbm.devices.scan.messages;

import java.util.LinkedList;

import com.google.gson.annotations.SerializedName;

public class NetSettings {

	private NetSettings() {
	}
	
	public NetSettings(Interface<LinkedList<IPv4Entry>, LinkedList<IPv6Entry>> iface) {
	    this();
	    this.iface = iface;
	}

	public DefaultGateway getDefaultGateway() {
		return defaultGateway;
	}
	
	public Interface<LinkedList<IPv4Entry>, LinkedList<IPv6Entry>> getInterface() {
		return iface;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Network settings:\n");
		if (defaultGateway != null)
			sb.append("\t" + defaultGateway + "\n");
		if (iface != null)
			sb.append("\t" + iface + "\n");

		return sb.toString();
	}

	private DefaultGateway defaultGateway;

	@SerializedName("interface")
	private Interface<LinkedList<IPv4Entry>, LinkedList<IPv6Entry>> iface;
}

