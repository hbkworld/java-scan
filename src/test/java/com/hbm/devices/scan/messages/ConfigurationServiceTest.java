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

package com.hbm.devices.scan.messages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hbm.devices.scan.configure.ConfigurationCallback;
import com.hbm.devices.scan.configure.ConfigurationSerializer;
import com.hbm.devices.scan.configure.ConfigurationService;
import com.hbm.devices.scan.configure.FakeDeviceEmulator;
import com.hbm.devices.scan.configure.FakeMulticastSender;
import com.hbm.devices.scan.messages.ConfigurationInterface.Method;

public class ConfigurationServiceTest {
    private ResponseDeserializer messageParser;
    private JsonParser parser;
    private ConfigurationCallback cb;

    private boolean success;
    private boolean error;
    private boolean timeout;

    @Before
    public void setUp() {
        this.messageParser = new ResponseDeserializer();
        this.parser = new JsonParser();
        success = false;
        timeout = false;
        error = false;

        cb = new ConfigurationCallback() {
            public void onTimeout(long t) {
                synchronized(this) {
                    timeout = true;
                    this.notifyAll();
                }
            }
            public void onSuccess(Response response) {
                synchronized(this) {
                    success = true;
                    this.notifyAll();
                }
            }
            public void onError(Response response) {
                synchronized(this) {
                    error = true;
                    this.notifyAll();
                }
            }
        };
    }

    @Test
    public void sendingTest() {
        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        FakeMulticastSender fakeSender = new FakeMulticastSender();
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeSender);
        ConfigurationService service = new ConfigurationService(sender, messageParser);
        try {
            service.sendConfiguration(configParams, "TEST_UUID", cb, 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String testString = "{\"params\":{\"device\":{\"uuid\":\"0009E5001571\"},\"netSettings\":{\"interface\":{\"name\":\"eth0\",\"configurationMethod\":\"dhcp\"}},\"ttl\":1},\"id\":\"TEST_UUID\",\"jsonrpc\":\"2.0\",\"method\":\"configure\"}";
        JsonElement testMessage = parser.parse(testString);
        JsonElement sent = parser.parse(fakeSender.getLastSent());
        assertEquals("Sent message and test message are not equal", sent, testMessage);
    }

    @Test
    public void testCloseWithoutOutstandingResponses() {
        FakeMulticastSender fakeSender = new FakeMulticastSender();
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeSender);
        ConfigurationService service = new ConfigurationService(sender, messageParser);
        service.close();
        assertTrue("Service was not closed", service.isClosed());
    }

    @Test
    public void sendingAndReceivingTest() {

        final String queryID = "test-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        try {
            service.sendConfiguration(configParams, queryID, cb, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("No success response received", success && !error && !timeout);
        assertFalse("Service is still waiting for responses", service.awaitingResponse());
    }

    @Test(timeout = 200)
    public void checkTimeout() {

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        FakeMulticastSender fakeSender = new FakeMulticastSender();
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeSender);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        synchronized(cb) {
            try {
                service.sendConfiguration(configParams, cb, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!timeout && !success && !error) {
                try {
                    cb.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        assertTrue("Haven't got timeout", !success && !error && timeout);
        assertFalse("Service is still waiting for responses", service.awaitingResponse());
    }

    @Test(timeout=100)
    public void testNoResponseID() {

        final String queryID = "no-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        synchronized(cb) {
            try {
                service.sendConfiguration(configParams, queryID, cb, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!timeout && !success && !error) {
                try {
                    cb.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        assertTrue("Illegal response not ignored", timeout && !success && !error);
        service.close();
    }

    @Test(timeout=100)
    public void testEmptyResponseID() {

        final String queryID = "empty-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        synchronized(cb) {
            try {
                service.sendConfiguration(configParams, queryID, cb, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!timeout && !success && !error) {
                try {
                    cb.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        assertTrue("Illegal response not ignored", timeout && !success && !error);
        service.close();
    }

    @Test(timeout=100)
    public void testErrorResponse() {

        final String queryID = "error";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        synchronized(cb) {
            try {
                service.sendConfiguration(configParams, queryID, cb, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!timeout && !success && !error) {
                try {
                    cb.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        assertTrue("Illegal response not ignored", error && !timeout && !success);
        service.close();
    }
}
