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

import java.util.LinkedList;
import java.util.List;

/**
 * The AnnounceParams describe the properties of the device, which contain information of the device
 * itself (like uuid, name, type, etc), the NetSettings, router information the device is connected
 * to and running services
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

    public Device getDevice() {
        return device;
    }

    public NetSettings getNetSettings() {
        return netSettings;
    }

    public Router getRouter() {
        return router;
    }

    public Iterable<ServiceEntry> getServices() {
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
                sb.append("\n\t" + se);
            }
        }
        sb.append("\nexpiration: " + expiration + "\n");
        sb.append("\n");

        return sb.toString();
    }
}
