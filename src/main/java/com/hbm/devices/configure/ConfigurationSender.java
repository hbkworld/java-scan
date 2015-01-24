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

import java.util.Observable;
import java.util.UUID;

import com.hbm.devices.scan.messages.Configure;
import com.hbm.devices.scan.messages.ConfigureParams;

/**
 * This class is used to send a configuration to a device. Its main task is to generate an unique
 * query id. When the device receives the query, it sends a response. This response contains this
 * query id, so the service can match the response to the query.
 * 
 * @since 1.0
 *
 */
public class ConfigurationSender extends Observable {

    private final String forcedQueryID;

    public ConfigurationSender() {
        this.forcedQueryID = null;
    }

    /**
     * Don't use this method. It is only used for the JUnit tests
     * 
     * @param queryID
     */
    @SuppressWarnings("unused")
    private ConfigurationSender(String queryID) {
        this.forcedQueryID = queryID;
    }

    /**
     * This function generates the query.
     * <p>
     * It creates a query container {@link ConfigQuery}, which contains a generated, unique query
     * id, the configuration, the callback interface and the timeout.
     * 
     * @param configParams
     *            the configuration parameters, which should be send to a device
     * @param callback
     *            this is the {@link ConfigCallback} interface, that informs the user if the
     *            configuration is received successfully or if an error occurred.
     * @param timeout
     *            the time in ms within a response by the device is expected
     * @return this function returns the query container, containing the queryID, the configParams,
     *         the callback interface and the timeout
     */
    public ConfigQuery generateConfigQuery(ConfigureParams configParams, ConfigCallback callback,
            int timeout) {
        String queryID;
        if (forcedQueryID == null) {
            queryID = UUID.randomUUID().toString();
        } else {
            queryID = forcedQueryID;
        }

        Configure config = new Configure(configParams, queryID);

        return new ConfigQuery(queryID, config, callback, timeout);
    }

    /**
     * This function notifies the observers that there is a query to be sent.
     * <p>
     * This {@link Observable} object is linked to a {@link ConfigParser} and again the parser is
     * linked to a udp multicast socket. So any query (which is generated via
     * {@link ConfigurationSender#generateConfigQuery(ConfigureParams, ConfigCallback, int)}) is
     * processed in the observer-observable chain and finally transmitted
     * 
     * @param query
     *            the query to be sent
     */
    public void sendQuery(ConfigQuery query) {
        setChanged();
        notifyObservers(query.getConfiguration());
    }
}
