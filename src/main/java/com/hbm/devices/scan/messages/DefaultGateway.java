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

package com.hbm.devices.scan.messages;

/**
 * The default gateway describes the configured default gateway of a
 * device.<p>
 *
 * Only a single default gateway is possible.
 * 
 * @since 1.0
 */
public final class DefaultGateway {

    final private String ipv4Address;
    private String ipv6Address;

    DefaultGateway(String ipv4Address) {
        this.ipv4Address = ipv4Address;
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
}
