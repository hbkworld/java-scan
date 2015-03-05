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

import com.google.gson.annotations.SerializedName;

/**
 * Objects of this class hold the information of a devices network settings.
 */
public final class NetSettings {

    private DefaultGateway defaultGateway;

    @SerializedName("interface")
    private Interface iface;

    private NetSettings() {
    }

    /**
     * @return the default gateway of a device. Might return {@code
     * null} if not announced.
     */
    public DefaultGateway getDefaultGateway() {
        return defaultGateway;
    }
    
    /**
     * @return the network interface of a device. It is guaranteed by the {@link
     * AnnounceDeserializer} that only valid announces are forwarded
     * through the chain of observers, so a null reference is never
     * returned from this method.
     */
    public Interface getInterface() {
        return iface;
    }
}

