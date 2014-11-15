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
        System.out.println("sending ConfigQuery...");
        this.setChanged();
        this.notifyObservers(query.getConfiguration());

    }

}
