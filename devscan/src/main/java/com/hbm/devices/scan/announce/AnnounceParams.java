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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hbm.devices.scan.ScanConstants;

/**
 * The AnnounceParams describe the properties of the device, which contain
 * information of the device itself (like uuid, name, type, etc), the
 * NetSettings, router information the device is connected to and running
 * services
 * 
 * @since 1.0
 */
public final class AnnounceParams implements Serializable {

    private String apiVersion;
    private Device device;
    private NetSettings netSettings;
    private Router router;
    private List<ServiceEntry> services;
    private int expiration;

    private static final long serialVersionUID = 2532013264163413436L;

    private AnnounceParams() {
    }

    /**
     * Gets the router specific information of an announce message.
     * 
     * @return the device. It is guaranteed by the {@link
     * AnnounceDeserializer} that only valid announces are forwarded
     * through the chain of observers, so a null reference is never
     * returned from this method.
     */
    public Device getDevice() {
        return device;
    }

    /**
     * @return the API version of the announce. 
     * It is guaranteed by the {@link
     * AnnounceDeserializer} that only valid announces are forwarded
     * through the chain of observers, so a null reference is never
     * returned from this method.
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * @return the network settings of an announce.
     * It is guaranteed by the {@link
     * AnnounceDeserializer} that only valid announces are forwarded
     * through the chain of observers, so a null reference is never
     * returned from this method.
     */
    public NetSettings getNetSettings() {
        return netSettings;
    }

    /**
     * @return the router object or {@code null}, if no router was
     * announced.
     */
    public Router getRouter() {
        return router;
    }

    /**
     * @return an {@link java.util.Collections#unmodifiableList(List list) unmodifiable List}
     * of services announced. If no services are
     * announced, an empty {@link java.util.List} is returned.
     */
    public List<ServiceEntry> getServices() {
        if (services == null) {
            return new ArrayList<>();
        } else {
            return Collections.unmodifiableList(services);
        }
    }

    public int getExpiration() {
        if (expiration <= 0) {
            return ScanConstants.DEFAULT_EXPIRATION_S;
        }
        return expiration;
    }
}
