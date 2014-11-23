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
 * Network configuration request datagrams are dedicated requests, so only the device addressed by
 * request/configure/device/uuid must answer with a network configuration response datagram.
 * 
 * @since 1.0
 */
public class Configure extends JsonRpc {

    private String id;
    private ConfigureParams params;

    private Configure() {
        super("configure");
    }

    /**
     * @param params
     *            the configuration parameters, which should be sent to a device
     * @param queryId
     *            A value of any type, which is used to match the response with the request that it
     *            is replying to.
     */
    public Configure(ConfigureParams params, String queryId) {
        this();
        this.params = params;
        this.id = queryId;
    }

    public ConfigureParams getParams() {
        return params;
    }

    @Override
    public String toString() {
        return params.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Configure)) {
            return false;
        }
        Configure rhs = (Configure) o;
        return this.getJSONString().equals(rhs.getJSONString());
    }

    @Override
    public int hashCode() {
        return getJSONString().hashCode();
    }

    public String getQueryId() {
        return id;
    }

    /**
     * This method checks a {@link Configure} object for errors; especially it checks if the
     * {@link Configure} object conforms the specification.
     * 
     * @param config
     *          the {@link Configure} object, which should be checked for errors
     * @throws MissingDataException
     *          if some information required by the specification is not in {@code config}.
     * @throws NullPointerException
     *          if {@code settings} is null.
     */
    public static void checkForErrors(Configure config) throws MissingDataException {
        if (config == null) {
            throw new NullPointerException("config object must not be null");
        }

        if ((config.id == null) || (config.id.length() == 0)) {
            throw new MissingDataException("no queryId in configure");
        }

        if (config.params == null) {
            throw new MissingDataException("no params in configure");
        }

        ConfigureParams.checkForErrors(config.params);
    }
}
