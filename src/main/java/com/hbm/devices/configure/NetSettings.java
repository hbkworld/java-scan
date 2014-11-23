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

package com.hbm.devices.configure;

import com.google.gson.annotations.SerializedName;
import com.hbm.devices.scan.messages.MissingDataException;
import com.hbm.devices.scan.messages.DefaultGateway;

/**
 * This class stores the network properties which are send in a configuration request
 * 
 * @since 1.0
 *
 */
public class NetSettings {

    private DefaultGateway defaultGateway;

    @SerializedName("interface")
    private Interface iface;

    private NetSettings() {
    }

    /**
     * This constructor is used to instantiate a {@link NetSettings} object. The default Gateway is
     * not changed.
     * 
     * @param iface
     *            the interface settings
     */
    public NetSettings(Interface iface) {
        this(iface, null);
    }

    /**
     * This constructor is used to instantiate a {@link NetSettings} object.
     * 
     * @param iface
     *            the interface settings
     * @param defaultGateway
     *            the new defaultGateway
     */
    public NetSettings(Interface iface, DefaultGateway defaultGateway) {
        this();
        this.iface = iface;
        this.defaultGateway = defaultGateway;
    }

    /**
     * 
     * @return returns the default gateway settings
     */
    public DefaultGateway getDefaultGateway() {
        return defaultGateway;
    }

    /**
     * 
     * @return returns the interface settings
     */
    public Interface getInterface() {
        return iface;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Network settings:\n");
        if (defaultGateway != null) {
            sb.append("\t defaultGateway: " + defaultGateway + "\n");
        }
        if (iface != null) {
            sb.append("\t interface: \n" + iface + "\n");
        }

        return sb.toString();
    }

    /**
     * This method checks the {@link NetSettings} object for errors and if it conforms to the HBM
     * network discovery and configuration protocol.
     * 
     * @param settings
     *          the {@link NetSettings} object, which should be checked for errors
     * @throws MissingDataException
     *          if some information required by the specification is missing in {@code settings}.
     * @throws NullPointerException
     *          if {@code settings} is null.
     */
    public static void checkForErrors(NetSettings settings) throws MissingDataException {
        if (settings == null) {
            throw new NullPointerException("settings object must not be null");
        }

        if (settings.iface == null) {
            throw new MissingDataException("No interface in NetSettings");
        }
        Interface.checkForErrors(settings.iface);
    }
}
