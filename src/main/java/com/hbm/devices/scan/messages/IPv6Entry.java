package com.hbm.devices.scan.messages;

public class IPv6Entry {

	private IPv6Entry() {
	}

	public String getAddress() {
		return address;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public String toString() {
		return address + "/" + prefix;
	}
	
	private String address;
	private String prefix;
}
