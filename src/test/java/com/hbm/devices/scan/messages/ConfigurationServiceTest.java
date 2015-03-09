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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static com.hbm.devices.scan.configure.ConfigurationInterface.Method;

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

    @Rule 
    public ExpectedException exception = ExpectedException.none(); 

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

        try {
            service.sendConfiguration(configParams, cb, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("Service is not waiting for responses", service.awaitingResponse());
        synchronized(cb) {
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

    @Test(timeout=1000)
    public void testWrongResponseID() {

        final String queryID = "wrong-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        synchronized(cb) {
            try {
                service.sendConfiguration(configParams, queryID, cb, 100);
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

    @Test(timeout=1000)
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
                service.sendConfiguration(configParams, queryID, cb, 100);
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

    @Test(timeout=1000)
    public void testErrorResponseNoMessage() {

        final String queryID = "error-no-message";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        try {
            service.sendConfiguration(configParams, queryID, cb, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized(cb) {
            while (!timeout && !success && !error) {
                try {
                    cb.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        assertTrue("Illegal response not ignored", timeout && !error && !success);
        service.close();
    }

    @Test(timeout=1000)
    public void testErrorResponseEmptyMessage() {

        final String queryID = "error-empty-message";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        synchronized(cb) {
            try {
                service.sendConfiguration(configParams, queryID, cb, 100);
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

        assertTrue("Illegal response not ignored", timeout && !error && !success);
        service.close();
    }

    @Test
    public void testNoConfigParams() {

        final String queryID = "test-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));

        try {
            exception.expect(IllegalArgumentException.class);
            service.sendConfiguration(null, queryID, cb, 100);
            fail("Not failed despite no config params given");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("Illegal response not ignored", !timeout && !error && !success);
        service.close();
    }

    @Test
    public void testWrongTimeout() {

        final String queryID = "test-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        try {
            exception.expect(IllegalArgumentException.class);
            service.sendConfiguration(configParams, queryID, cb, 0);
            fail("Not failed despite no config params given");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("Illegal response not ignored", !timeout && !error && !success);
        service.close();
    }

    @Test
    public void testNoCallback() {

        final String queryID = "test-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        try {
            exception.expect(IllegalArgumentException.class);
            service.sendConfiguration(configParams, queryID, null, 100);
            fail("Not failed despite no config params given");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("Illegal response not ignored", !timeout && !error && !success);
        service.close();
    }

    @Test
    public void testNoQueryID() {

        final String queryID = "test-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        try {
            exception.expect(IllegalArgumentException.class);
            service.sendConfiguration(configParams, null, cb, 100);
            fail("Not failed despite no config params given");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("Illegal response not ignored", !timeout && !error && !success);
        service.close();
    }

    @Test
    public void testEmptyQueryID() {

        final String queryID = "test-id";

        FakeDeviceEmulator fakeDevice = new FakeDeviceEmulator(queryID);
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeDevice);
        
        fakeDevice.addObserver(messageParser);
        ConfigurationService service = new ConfigurationService(sender, messageParser);

        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);

        try {
            exception.expect(IllegalArgumentException.class);
            service.sendConfiguration(configParams, "", cb, 100);
            fail("Not failed despite no config params given");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue("Illegal response not ignored", !timeout && !error && !success);
        service.close();
    }
}
