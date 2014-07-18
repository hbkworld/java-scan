package com.hbm.devices.scan.messages;

public class IPv6Entry {

	private IPv6Entry() {
	}

	public IPv6Entry(String address, String prefix) {
	    this();
	    this.address = address;
	    this.prefix = prefix;
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
