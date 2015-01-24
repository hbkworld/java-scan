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

package com.hbm.devices.configure;

import com.hbm.devices.scan.messages.MissingDataException;

/**
 * The device class specifies which device should be configured.
 * 
 * @since 1.0
 *
 */
public class Device {

    private final String uuid;

    public Device(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 
     * @return returns the unique ID of the device
     */
    public String getUUID() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "Device:\n\t uuid: " + uuid + "\n";
    }

    /**
     * This method checks the {@link Device} object for errors and if it conforms to the HBM network
     * discovery and configuration protocol.
     * 
     * @param device
     *          the {@link Device} object, which should be checked for errors
     * @throws MissingDataException
     *          if information required by the specification is mssing in {@code device}.
     * @throws IllegalArgumentException
     *          if {@code device} is null.
     */
    public static void checkForErrors(Device device) throws MissingDataException {
        if (device == null) {
            throw new IllegalArgumentException("device object must not be null");
        }

        if ((device.uuid == null) || (device.uuid.length() == 0)) {
            throw new MissingDataException("No uuid in Device");
        }
    }

}
