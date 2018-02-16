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

package com.hbm.devices.scan;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * Convenience class to gather all network interfaces eligible for multicast scanning &amp; sending.
 *
 * @since 1.0
 */
public final class ScanInterfaces {
    private final List<NetworkInterface> interfaces;

    /**
     * Constructs an object containing all network interfaces eligible
     * for multicast scanning &amp; sending.
     *
     * @throws SocketException if an I/O error occurs.
     */
    public ScanInterfaces() throws SocketException {
        this(Predicates.<NetworkInterface>alwaysTrue());
    }

    /**
     * Constructs an object containing all network interfaces eligible for multicast scanning &amp;
     * sending and fullfilling the conditions specified by the given predicate.
     *
     * @param ifacePredicate
     *            is called before doing checks on the interface reg. its awareness for multicast
     *            scanning. So the caller may inject own filter criteria.
     * @throws SocketException
     *             if an I/O error occurs.
     */
    public ScanInterfaces(Predicate<NetworkInterface> ifacePredicate) throws SocketException {
        Builder<NetworkInterface> interfacesBuilder = ImmutableList.<NetworkInterface> builder();
        for (NetworkInterface iface : Iterables.filter(getAllNetworkInterfaces(), ifacePredicate)) {
            if (willScan(iface)) {
                interfacesBuilder.add(iface);
            }
        }
        interfaces = interfacesBuilder.build();
    }

    /**
     * @return an {@link ImmutableList immutable list}
     * of {@link java.net.NetworkInterface NetworkInterfaces} usable for
     * Multicast scanning. If no interface was found, an empty
     * {@link ImmutableList immutable list} is returned.
     */
    public Collection<NetworkInterface> getInterfaces() {
        return interfaces;
    }

    private static Iterable<NetworkInterface> getAllNetworkInterfaces() throws SocketException {
        return Collections.list(Optional.fromNullable(NetworkInterface.getNetworkInterfaces()).or(Collections.<NetworkInterface>emptyEnumeration()));
    }

    private static boolean willScan(NetworkInterface iface) throws SocketException {
        if (iface.isLoopback() ||
            !iface.isUp() ||
            !hasConfiguredIPv4Address(iface) ||
            !iface.supportsMulticast()) {
            return false;
        }
        return true;
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
