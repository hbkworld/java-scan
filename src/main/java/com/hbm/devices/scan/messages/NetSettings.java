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

import com.google.gson.annotations.SerializedName;

public class NetSettings {

    private DefaultGateway defaultGateway;

    @SerializedName("interface")
    private Interface iface;

    private NetSettings() {
    }
    
    public NetSettings(Interface iface) {
        this.iface = iface;
    }

    public DefaultGateway getDefaultGateway() {
        return defaultGateway;
    }
    
    public Interface getInterface() throws MissingDataException {
        if (iface == null) {
            throw new MissingDataException("No interface section in netSettings!");
        }
        return iface;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Network settings:\n");
        if (defaultGateway != null) {
            sb.append('\t').append(defaultGateway).append('\n');
        }
        if (iface != null) {
            sb.append('\t').append(iface).append('\n');
        }

        return sb.toString();
    }
}

