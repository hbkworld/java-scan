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

package com.hbm.devices.scan.messages;

import java.util.List;

/**
 * Objects of this class hold the inforamtion describing the properties
 * of an network interface.
 * 
 * @since 1.0
 */
public final class Interface {

    private String name;
    private String type;
    private String description;
    private String configurationMethod;
    private List<IPv4Entry> ipv4;
    private List<IPv6Entry> ipv6;

    private Interface() {
    }

    /**
     * @return
     *      A string containing the name of the interface. For
     *      Linux systems typically something like eth0,
     *      eth1, ... .
     * @throws MissingDataException
     *      if no name is set
     */
    public String getName() throws MissingDataException {
        if (name == null) {
            throw new MissingDataException("Interface has no name!");
        }
        return name;
    }

    /**
     * @return
     *      An optional string containing the type of the interface.
     *      For QuantumX systems it might be useful to distinguish
     *      Ethernet and Firewire interfaces.
     */
    public String getType() {
        return type;
    }

    /**
     * @return
     *      An optional string containing some additional
     *      information. QuantumX devices report whether the
     *      interface is on the front or back side.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return
     *      A string enumeration describing how the network settings
     *      configured on the device during the startup. Currently
     *      the values *manual*, *dhcp* and *RouterSolicitation* are
     *      valid.
     * @throws MissingDataException
     *      if no configuration method is set
     */
    public String getConfigurationMethod() throws MissingDataException {
        if (configurationMethod == null) {
            throw new MissingDataException("No configuration method set in interface section!");
        }
        return configurationMethod;
    }

    /**
     * @return
     *      An array containig all IPv4 addresses of the interface
     *      with their netmask.
     */
    public List<IPv4Entry> getIPv4() {
        return ipv4;
    }

    /**
     * @return
     *      An array containig all IPv6 addresses of the interface
     *      with their prefix.
     */
    public List<IPv6Entry> getIPv6() {
        return ipv6;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Interface:");
        if (name != null) {
            builder.append("\n\t\tname: ").append(name);
        }
        if (type != null) {
            builder.append("\n\t\ttype: ").append(type);
        }
        if (description != null) {
            builder.append("\n\t\tdescription: ").append(description);
        }
        if (configurationMethod != null) {
            builder.append("\n\t\tconfigurationMethod: ").append(configurationMethod);
        }
        if (ipv4 != null) {
            builder.append("\n\t\tIPv4 addresses:");
            for (final Object e : ipv4) {
                builder.append("\n\t\t\t").append(e);
            }
        }
        if (ipv6 != null) {
            builder.append("\n\t\tIPv6 addresses:");
            for (final Object e : ipv6) {
                builder.append("\n\t\t\t").append(e);
            }
        }
        return builder.toString();
    }

    /**
     * Network interface configuration methods that can be used.
     */
    public enum Method {
        MANUAL {
            @Override
            public String toString() {
                return "manual";
            }
        },
        DHCP {
            @Override
            public String toString() {
                return "dhcp";
            }
        },
        ROUTER_SOLICITATION {
            @Override
            public String toString() {
                return "routerSolicitation";
            }
        };
    }
}
