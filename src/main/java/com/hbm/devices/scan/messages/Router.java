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

public class Router {

    private String uuid;

    private Router() {
    }

    /**
     * @return     A string containing the unique ID of the router the device is connected to.
     */
    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Router:\n" +
        "\t uuid: " + uuid;
    }
}

