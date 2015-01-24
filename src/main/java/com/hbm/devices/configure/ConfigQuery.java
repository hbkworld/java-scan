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
