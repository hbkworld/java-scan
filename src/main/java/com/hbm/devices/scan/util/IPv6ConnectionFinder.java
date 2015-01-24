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

package com.hbm.devices.scan.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.IPv6Entry;
import com.hbm.devices.scan.messages.MissingDataException;

class IPv6ConnectionFinder {

    private Iterable<InterfaceAddress> ipv6Addresses;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    IPv6ConnectionFinder(Collection<NetworkInterface> interfaces) {

        List<InterfaceAddress> addressList = new LinkedList<InterfaceAddress>();

        for (NetworkInterface iface : interfaces) {
            List<InterfaceAddress> niAddresses = iface.getInterfaceAddresses();
            for (InterfaceAddress niAddress : niAddresses) {
                InetAddress interfaceAddress = niAddress.getAddress();
                if (interfaceAddress instanceof Inet6Address) {
                    addressList.add(niAddress);
                }
            }
        }
        ipv6Addresses = addressList;

    }

    InetAddress getConnectableAddress(Announce announce) throws MissingDataException {
        for (InterfaceAddress niAddress : ipv6Addresses) {
            InetAddress address = getConnectAddress(niAddress, announce);
            if (address != null) {
                return address;
            }
        }
        return null;
    }

    private static InetAddress getConnectAddress(InterfaceAddress interfaceAddress,
            Announce announce) throws MissingDataException {
        List<IPv6Entry> announceAddresses = announce.getParams().getNetSettings()
                .getInterface().getIPv6();
        if (announceAddresses == null) {
            return null;
        }
        for (IPv6Entry address : announceAddresses) {
            InetAddress announceAddress;
            try {
                announceAddress = InetAddress.getByName(address.getAddress());
                if (!(announceAddress instanceof Inet6Address)) {
                    continue;
                }
                if (sameNet(announceAddress, Short.parseShort(address.getPrefix()),
                        interfaceAddress.getAddress(), interfaceAddress.getNetworkPrefixLength())) {
                    return announceAddress;
                }

            } catch (UnknownHostException e) {
                LOGGER.log(Level.INFO, "Can't retrieve InetAddress from IP address!", e);
                continue;
            }

        }
        return null;
    }

    private static boolean sameNet(InetAddress announceAddress, short announcePrefixLength,
            InetAddress interfaceAddress, short interfacePrefixLength) {
        if (announcePrefixLength != interfacePrefixLength) {
            return false;
        }

        byte[] announceAddr = announceAddress.getAddress();
        byte[] interfaceAddr = interfaceAddress.getAddress();

        if (announceAddr.length < (announcePrefixLength / 8)
                || interfaceAddr.length < (announcePrefixLength / 8)) {
            return false;
        }

        for (int i = 0; i < (announcePrefixLength / 8); i++) {
            if (announceAddr[i] != interfaceAddr[i]) {
                return false;
            }
        }

        return true;
    }
}
