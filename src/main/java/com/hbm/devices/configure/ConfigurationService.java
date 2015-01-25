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

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import com.hbm.devices.scan.messages.Configure;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Response;
import com.hbm.devices.scan.ScanConstants;

/** 
 *This is the main service which is used to configure a device.<p>
 *
 * It sends the configuration settings and listen to responses. If no
 * response is received within a certain time, a callback method ({@link
 * ConfigCallback#onTimeout(int timeout)}) is called.  Otherwise if a
 * response is received, either {@link
 * ConfigCallback#onSuccess(Response)} or {@link
 * ConfigCallback#onError(Response)} is called accordingly
 * to the response.<p>
 *
 * The main method, which is used to transmit configuration settings, is
 * {@link ConfigurationService#sendConfiguration(ConfigureParams,
 * ConfigCallback, int)}.<p>
 *
 * @since 1.0
 *
 */
public class ConfigurationService implements Observer {

    private final Map<String, ConfigQuery> awaitingResponses;

    private final ResponseListener responseListener;

    private final ConfigurationSender configSender;

    private final ScheduledThreadPoolExecutor executor;

    private static final Logger LOGGER = 
        Logger.getLogger(ScanConstants.LOGGER_NAME);

    /**
     * This is the standard constructor to instantiate a configuration
     * service.  <p>
     *
     * It starts the network multicast sockets for communication with
     * the devices, the message parsers to convert incoming JSON Strings
     * into objects or outgoing objects into JSON Strings.
     *
     */
    public ConfigurationService(ConfigurationSender sender, ResponseListener listener) {
        executor = new ScheduledThreadPoolExecutor(1);
        awaitingResponses = new HashMap<String, ConfigQuery>();
        configSender = sender;
        responseListener = listener;
        responseListener.addObserver(this);
    }

    public void shutdown() {
        responseListener.deleteObserver(this);

        executor.shutdownNow();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.info("Interrupted while waiting for termination of timer tasks!\n");
        }
    }

    /**
     * This method checks, if the service is waiting for a response.
     * 
     * @return returns if the service is waiting for a response
     */
    public boolean awaitingResponse() {
        return !awaitingResponses.isEmpty();
    }

    /**
     * 
     * This method is called when any response packet is received. It
     * checks if the received response corresponds to an earlier sent
     * configuration query. If not, the response is dropped, otherwise
     * either the onSucess or onError callback function of the
     * corresponding query object is called.
     * 
     * @param arg the received response
     */
    @Override
	public void update(Observable o, Object arg) {
        if (!(arg instanceof Response))  {
            return;
        }
        final Response response = (Response)arg;

        if (awaitingResponses.containsKey(response.getId())) {
            final ConfigQuery configQuery = awaitingResponses.get(response.getId());
            awaitingResponses.remove(response.getId());
            if (response.getError() == null) {
                configQuery.getConfigCallback().onSuccess(response);
            } else {
                configQuery.getConfigCallback().onError(response);
            }
        }
    }

    /**
     *
     * This method sends a configuration via multicast. If no response
     * is received within the timeout, the callback method {@link
     * ConfigCallback#onTimeout(int timeout)} is called. If a response
     * is received, either {@link ConfigCallback#onSuccess(
     * Response)} or {@link ConfigCallback#onError(
     * Response)} is called.
     * 
     * @param configParams
     *              the configuration parameters, which are send via
     *              multicast
     * @param callback
     *              the interface with the callback methods for error
     *              handling
     * @param timeout
     *              the time in ms, the service waits for a response
     */
    public void sendConfiguration(final ConfigureParams configParams,
        final ConfigCallback callback, int timeout) {

        if (configParams == null) {
            throw new IllegalArgumentException("configParams must not be null");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout must be greater/equal than 0");
        }
        if (callback == null) {
            throw new IllegalArgumentException("the callback parameter must not be null");
        }

        final String queryID = UUID.randomUUID().toString();
        final Configure config = new Configure(configParams, queryID);
        ConfigQuery configQuery = new ConfigQuery(config, callback, timeout);

        awaitingResponses.put(queryID, configQuery);
        final TimeoutTimerTask task = new TimeoutTimerTask(configQuery);
        executor.schedule(task, timeout, TimeUnit.MILLISECONDS);

        configSender.sendQuery(config);
    }

    private class TimeoutTimerTask implements Callable<Void> {
        private final ConfigQuery configQuery;

        TimeoutTimerTask(ConfigQuery query) {
            configQuery = query;
        }

        @Override
        public Void call() throws Exception {
            synchronized (awaitingResponses) {
                if (awaitingResponses.containsKey(configQuery.getQueryID())) {
                    awaitingResponses.remove(configQuery.getQueryID());
                    configQuery.getConfigCallback().onTimeout(configQuery.getTimeout());
                }
            }
            return null;
        }
    }
}

class ConfigQuery {

    private Configure config;
    private int timeout;

    private ConfigCallback callback;

    ConfigQuery(Configure config, ConfigCallback callback, int timeout) {
        this.config = config;
        this.timeout = timeout;
        this.callback = callback;
    }

    String getQueryID() {
        return config.getQueryId();
    }

    Configure getConfiguration() {
        return this.config;
    }

    int getTimeout() {
        return this.timeout;
    }

    ConfigCallback getConfigCallback() {
        return callback;
    }
}
