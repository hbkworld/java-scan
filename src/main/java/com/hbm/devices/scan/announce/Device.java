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

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * The device describes some properties of the device.
 * 
 * @since 1.0
 */
public final class Device implements Serializable {

    private String uuid;
    private String name;
    private String type;
    private String familyType;
    private String firmwareVersion;
    private String hardwareId;

    static final long serialVersionUID = -2099617037615519469L;

    @SerializedName("isRouter")
    private boolean router;

    private Device() {
    }

    /**
     * @return
     *      A string containing a unique ID of a device. That might be the MAC address of a
     *      network interface card. This uuid is necessary to address devices by a dedicated
     *      request. It is guaranteed by the {@link AnnounceDeserializer} that only valid
     *      announces are forwarded through the chain of observers, so a null reference is
     *      never returned from this method.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return An string containing the name of the device or {@code
     * null} if no name was announced.
     */
    public String getName() {
        return name;
    }

    /**
     * @return A string describing the type of the device, e.g. for a QuantumX MX840 this will
     *         contain "MX840". This method could return {@code null} if
     *         no type was announced.
     */
    public String getType() {
        return type;
    }

    /**
     * @return A string describing the hardware ID of a device or {@code null} 
     * if no hardware ID was announced.
     */
    public String getHardwareId() {
        return hardwareId;
    }

    /**
     * @return A string describing the family type of the device, e.g.
     * QuantumX or PMX. Might be {@code null} if no family type was
     * announced.
     */
    public String getFamilyType() {
        return familyType;
    }

    /**
     * @return A string containing the firmware version of the device.
     * Might be {@code null} if no firmware version was announced.
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
