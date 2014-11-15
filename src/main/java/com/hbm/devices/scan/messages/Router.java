package com.hbm.devices.scan.messages;

public class Router {

    private String uuid;

    private Router() {
    }

    /**
     * @return     A string containing the unique ID of the router the device is connected to.
     */
    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Router:\n" +
        "\t uuid: " + uuid;
    }
}

