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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.MissingDataException;

/**
 * Convenience class for checking if an IP connection is possible to an announced device.
 * 
 * @since 1.0
 */
public class ConnectionFinder {

    private final boolean preferIPv6;
    private final IPv4ConnectionFinder ipv4ConnectionFinder;
    private final IPv6ConnectionFinder ipv6ConnectionFinder;

    public ConnectionFinder(Collection<NetworkInterface> interfaces, boolean preferIPv6) {
        this.preferIPv6 = preferIPv6;
        this.ipv4ConnectionFinder = new IPv4ConnectionFinder(interfaces);
        this.ipv6ConnectionFinder = new IPv6ConnectionFinder(interfaces);
    }

    public InetAddress getConnectableAddress(Announce announce) throws MissingDataException {
        if (preferIPv6) {
            final InetAddress address = ipv6ConnectionFinder.getConnectableAddress(announce);
            if (address != null) {
                return address;
            } else {
                return ipv4ConnectionFinder.getConnectableAddress(announce);
            }
        } else {
            final InetAddress address = ipv4ConnectionFinder.getConnectableAddress(announce);
            if (address != null) {
                return address;
            } else {
                return ipv6ConnectionFinder.getConnectableAddress(announce);
            }
        }
    }
}
