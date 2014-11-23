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

package com.hbm.devices.scan.messages;

/**
 * The default gateway describes the configured default gateway of a
 * device.<p>
 *
 * Only a single default gateway is possible.
 * 
 * @since 1.0
 */
public class DefaultGateway {

    private String ipv4Address;
    private String ipv6Address;

    private DefaultGateway() {
    }

    public DefaultGateway(String ipv4Address, String ipv6Address) {
        this();
        this.ipv4Address = ipv4Address;
        this.ipv6Address = ipv6Address;
    }

    /**
     * @return
     *      A string containing the IPv4 address of the configured
     *      default gateway.
     */
    public String getIpv4Address() {
        return ipv4Address;
    }

    /**
     * @return
     *      A string containing the IPv6 address of the configured
     *      default gateway.
     */
    public String getIpv6Address() {
        return ipv6Address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Gateway: ");
        if (ipv4Address != null) {
            sb.append("IPv4: " + ipv4Address);
        }
        if (ipv6Address != null) {
            sb.append("IPv6: " + ipv6Address);
        }
        return sb.toString();
    }
}
