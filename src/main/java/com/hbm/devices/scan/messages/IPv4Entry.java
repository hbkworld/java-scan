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
