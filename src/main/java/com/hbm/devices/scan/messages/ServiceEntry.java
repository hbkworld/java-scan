package com.hbm.devices.scan.messages;

public class ServiceEntry {

	private ServiceEntry() {
	}

	public String getType() {
		return type;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "type: " + type + " port: " + port;
	}

	private String type;
	private int port;

}

