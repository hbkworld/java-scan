package com.hbm.devices.configure;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hbm.devices.scan.MissingDataException;
import com.hbm.devices.scan.MulticastSender;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Response;
import com.hbm.devices.scan.util.ScanInterfaces;

/**
 * This is the main service which is used to configure a device.
 * <p>
 * It sends the configuration settings and listen to responses. If no response is received within a
 * certain time, a callback method ({@link ConfigCallback#onTimeout(ConfigQuery)}) is called.
 * Otherwise if a response is received, either
 * {@link ConfigCallback#onSuccess(ConfigQuery, Response)} or
 * {@link ConfigCallback#onError(ConfigQuery, Response)} is called accordingly to the response.
 * <p>
 * The main method, which is used to transmit configuration settings, is
 * {@link ConfigurationService#sendConfiguration(ConfigureParams, ConfigCallback, int)}.
 * <p>
 * The {@link ConfigureParams} are also checked if they conform to the HBM network discovery and
 * configuration protocol and the user will be notified if the configuration parameters are not
 * valid.
 * 
 * @since 1.0
 *
 */
public class ConfigurationService implements Observer, Noticeable {

    private Map<String, ConfigQuery> awaitingResponses;
    private Map<String, ScheduledFuture<Void>> timeoutTasks;

    private ResponseListener responseListener;
    private Thread responseListenerThread;

    private ConfigurationSender configSender;
    private ConfigParser configParser;

    private ScheduledThreadPoolExecutor executor;

    /**
     * This is the standard constructor to instantiate a configuration service.
     * <p>
     * It starts the network multicast sockets for communication with the devices, the message
     * parsers to convert incoming JSON Strings into objects or outgoing objects into JSON Strings.
     */
    public ConfigurationService() {
        this.executor = new ScheduledThreadPoolExecutor(1);

        this.awaitingResponses = new HashMap<String, ConfigQuery>();
        this.timeoutTasks = new HashMap<String, ScheduledFuture<Void>>();

        this.configSender = new ConfigurationSender();
        this.configParser = new ConfigParser(this);

        try {
            MulticastSender multicastSender;
            multicastSender = new MulticastSender(new ScanInterfaces().getInterfaces(), this);
            configSender.addObserver(configParser);
            configParser.addObserver(multicastSender);
        } catch (IOException e) {
            e.printStackTrace();
        }

        responseListener = new ResponseListener();
        responseListener.addObserver(this);
        this.responseListenerThread = new Thread(responseListener);
        this.responseListenerThread.start();
    }

    /**
     * Don't use this constructor. It is only used for the JUnit tests to set up custom mutlicast
     * sender and receiver
     * 
     * @param fakeSender
     * @param fakeReceiver
     * @param forcedQueryID
     */
    @SuppressWarnings("unused")
    private ConfigurationService(Observer fakeSender, Observable fakeReceiver, String forcedQueryID) {
        this.executor = new ScheduledThreadPoolExecutor(1);

        this.awaitingResponses = new HashMap<String, ConfigQuery>();
        this.timeoutTasks = new HashMap<String, ScheduledFuture<Void>>();

        Constructor<ConfigurationSender> senderConstr;
        try {
            senderConstr = ConfigurationSender.class.getDeclaredConstructor(String.class);
            senderConstr.setAccessible(true);
            this.configSender = senderConstr.newInstance(forcedQueryID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.configParser = new ConfigParser(this);

        configSender.addObserver(configParser);
        configParser.addObserver(fakeSender);

        Constructor<ResponseListener> listenerConstr;
        try {
            listenerConstr = ResponseListener.class.getDeclaredConstructor(Observable.class);
            listenerConstr.setAccessible(true);
            responseListener = listenerConstr.newInstance(fakeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseListener.addObserver(this);
    }

    @Override
    public void finalize() throws Throwable {
        this.responseListener.stop();

        this.executor.shutdownNow();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Ignore
        }
        super.finalize();
    }

    /**
     * This method checks, if the service is waiting for a response.
     * 
     * @return returns if the service is waiting for a response
     */
    public boolean awaitingResponse() {
        return !this.awaitingResponses.isEmpty();
    }

    /**
     * This method checks, if the service has running timeout timers
     * 
     * @return if the service has running timeout timers
     */
    public boolean hasResponseTimeoutTimer() {
        return !this.timeoutTasks.isEmpty();
    }

    /**
     * This method is called when any response packet is received. It checks if the received
     * response corresponds to an earlier sent configuration query. If not, the response is dropped,
     * otherwise either the onSucess or onError callback function of the corresponding query object
     * is called.
     * 
     * @param arg
     *            the received response
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Response) {
            Response response = (Response) arg;

            if (this.awaitingResponses.containsKey(response.getId())) {
                ConfigQuery configQuery = this.awaitingResponses.get(response.getId());
                this.awaitingResponses.remove(response.getId());
                // if a response is received within timeout, the timeoutTask is cancelled
                if (this.timeoutTasks.containsKey(response.getId())) {
                    this.timeoutTasks.get(response.getId()).cancel(false);
                    this.timeoutTasks.remove(response.getId());
                }
                if (response.getError() == null) {
                    configQuery.getConfigCallback().onSuccess(configQuery, response);
                } else {
                    configQuery.getConfigCallback().onError(configQuery, response);
                }
            }
        }

    }

    /**
     * This method sends a configuration via multicast. If no response is received within the
     * timeout, the callback method {@link ConfigCallback#onTimeout(ConfigQuery)} is called. If a
     * response is received, either {@link ConfigCallback#onSuccess(ConfigQuery, Response)} or
     * {@link ConfigCallback#onError(ConfigQuery, Response)} is called.
     * 
     * @param configParams
     *            the configuration parameters, which are send via multicast
     * @param callback
     *            the interface with the callback methods for error handling
     * @param timeout
     *            the time in ms, the service waits for a response
     * @throws NullPointerException
     * @throws IllegalArgumentException
     * @throws MissingDataException
     */
    public void sendConfiguration(ConfigureParams configParams, final ConfigCallback callback,
            int timeout) throws NullPointerException, IllegalArgumentException,
            MissingDataException {
        if (configParams == null) {
            throw new NullPointerException("configParams must not be null");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout must be greater than 0");
        }
        if (callback == null) {
            throw new NullPointerException("the callback parameter must not be null");
        }

        ConfigureParams.checkForErrors(configParams);

        final ConfigQuery query = this.configSender.generateConfigQuery(configParams, callback,
                timeout);
        // save the QueryID in the hashmap and start timeoutTimer BEFORE sending the query
        this.awaitingResponses.put(query.getQueryID(), query);
        TimeoutTimerTask task = new TimeoutTimerTask(query);
        executor.schedule(task, timeout, TimeUnit.MILLISECONDS);

        this.configSender.sendQuery(query);
    }

    private class TimeoutTimerTask implements Callable<Void> {
        private ConfigQuery configQuery;

        TimeoutTimerTask(ConfigQuery query) {
            this.configQuery = query;
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
        // If an error occurred, simply notify the user. The hashmaps will be cleaned up via timeout
        e.printStackTrace();
    }

}
