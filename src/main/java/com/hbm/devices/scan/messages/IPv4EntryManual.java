package com.hbm.devices.scan.messages;

import com.hbm.devices.scan.MissingDataException;

public class IPv4EntryManual {

    private String manualAddress;
    private String manualNetmask;

    protected IPv4EntryManual() {
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

    public static void checkForErrors(IPv4EntryManual ip) throws MissingDataException,
            NullPointerException {
        if (ip == null) {
            throw new NullPointerException("ip object must not be null");
        }

        // TODO: hier weiß ich nicht genau ob ipaddress & netmask vorhanden sein muss, oder nur
        // (mind.) eins
    }

}
