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
 * The device describes some properties of the device.
 * 
 * @since 1.0
 */
public final class Device {

    private final String uuid;
    private String name;
    private String type;
    private String familyType;
    private String firmwareVersion;
    private String hardwareId;

    @SerializedName("isRouter")
    private boolean router;

    Device(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return A string containing a unique ID of a device. That might be the MAC address of a
     *         network interface card. This uuid is necessary to address devices by a dedicated
     *         request.
     * @throws MissingDataException if no uuid was set in Device object
     */
    public String getUuid() throws MissingDataException {
        if (uuid == null) {
            throw new MissingDataException("No UUID in device section!");
        }
        return uuid;
    }

    /**
     * @return An optional string containing the name of the device.
     */
    public String getName() {
        return name;
    }

    /**
     * @return A string describing the type of the device, e.g. for a QuantumX MX840 this will
     *         contain "MX840".
     */
    public String getType() {
        return type;
    }

    /**
     * @return A string describing the hardware ID of a device.
     */
    public String getHardwareId() {
        return hardwareId;
    }

    /**
     * @return A string describing the family type of the device, e.g. QuantumX or PMX.
     */
    public String getFamilyType() {
        return familyType;
    }

    /**
     * @return A string containing the firmware version of the device.
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * @return this key is send with value true if the module acts as a IP router.
     */
    public boolean isRouter() {
        return router;
    }
}
