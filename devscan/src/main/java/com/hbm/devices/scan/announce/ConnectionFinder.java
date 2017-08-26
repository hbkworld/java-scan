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
import java.net.Inet6Address;
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

    private final Collection<NetworkInterfaceAddress> ipv4AddressList;
    private final Collection<NetworkInterfaceAddress> ipv6AddressList;
    /**
     * Constructs a new {@link ConnectionFinder} object.
     *
     * @param interfaces A {@link Collection} of {@link
     * NetworkInterface}s used to check {@link Announce} objects against
     * in {@link #getSameNetworkAddresses(Announce)}.
     *
     */
    public ConnectionFinder(Collection<NetworkInterface> interfaces) {
        this.ipv4AddressList = new LinkedList<>();
        this.ipv6AddressList = new LinkedList<>();
        
        for (final NetworkInterface iface : interfaces) {
            final List<InterfaceAddress> niAddresses = iface.getInterfaceAddresses();
            for (final InterfaceAddress niAddress : niAddresses) {
                final InetAddress interfaceAddress = niAddress.getAddress();
                final NetworkInterfaceAddress address =
                    new NetworkInterfaceAddress(interfaceAddress, niAddress.getNetworkPrefixLength());
                if (interfaceAddress instanceof Inet4Address) {
                    ipv4AddressList.add(address);
                } else {
                    ipv6AddressList.add(address);
                }
            }
        }
    }

    ConnectionFinder(Collection<NetworkInterfaceAddress> ipv4List, Collection<NetworkInterfaceAddress> ipv6List) {
        this.ipv4AddressList = ipv4List;
        this.ipv6AddressList = ipv6List;
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
    public List<InetAddress> getSameNetworkAddresses(Announce announce) {
        final Iterable<IPEntry> announceAddresses = announce.getParams().getNetSettings()
            .getInterface().getIPList();
        final List<InetAddress> list = new LinkedList<>();
        
        for (final Object ipEntry : announceAddresses) {
            IPEntry entry = (IPEntry) ipEntry;
            final InetAddress announceAddress = entry.getAddress();
            final int announcePrefix = entry.getPrefix();
            if ((announceAddress instanceof Inet4Address)) {
                for (final NetworkInterfaceAddress iface : this.ipv4AddressList) {
                    final InetAddress ifaceAddress = iface.getAddress();
                    final int ifaceAddressPrefix = iface.getPrefix();
                    if (sameIPv4Net(announceAddress, announcePrefix, ifaceAddress, ifaceAddressPrefix)) {
                        list.add(announceAddress);
                    } 
                }
            } else if (announceAddress instanceof Inet6Address) {
                for (final NetworkInterfaceAddress iface : this.ipv6AddressList) {
                    final InetAddress ifaceAddress = iface.getAddress();
                    final int ifaceAddressPrefix = iface.getPrefix();
                    if (sameIPv6Net(announceAddress, announcePrefix, ifaceAddress, ifaceAddressPrefix)) {
                        list.add(announceAddress);
                    } 
                }
            }
        }

        return list;
    }
    
    static boolean sameIPv4Net(InetAddress announceAddress, int announcePrefix,
            InetAddress interfaceAddress, int interfacePrefix) {
        final byte[] announceBytes = announceAddress.getAddress();
        final byte[] interfaceBytes = interfaceAddress.getAddress();
        int announceInteger = convertToInteger(announceBytes);
        int interfaceInteger = convertToInteger(interfaceBytes);
        announceInteger = announceInteger >>> (Integer.SIZE - announcePrefix);
        interfaceInteger = interfaceInteger >>> (Integer.SIZE - interfacePrefix);
        return announceInteger == interfaceInteger;
    }
    
    private static int convertToInteger(byte... address) {
        int value = 0;
        for (final byte b: address) {
            value = (value << Byte.SIZE) | (b & 0xff);
        }
        return value;
    }
    
    static boolean sameIPv6Net(InetAddress announceAddress, int announcePrefixLength,
            InetAddress interfaceAddress, int interfacePrefixLength) {
        if (announcePrefixLength != interfacePrefixLength) {
            return false;
        }

        if (!(announceAddress instanceof Inet6Address) || !(interfaceAddress instanceof Inet6Address)) {
            return false;
        }

        final byte[] announceAddr = announceAddress.getAddress();
        final byte[] interfaceAddr = interfaceAddress.getAddress();

        for (int i = 0; i < (announcePrefixLength / Byte.SIZE); i++) {
            if (announceAddr[i] != interfaceAddr[i]) {
                return false;
            }
        }

        return true;
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
