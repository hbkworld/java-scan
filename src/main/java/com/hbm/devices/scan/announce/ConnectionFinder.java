/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Hottinger Baldwin Messtechnik GmbH
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
package com.hbm.devices.scan.announce;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Convenience class for checking if an IP connection is possible to an announced device.
 * 
 * @since 1.0
 */
public final class ConnectionFinder {

    private final IPv4ConnectionFinder ipv4ConnectionFinder;
    private final IPv6ConnectionFinder ipv6ConnectionFinder;

    /**
     * Constructs a new {@link ConnectionFinder} object.
     *
     * @param interfaces A {@link Collection} of {@link
     * NetworkInterface}s used to check {@link Announce} objects against
     * in {@link #getConnectableAddresses(Announce)}.
     *
     */
    public ConnectionFinder(Collection<NetworkInterface> interfaces) {

        final List<NetworkInterfaceAddress> ipv4AddressList = new LinkedList<NetworkInterfaceAddress>();
        final List<NetworkInterfaceAddress> ipv6AddressList = new LinkedList<NetworkInterfaceAddress>();

        for (final NetworkInterface iface : interfaces) {
            final List<InterfaceAddress> niAddresses = iface.getInterfaceAddresses();
            for (final InterfaceAddress niAddress : niAddresses) {
                final InetAddress interfaceAddress = niAddress.getAddress();
                final NetworkInterfaceAddress address = new NetworkInterfaceAddress(interfaceAddress, niAddress.getNetworkPrefixLength());
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

    ConnectionFinder(Collection<NetworkInterfaceAddress> ipv4List, Collection<NetworkInterfaceAddress> ipv6List) {
        this.ipv4ConnectionFinder = new IPv4ConnectionFinder(ipv4List);
        this.ipv6ConnectionFinder = new IPv6ConnectionFinder(ipv6List);
    }

    /**
     * This method looks for a connectable IP address.
     *
     * Please note, that this method might give
     * you false positives. Consider you have two ethernet interfaces:
     * eth0 with IP address 172.19.1.2 and eth1 with IP address
     * 10.1.2.3. Now you get an announce packet via eth0 from a device
     * with IP address 10.1.2.4. This method now wrongly says that the
     * device is connectable.
     *
     * This limitation comes from the Java-API by not providing the
     * information over which network interface a multicast UDP packet was
     * received.
     *
     * @param announce The {@link Announce} containing the device we
     * want to communicate with.
     *
     * @return An {@link InetAddress} if a connectable IP address was
     * found, null otherwise.
     */
    public List<InetAddress> getConnectableAddresses(Announce announce) {
        final List<InetAddress> list = ipv4ConnectionFinder.getConnectableAddresses(announce);
        list.addAll(ipv6ConnectionFinder.getConnectableAddresses(announce));
        return list;
    }
}

class NetworkInterfaceAddress {
    private final InetAddress address;
    private final int prefix;

    NetworkInterfaceAddress(InetAddress address, int prefix) {
        this.address = address;
        this.prefix = prefix;
    }

    InetAddress getAddress() {
        return address;
    }

    int getPrefix() {
        return prefix;
    }
}
