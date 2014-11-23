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

/**
 * The optional service might be used to deliver the IP port under which the client can reach
 * different services on the device. So devices might e.g. specify how to connect to the data
 * acquisition service.
 * 
 * The content of the service is totally device specific and not specified in this document.
 * 
 * @since 1.0
 */
public class ServiceEntry {

    private String type;
    private int port;

    private ServiceEntry() {
    }

    /**
     * @return Name of the service.
     */
    public String getType() {
        return type;
    }

    /**
     * @return IP port of the service.
     */
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "type: " + type + " port: " + port;
    }
}
