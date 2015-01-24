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
    private JsonElement shouldReceive;

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
