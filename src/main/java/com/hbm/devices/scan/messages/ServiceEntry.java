package com.hbm.devices.scan.messages;

/**
 * The optional service might be used to deliver the IP port under which the client can reach
 * different services on the device. So devices might e.g. specify how to connect to the data
 * acquisition service.
 * 
 * The content of the service is totally device specific and not specified in this document.
 * 
 * @since 1.0
 */
public class ServiceEntry {

    private ServiceEntry() {
    }

    /**
     * @return Name of the service.
     */
    public String getType() {
        return type;
    }

    /**
     * @return IP port of the service.
     */
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "type: " + type + " port: " + port;
    }

    private String type;
    private int port;

}
