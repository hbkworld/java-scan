/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Stephan Gatzka
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        StringBuilder sb = new StringBuilder("Network settings:\n");
        if (defaultGateway != null) {
            sb.append("\t defaultGateway: ").append(defaultGateway).append('\n');
        }
        if (iface != null) {
            sb.append("\t interface: \n").append(iface).append('\n');
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
     * @throws IllegalArgumentException
     *          if {@code settings} is null.
     */
    public static void checkForErrors(NetSettings settings) throws MissingDataException {
        if (settings == null) {
            throw new IllegalArgumentException("settings object must not be null");
        }

        if (settings.iface == null) {
            throw new MissingDataException("No interface in NetSettings");
        }
        Interface.checkForErrors(settings.iface);
    }
}
