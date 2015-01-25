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

public class ConfigureParams {

    private ConfigureDevice device;
    private ConfigureNetSettings netSettings;
    private int ttl;

    public ConfigureParams(ConfigureDevice device, ConfigureNetSettings netSettings) {
        this.device = device;
        this.netSettings = netSettings;
        this.ttl = 1;
    }

    public ConfigureParams(ConfigureDevice device, ConfigureNetSettings netSettings, int ttl) {
        this(device, netSettings);
        this.ttl = ttl;
    }

    public ConfigureDevice getDevice() {
        return device;
    }

    public ConfigureNetSettings getNetSettings() {
        return netSettings;
    }

    /**
     * @return An optional key which limits the number of router hops a configure request/response
     *         can cross. Leaving out this key should default to a ttl (Time to live) of 1 when
     *         sending datagrams, so no router boundary is crossed.
     */
    public int getTtl() {
        return ttl;
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
        sb.append("ttl: ").append(ttl).append("\n\n");

        return sb.toString();
    }

    public static void checkForErrors(ConfigureParams params) throws MissingDataException {
        if (params == null) {
            throw new IllegalArgumentException("params object must not be null");
        }

        if (params.ttl < 1) {
            throw new MissingDataException(
                    "time-to-live must be greater or equals 1 in ConfigureParams");
        }

        if (params.device == null) {
            throw new IllegalArgumentException("No device in ConfigureParams");
        }
        ConfigureDevice.checkForErrors(params.device);

        if (params.netSettings == null) {
            throw new IllegalArgumentException("No net settings in ConfigureParams");
        }
        ConfigureNetSettings.checkForErrors(params.netSettings);
    }
}
