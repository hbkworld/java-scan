/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
 */

package com.hbm.devices.scan.messages;

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

    public static void checkForErrors(IPv4EntryManual ip) throws MissingDataException {
        if (ip == null) {
            throw new NullPointerException("ip object must not be null");
        }

        // TODO: hier weiß ich nicht genau ob ipaddress & netmask vorhanden sein muss, oder nur
        // (mind.) eins
    }

}
