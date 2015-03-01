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

/**
 * This class finds the IP addresses of an announce messages the
 * receiving device is able to connect to.
 */
package com.hbm.devices.scan.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.MissingDataException;

/**
 * Convenience class for checking if an IP connection is possible to an announced device.
 * 
 * @since 1.0
 */
public final class ConnectionFinder {

    private final boolean preferIPv6;
    private final IPv4ConnectionFinder ipv4ConnectionFinder;
    private final IPv6ConnectionFinder ipv6ConnectionFinder;

    /**
     * Constructs a new {@link ConnectionFinder} object.
     *
     * @param interfaces A {@link Collection} of {@link
     * NetworkInterface}s used to check {@link Announce} objects against
     * in {@link #getConnectableAddress(Announce)}.
     *
     * @param preferIPv6 Set true if {@link #getConnectableAddress}
     * returns IPv6 addresses preferentially.
     */
    public ConnectionFinder(Collection<NetworkInterface> interfaces, boolean preferIPv6) {
        this.preferIPv6 = preferIPv6;

        final List<NetworkInterfaceAddress> ipv4AddressList = new LinkedList<NetworkInterfaceAddress>();
        final List<NetworkInterfaceAddress> ipv6AddressList = new LinkedList<NetworkInterfaceAddress>();

        for (final NetworkInterface iface : interfaces) {
            final List<InterfaceAddress> niAddresses = iface.getInterfaceAddresses();
            for (final InterfaceAddress niAddress : niAddresses) {
                final InetAddress interfaceAddress = niAddress.getAddress();
                final NetworkInterfaceAddress address = new NetworkInterfaceAddress(interfaceAddress.getHostAddress(), niAddress.getNetworkPrefixLength());
                if (interfaceAddress instanceof Inet4Address) {
                    ipv4AddressList.add(address);
                } else {
                    ipv6AddressList.add(address);
                }
            }
        }
        this.ipv4ConnectionFinder = new IPv4ConnectionFinder(ipv4AddressList);
        this.ipv6ConnectionFinder = new IPv6ConnectionFinder(ipv6AddressList);
    }

    /**
     * This method looks for a connectable IP address.
     *
     * @param announce The {@link Announce} containing the device we
     * want to communicate with.
     *
     * @return An {@link InetAddress} if a connectable IP address was
     * found, null otherwise.
     *
     * @throws MissingDataException if  the Announce object doesn't
     * contain any IP addresses.
     */
    public InetAddress getConnectableAddress(Announce announce) throws MissingDataException {
        if (preferIPv6) {
            InetAddress address = ipv6ConnectionFinder.getConnectableAddress(announce);
            if (address == null) {
                address = ipv4ConnectionFinder.getConnectableAddress(announce);
            }
            return address;
        } else {
            InetAddress address = ipv4ConnectionFinder.getConnectableAddress(announce);
            if (address == null) {
                address = ipv6ConnectionFinder.getConnectableAddress(announce);
            }
            return address;
        }
    }
}

class NetworkInterfaceAddress {
    String address;
    int prefix;

    NetworkInterfaceAddress(String address, int prefix) {
        this.address = address;
        this.prefix = prefix;
    }
}
