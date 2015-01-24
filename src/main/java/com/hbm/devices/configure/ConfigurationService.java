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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.hbm.devices.scan.MulticastSender;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.MissingDataException;
import com.hbm.devices.scan.messages.Response;
import com.hbm.devices.scan.util.ScanInterfaces;

/** 
 *This is the main service which is used to configure a device.<p>
 *
 * It sends the configuration settings and listen to responses. If no
 * response is received within a certain time, a callback method ({@link
 * ConfigCallback#onTimeout(ConfigQuery)}) is called.  Otherwise if a
 * response is received, either {@link
 * ConfigCallback#onSuccess(ConfigQuery, Response)} or {@link
 * ConfigCallback#onError(ConfigQuery, Response)} is called accordingly
 * to the response.<p>
 *
 * The main method, which is used to transmit configuration settings, is
 * {@link ConfigurationService#sendConfiguration(ConfigureParams,
 * ConfigCallback, int)}.<p>
 *
 * The {@link ConfigureParams} are also checked if they conform to the
 * HBM network discovery and configuration protocol and the user will be
 * notified if the configuration parameters are not valid.
 * 
 * @since 1.0
 *
 */
public class ConfigurationService implements Observer, Noticeable {

    private Map<String, ConfigQuery> awaitingResponses;
    private Map<String, ScheduledFuture<Void>> timeoutTasks;

    private MulticastSender multicastSender;
    private ResponseListener responseListener;
    private Thread responseListenerThread;

    private ConfigurationSender configSender;
    private ConfigParser configParser;

    private ScheduledThreadPoolExecutor executor;

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
     * @throws IOException if the multicast sockets cannot be started.
     */
    public ConfigurationService() throws IOException {
        executor = new ScheduledThreadPoolExecutor(1);

        awaitingResponses = new HashMap<String, ConfigQuery>();
        timeoutTasks = new HashMap<String, ScheduledFuture<Void>>();

        configSender = new ConfigurationSender();
        configParser = new ConfigParser(this);

        multicastSender = new MulticastSender(
            new ScanInterfaces().getInterfaces(), this);
        configSender.addObserver(configParser);
        configParser.addObserver(multicastSender);

        responseListener = new ResponseListener();
        responseListener.addObserver(this);
        responseListenerThread = new Thread(responseListener);
        responseListenerThread.start();
    }

    /**
     * Don't use this constructor. It is only used for the JUnit tests
     * to set up custom mutlicast sender and receiver
     * 
     * @param fakeSender
     * @param fakeReceiver
     * @param forcedQueryID
     */
    @SuppressWarnings("unused")
    private ConfigurationService(Observer fakeSender, Observable fakeReceiver, 
        String forcedQueryID) throws ReflectiveOperationException {
        
        executor = new ScheduledThreadPoolExecutor(1);

        awaitingResponses = new HashMap<String, ConfigQuery>();
        timeoutTasks = new HashMap<String, ScheduledFuture<Void>>();

        Constructor<ConfigurationSender> senderConstr;
        senderConstr =
            ConfigurationSender.class.getDeclaredConstructor(String.class);
        senderConstr.setAccessible(true);
        configSender = senderConstr.newInstance(forcedQueryID);
        configParser = new ConfigParser(this);

        configSender.addObserver(configParser);
        configParser.addObserver(fakeSender);

        Constructor<ResponseListener> listenerConstr;
        listenerConstr =
            ResponseListener.class.getDeclaredConstructor(Observable.class);
        listenerConstr.setAccessible(true);
        responseListener = listenerConstr.newInstance(fakeReceiver);
        responseListener.addObserver(this);
    }

    public void shutdown() {
        multicastSender.shutdown();
        responseListener.stop();

        executor.shutdownNow();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Ignore
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
     * This method checks, if the service has running timeout timers
     * 
     * @return if the service has running timeout timers
     */
    public boolean hasResponseTimeoutTimer() {
        return !timeoutTasks.isEmpty();
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
    @Override public void update(Observable o, Object arg) {
        if (!(arg instanceof Response))  {
            return;
        }
        Response response = (Response)arg;

        if (awaitingResponses.containsKey(response.getId())) {
            ConfigQuery configQuery = awaitingResponses.get(response.getId());
            awaitingResponses.remove(response.getId());
            // if a response is received within timeout,
            // the timeoutTask is cancelled
            if (timeoutTasks.containsKey(response.getId())) {
                timeoutTasks.get(response.getId()).cancel(false);
                timeoutTasks.remove(response.getId());
            }
            if (response.getError() == null) {
                configQuery.getConfigCallback().onSuccess(configQuery, response);
            } else {
                configQuery.getConfigCallback().onError(configQuery, response);
            }
        }
    }

    /**
     *
     * This method sends a configuration via multicast. If no response
     * is received within the timeout, the callback method {@link
     * ConfigCallback#onTimeout(ConfigQuery)} is called. If a response
     * is received, either {@link ConfigCallback#onSuccess(ConfigQuery,
     * Response)} or {@link ConfigCallback#onError(ConfigQuery,
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
     * @throws MissingDataException
     *              if some information in {@code configParams} is
     *              missing according to the specification.
     */
    public void sendConfiguration(ConfigureParams configParams,
        final ConfigCallback callback, int timeout) throws MissingDataException {

        if (configParams == null) {
            throw new IllegalArgumentException("configParams must not be null");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout must be greater than 0");
        }
        if (callback == null) {
            throw new IllegalArgumentException("the callback parameter must not be null");
        }

        ConfigureParams.checkForErrors(configParams);

        final ConfigQuery query =
            configSender.generateConfigQuery(configParams, callback, timeout);
        // save the QueryID in the hashmap and start timeoutTimer BEFORE sending the query
        awaitingResponses.put(query.getQueryID(), query);
        TimeoutTimerTask task = new TimeoutTimerTask(query);
        executor.schedule(task, timeout, TimeUnit.MILLISECONDS);

        configSender.sendQuery(query);
    }

    private class TimeoutTimerTask implements Callable<Void> {
        private ConfigQuery configQuery;

        TimeoutTimerTask(ConfigQuery query) {
            configQuery = query;
        }

        @Override
        public Void call() throws Exception {
            synchronized (awaitingResponses) {
                if (awaitingResponses.containsKey(configQuery.getQueryID())) {
                    awaitingResponses.remove(configQuery.getQueryID());
                    configQuery.getConfigCallback().onTimeout(configQuery);
                }
                timeoutTasks.remove(configQuery.getQueryID());
            }
            return null;
        }

    }

    @Override
    public void onException(Exception e) {
        LOGGER.info(e.toString());
    }
}
