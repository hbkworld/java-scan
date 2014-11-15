package com.hbm.devices.scan.messages;

public class IPv4Entry {
    
    private String address;
    private String netmask;

    protected IPv4Entry() {
    }
        
    public String getAddress() {
        return address;
    }

    public String getNetmask() {
        return netmask;
    }

    @Override
    public String toString() {
        return address + "/" + netmask;
    }
}
