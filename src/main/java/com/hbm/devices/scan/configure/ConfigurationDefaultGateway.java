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

package com.hbm.devices.scan.configure;

import java.io.Serializable;

/**
 * The default gateway describes the configured default gateway of a
 * device.<p>
 *
 * Only a single default gateway is possible.
 * 
 * @since 1.0
 */
public final class ConfigurationDefaultGateway implements Serializable {

    private static final long serialVersionUID = -6691414244302799743L;

    private final String ipv4Address;

    /**
     * Constructs an object representing a default IPv4 gateway.
     *
     * @param ipv4Address the IPv4 address of the default gateway.
     */
    public ConfigurationDefaultGateway(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    /**
     * @return
     *      A string containing the IPv4 address of the configured
     *      default gateway of {@code null} if no IPv4 default gateway was
     *      announced.
     */
    public String getIpv4Address() {
        return ipv4Address;
    }
}
