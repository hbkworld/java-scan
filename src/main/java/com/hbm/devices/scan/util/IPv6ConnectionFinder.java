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
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.IPv6Entry;
import com.hbm.devices.scan.messages.MissingDataException;

final class IPv6ConnectionFinder {

    private final Iterable<NetworkInterfaceAddress> interfaceAddresses;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    IPv6ConnectionFinder(Collection<NetworkInterfaceAddress> interfacesAddresses) {
        this.interfaceAddresses = interfacesAddresses;
    }

    InetAddress getConnectableAddress(Announce announce) throws MissingDataException {
        for (final NetworkInterfaceAddress niAddress : interfaceAddresses) {
            final InetAddress address = getConnectAddress(niAddress, announce);
            if (address != null) {
                return address;
            }
        }
        return null;
    }

    private static InetAddress getConnectAddress(NetworkInterfaceAddress interfaceAddress,
            Announce announce) throws MissingDataException {
        final Iterable<IPv6Entry> announceAddresses = announce.getParams().getNetSettings()
                .getInterface().getIPv6();
        if (announceAddresses == null) {
            return null;
        }
        for (final IPv6Entry address : announceAddresses) {
            try {
                final InetAddress announceAddress = InetAddress.getByName(address.getAddress());
                if (!(announceAddress instanceof Inet6Address)) {
                    continue;
                }
                if (sameNet(announceAddress, address.getPrefix(),
                        interfaceAddress.getAddress(), interfaceAddress.getPrefix())) {
                    return announceAddress;
                }
            } catch (UnknownHostException e) {
                LOGGER.log(Level.INFO, "Can't retrieve InetAddress from IP address!", e);
            }

        }
        return null;
    }

    static boolean sameNet(InetAddress announceAddress, int announcePrefixLength,
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
