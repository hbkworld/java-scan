/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
 */

package com.hbm.devices.scan.messages;

import com.google.gson.annotations.SerializedName;

/**
 * The device describes some properties of the device.
 * 
 * @since 1.0
 */
public class Device {

    private String uuid;
    private String name;
    private String type;
    private String familyType;
    private String firmwareVersion;
    @SerializedName("isRouter")
    private boolean router = false;

    private Device() {
    }

    /**
     * @param uuid
     *            This string contains the unique ID of the device that should be configured. The
     *            uuid itself must be gathered from an announce datagram.
     */
    public Device(String uuid) {
        this();
        this.uuid = uuid;
    }

    /**
     * @return A string containing a unique ID of a device. That might be the MAC address of a
     *         network interface card. This uuid is necessary to address devices by a dedicated
     *         request.
     */
    public String getUuid() {
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

    @Override
    public String toString() {
        return "Device:\n" + "\tUUID: " + uuid + "\n" + "\tname: " + name + "\n" + "\tfamily: "
                + familyType + "\n" + "\ttype: " + type + "\n" + "\tfirmware version: "
                + firmwareVersion + "\n" + "\tisRouter: " + router + "\n";
    }
}
