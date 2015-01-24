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

import java.util.List;

/**
 * The AnnounceParams describe the properties of the device, which contain
 * information of the device itself (like uuid, name, type, etc), the
 * NetSettings, router information the device is connected to and running
 * services
 * 
 * @since 1.0
 */
public class AnnounceParams {

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
        StringBuilder sb = new StringBuilder();
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
            for (ServiceEntry se : services) {
                sb.append("\n\t").append(se);
            }
        }
        sb.append("\nexpiration: ").append(expiration).append("\n\n");

        return sb.toString();
    }
}
