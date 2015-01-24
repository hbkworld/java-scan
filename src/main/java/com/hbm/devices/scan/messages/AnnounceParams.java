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

import java.util.List;

/**
 * The AnnounceParams describe the properties of the device, which contain
 * information of the device itself (like uuid, name, type, etc), the
 * NetSettings, router information the device is connected to and running
 * services
 * 
 * @since 1.0
 */
public final class AnnounceParams {

    private Device device;
    private NetSettings netSettings;
    private Router router;
    private List<ServiceEntry> services;
    private int expiration;

    private AnnounceParams() {
    }

    public Device getDevice() throws MissingDataException {
        if (device == null) {
            throw new MissingDataException("No device section in announce params!");
        }
        return device;
    }

    public NetSettings getNetSettings() throws MissingDataException {
        if (netSettings == null) {
            throw new MissingDataException("No netSettings section in announce params!");
        }
        return netSettings;
    }

    public Router getRouter() {
        return router;
    }

    public Iterable<ServiceEntry> getServices() throws MissingDataException {
        return services;
    }

    public int getExpiration() {
        return expiration;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (device != null) {
            sb.append(device);
        }
        if (netSettings != null) {
            sb.append(netSettings);
        }
        if (router != null) {
            sb.append(router);
        }
        if (services != null) {
            sb.append("Services:");
            for (final ServiceEntry se : services) {
                sb.append("\n\t").append(se);
            }
        }
        sb.append("\nexpiration: ").append(expiration).append("\n\n");

        return sb.toString();
    }
}
