package com.hbm.devices.scan.messages;

import com.google.gson.annotations.SerializedName;

public class NetSettings {

	private NetSettings() {
	}
	
	public NetSettings(Interface<?, ?> iface) {
	    this();
	    this.iface = iface;
	}

	public DefaultGateway getDefaultGateway() {
		return defaultGateway;
	}
	
	public Interface<?, ?> getInterface() {
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
	private Interface<?, ?> iface;
}

