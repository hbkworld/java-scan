/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
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
public class ScanInterfaces {

    private List<NetworkInterface> interfaces;

    public ScanInterfaces() throws SocketException {
        interfaces = new LinkedList<NetworkInterface>();
        Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();

        if (ifs != null) {
            while (ifs.hasMoreElements()) {
                NetworkInterface iface = ifs.nextElement();
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
        Enumeration<InetAddress> addrs = iface.getInetAddresses();
        while (addrs.hasMoreElements()) {
            InetAddress addr = addrs.nextElement();
            if (addr instanceof Inet4Address) {
                Inet4Address addr4 = (Inet4Address) addr;
                if (!addr4.isAnyLocalAddress()) {
                    return true;
                }
            }
        }
        return false;
    }
}
