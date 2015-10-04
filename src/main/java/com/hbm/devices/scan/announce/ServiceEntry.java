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

package com.hbm.devices.scan.announce;

/**
 * The optional service might be used to deliver the IP port under which the client can reach
 * different services on the device. So devices might e.g. specify how to connect to the data
 * acquisition service.
 * 
 * The content of the service is totally device specific and not specified in this document.
 * 
 * @since 1.0
 */
public final class ServiceEntry {

    public static final String SERVICE_HTTP = "http";
    public static final String SERVICE_SSH = "ssh";
    public static final String SERVICE_DAQ = "daq";

    private String type;
    private int port;

    private ServiceEntry() {
    }

    /**
     * @return Name of the service. Might return {@code null} if not announced.
     */
    public String getType() {
        return type;
    }

    /**
     * @return IP port of the service.
     */
    public int getPort() {
        return port;
    }
}
