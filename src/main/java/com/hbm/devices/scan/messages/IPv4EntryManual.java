package com.hbm.devices.scan.messages;

public class IPv4EntryManual {

	private IPv4EntryManual() {
	}
	
	public IPv4EntryManual(String address, String netmask) {
	    this();
	    this.manualAddress = address;
	    this.manualNetmask = netmask;
	}
		
    public String getAddress() {
		return manualAddress;
	}

	public String getNetmask() {
		return manualNetmask;
	}

	@Override
	public String toString() {
		return manualAddress + "/" + manualNetmask;
	}
	
	private String manualAddress;
	private String manualNetmask;

}
