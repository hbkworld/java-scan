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

import java.util.Observable;
import java.util.Observer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * This class is used to simulate a device.
 * <p>
 * When this class receives a specific configuration message, it responses instantly with a success
 * message.
 * <p>
 * This class is only used for the JUnit tests.
 * 
 * @since 1.0
 *
 */
public class FakeDeviceEmulator extends Observable implements Observer {

    private static String receivingString = "{\"params\":{\"device\":{\"uuid\":\"0009E5001571\"},\"netSettings\":{\"interface\":{\"name\":\"eth0\",\"configurationMethod\":\"dhcp\"}},\"ttl\":1},\"id\":\"TEST_UUID\",\"jsonrpc\":\"2.0\",\"method\":\"configure\"}";
    private static String sendingString = "{\"id\":\"TEST_UUID\",\"jsonrpc\":\"2.0\",\"result\":0}";

    private JsonParser parser;
    private static JsonElement shouldReceive;

    public FakeDeviceEmulator() {
        this.parser = new JsonParser();
        shouldReceive = this.parser.parse(receivingString);
    }

    @Override
    public void update(Observable o, Object obj) {
        if (obj instanceof String) {
            String msg = (String) obj;
            JsonElement received = parser.parse(msg);

            if (shouldReceive.equals(received)) {
                setChanged();
                notifyObservers(sendingString);
            }
        }

    }

}
