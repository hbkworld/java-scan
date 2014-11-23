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

package com.hbm.devices.configure;

import com.hbm.devices.scan.messages.Configure;

/**
 * This class is a container, which contains the unique queryID, the configuration, which is sent to
 * the device, the callback object and the timeout, within a response is expected.
 * <p>
 * To create a {@link ConfigQuery} object, use
 * {@link ConfigurationSender#generateConfigQuery(com.hbm.devices.scan.messages.ConfigureParams, ConfigCallback, int)}
 * 
 * @since 1.0
 */
public class ConfigQuery {

    private String queryID;
    private Configure config;
    private int timeout;

    private ConfigCallback callback;

    /**
     * This constructor is used to instantiate a ConfigQuery. A default timeout of 5000ms is
     * assumed.
     * 
     * @param queryID
     *            this is the unique query id
     * @param config
     *            this is the {@link Configure} object, this query is connected with.
     * @param callback
     *            this is the {@link ConfigCallback} interface, that informs the user if the
     *            configuration is received successfully or if an error occurred.
     */
    ConfigQuery(String queryID, Configure config, ConfigCallback callback) {
        this(queryID, config, callback, 5000);
    }

    /**
     * This constructor is used to instantiate a ConfigQuery.
     * 
     * @param queryID
     *            this is the unique query id
     * @param config
     *            this is the {@link Configure} object, this query is connected with.
     * @param callback
     *            this is the {@link ConfigCallback} interface, that informs the user if the
     *            configuration is received successfully or if an error occurred.
     * @param timeout
     *            this parameter specifies the timeout a response is expected
     */
    ConfigQuery(String queryID, Configure config, ConfigCallback callback, int timeout) {
        this.queryID = queryID;
        this.config = config;
        this.timeout = timeout;
        this.callback = callback;
    }

    /**
     * 
     * @return returns the unique id of the query
     */
    public String getQueryID() {
        return this.queryID;
    }

    /**
     * 
     * @return returns the configuration with which this query is associated
     */
    public Configure getConfiguration() {
        return this.config;
    }

    /**
     * 
     * @return returns the timeout in ms
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * 
     * @return returns the {@link ConfigCallback} interface
     */
    public ConfigCallback getConfigCallback() {
        return callback;
    }

}
