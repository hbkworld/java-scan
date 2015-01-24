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

import java.net.Inet4Address;
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
import com.hbm.devices.scan.messages.IPv4Entry;
import com.hbm.devices.scan.messages.MissingDataException;

class IPv4ConnectionFinder {

    private final Iterable<InterfaceAddress> ipv4Addresses;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    IPv4ConnectionFinder(Collection<NetworkInterface> interfaces) {
        final List<InterfaceAddress> addressList = new LinkedList<InterfaceAddress>();

        for (final NetworkInterface iface : interfaces) {
            final List<InterfaceAddress> niAddresses = iface.getInterfaceAddresses();
            for (final InterfaceAddress niAddress : niAddresses) {
                final InetAddress interfaceAddress = niAddress.getAddress();
                if (interfaceAddress instanceof Inet4Address) {
                    addressList.add(niAddress);
                }
            }
        }
        ipv4Addresses = addressList;
    }

    InetAddress getConnectableAddress(Announce announce) throws MissingDataException {
        for (final InterfaceAddress niAddress : ipv4Addresses) {
            final InetAddress address = getConnectAddress(niAddress, announce);
            if (address != null) {
                return address;
            }
        }
        return null;
    }

    private static InetAddress getConnectAddress(InterfaceAddress interfaceAddress,
            Announce announce) throws MissingDataException {
        final Iterable<?> announceAddresses = (Iterable<?>) announce.getParams().getNetSettings()
                .getInterface().getIPv4();
        if (announceAddresses == null) {
            return null;
        }

        for (final Object ipv4Entry : announceAddresses) {
            try {
                final InetAddress announceAddress = InetAddress.getByName(((IPv4Entry) ipv4Entry)
                        .getAddress());
                if (!(announceAddress instanceof Inet4Address)) {
                    continue;
                }
                final InetAddress announceNetmask = InetAddress.getByName(((IPv4Entry) ipv4Entry)
                        .getNetmask());
                final int announcePrefix = calculatePrefix(announceNetmask);

                final InetAddress ifaceAddress = interfaceAddress.getAddress();
                final int ifaceAddressPrefix = interfaceAddress.getNetworkPrefixLength();
                if (sameNet(announceAddress, announcePrefix, ifaceAddress, ifaceAddressPrefix)) {
                    return announceAddress;
                }
            } catch (UnknownHostException e) {
                LOGGER.log(Level.INFO, "Can't retrieve InetAddress from IP address!", e);
                continue;
            }
        }

        return null;
    }

    private static int calculatePrefix(InetAddress announceNetmask) {
        final byte[] address = announceNetmask.getAddress();
        int prefix = 0;
        for (int i = 0; i < 4; i++) {
            prefix += Integer.bitCount(address[i] & 0xff);
        }
        return prefix;
    }

    private static boolean sameNet(InetAddress announceAddress, int announcePrefix,
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
        int value = ((int)address[0] & 0xff) << 24;
        value |= ((int)address[1] & 0xff) << 16;
        value |= ((int)address[2] & 0xff) << 8;
        value |= (((int) address[3]) & 0xff) << 0;
        return value;
    }
}
