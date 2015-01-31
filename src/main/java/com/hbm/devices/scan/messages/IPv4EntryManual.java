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
 * A class holding the information for manual IP interface configuration an a device.
 */
public class IPv4EntryManual {

    private final String manualAddress;
    private final String manualNetmask;

    /**
     * Constructs a {@link IPv4EntryManual} object.
     *
     * @param address The IP address for the manual interface
     * configuration of a device.
     * @param netmask The network mask for the manual interface
     * configuration of a device.
     */
    public IPv4EntryManual(String address, String netmask) {
        if (address == null) {
            throw new IllegalArgumentException("address parameter must not be null");
        }
        if (netmask == null) {
            throw new IllegalArgumentException("netmask parameter must not be null");
        }
        this.manualAddress = address;
        this.manualNetmask = netmask;
    }

    /**
     * @return the IP address for the manual interface configuration of
     * a device.
     */
    public String getAddress() {
        return manualAddress;
    }

    /**
     * @return the IP network mask for the manual interface configuration of
     * a device.
     */
    public String getNetmask() {
        return manualNetmask;
    }

    @Override
    public String toString() {
        return manualAddress + "/" + manualNetmask;
    }
}
