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

package com.hbm.devices.scan.configure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.messages.Configure;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.ErrorObject;
import com.hbm.devices.scan.messages.Response;
import com.hbm.devices.scan.ScanConstants;

/** 
 *This is the main service which is used to configure a device.<p>
 *
 * It sends the configuration settings and listen to responses. If no
 * response is received within a certain time, a callback method ({@link
 * ConfigurationCallback#onTimeout(int timeout)}) is called. Otherwise if a
 * response is received, either {@link
 * ConfigurationCallback#onSuccess(Response)} or {@link
 * ConfigurationCallback#onError(Response)} is called accordingly
 * to the response.<p>
 *
 * The main method, which is used to transmit configuration settings, is
 * {@link ConfigurationService#sendConfiguration(ConfigureParams,
 * ConfigurationCallback, int)}.<p>
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
     * @param sender the ConfigurationSender the ConfigurationService shall use.
     * @param listener the ResponseListener the ConfigurationService shall use.
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

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    LOGGER.log(Level.SEVERE, "Interrupted while waiting for termination of timer tasks!\n");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
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

        String id = response.getId();
        if (id == null || id.length() <= 0) {
            return;
        }
        String result = response.getResult();
        ErrorObject error = response.getError();
        if (result == null && error == null) {
           return;
        }
        if (result != null && error != null) {
           return;
        }

        if (awaitingResponses.containsKey(response.getId())) {
            final ConfigQuery configQuery = awaitingResponses.get(response.getId());
            awaitingResponses.remove(response.getId());
            if (error == null) {
                configQuery.getConfigCallback().onSuccess(response);
            } else {
                String message = error.getMessage();
                if (message == null || message.length() == 0) {
                    return;
                }
                configQuery.getConfigCallback().onError(response);
            }
        }
    }

    /**
     *
     * This method sends a configuration via multicast. If no response
     * is received within the timeout, the callback method {@link
     * ConfigurationCallback#onTimeout(int timeout)} is called. If a response
     * is received, either {@link ConfigurationCallback#onSuccess(
     * Response)} or {@link ConfigurationCallback#onError(
     * Response)} is called.
     * 
     * @param configParams
     *              the configuration parameters, which are send via
     *              multicast
     * @param callback
     *              the interface with the callback methods for error
     *              handling
     * @param timeout
     *              the time in ms, the service waits for a response.
     *              Must be greater than 0.
     * @throws IOException
     *              if the underlying socket send does not succeed.
     */
    public void sendConfiguration(final ConfigureParams configParams,
        final ConfigurationCallback callback, int timeout) throws IOException {

        if (configParams == null) {
            throw new IllegalArgumentException("configParams must not be null");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout must be greater than 0");
        }
        if (callback == null) {
            throw new IllegalArgumentException("the callback parameter must not be null");
        }

        final String queryID = UUID.randomUUID().toString();
        final Configure config = new Configure(configParams, queryID);
        final ConfigQuery configQuery = new ConfigQuery(config, callback, timeout);

        awaitingResponses.put(queryID, configQuery);
        final TimeoutTimerTask task = new TimeoutTimerTask(configQuery);
        executor.schedule(task, timeout, TimeUnit.MILLISECONDS);

        configSender.sendConfiguration(config);
    }

    private class TimeoutTimerTask implements Callable<Void> {
        private final ConfigQuery configQuery;

        TimeoutTimerTask(ConfigQuery query) {
            configQuery = query;
        }

        @Override
        public Void call() throws Exception {
            synchronized (awaitingResponses) {
                final String queryID = configQuery.getQueryID();
                if (awaitingResponses.containsKey(queryID)) {
                    awaitingResponses.remove(queryID);
                    configQuery.getConfigCallback().onTimeout(configQuery.getTimeout());
                }
            }
            return null;
        }
    }
}

class ConfigQuery {

    private final Configure config;
    private final int timeout;
    private final ConfigurationCallback callback;

    ConfigQuery(Configure config, ConfigurationCallback callback, int timeout) {
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

    ConfigurationCallback getConfigCallback() {
        return callback;
    }
}
