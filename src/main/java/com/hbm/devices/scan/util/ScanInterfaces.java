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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Convenience class to gather all network interfaces eligible for multicast scanning &amp; sending.
 * 
 * @since 1.0
 */
public final class ScanInterfaces {

    private final List<NetworkInterface> interfaces;

    public ScanInterfaces() throws SocketException {
        interfaces = new LinkedList<NetworkInterface>();
        final Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();

        if (ifs != null) {
            while (ifs.hasMoreElements()) {
                final NetworkInterface iface = ifs.nextElement();
                if (willScan(iface)) {
                    interfaces.add(iface);
                }
            }
        }
    }

    public Collection<NetworkInterface> getInterfaces() {
        return interfaces;
    }

    private static boolean willScan(NetworkInterface iface) throws SocketException {
        if (iface.isLoopback()) {
            return false;
        }
        if (!iface.isUp()) {
            return false;
        }
        if (!hasConfiguredIPv4Address(iface)) {
            return false;
        }
        if (iface.supportsMulticast()) {
            return true;
        }
        return false;
    }

    private static boolean hasConfiguredIPv4Address(NetworkInterface iface) {
        final Enumeration<InetAddress> addrs = iface.getInetAddresses();
        while (addrs.hasMoreElements()) {
            final InetAddress addr = addrs.nextElement();
            if (addr instanceof Inet4Address) {
                final Inet4Address addr4 = (Inet4Address)addr;
                if (!addr4.isAnyLocalAddress()) {
                    return true;
                }
            }
        }
        return false;
    }
}
