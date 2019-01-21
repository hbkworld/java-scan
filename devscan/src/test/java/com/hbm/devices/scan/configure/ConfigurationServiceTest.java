/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Hottinger Baldwin Messtechnik GmbH
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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

    @BeforeEach
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
        assertEquals(sent, testMessage, "Sent message and test message are not equal");
    }

    @Test
    public void testCloseWithoutOutstandingResponses() {
        FakeMulticastSender fakeSender = new FakeMulticastSender();
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeSender);
        ConfigurationService service = new ConfigurationService(sender, messageParser);
        service.close();
        assertTrue(service.isClosed(), "Service was not closed");
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

        assertTrue(success && !error && !timeout, "No success response received");
        assertFalse(service.awaitingResponse(), "Service is still waiting for responses");
    }

     public void checkTimeout() {
         assertTimeout(ofMillis(200), () -> {
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
            assertTrue(service.awaitingResponse(), "Service is not waiting for responses");
            synchronized(cb) {
                while (!timeout && !success && !error) {
                    try {
                        cb.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            assertTrue(!success && !error && timeout, "Haven't got timeout");
            assertFalse(service.awaitingResponse(), "Service is still waiting for responses");
         });
     }
 
     @Test
     public void testWrongResponseID() {
         assertTimeout(ofMillis(200), () -> {
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
 
            assertTrue(timeout && !success && !error, "Illegal response not ignored");
            service.close();
         });
     }
 
     @Test
     public void testErrorResponse() {
         assertTimeout(ofMillis(1000), () -> {
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
 
            assertTrue(error && !timeout && !success, "Illegal response not ignored");
            service.close();
         });
     }
 
     @Test
     public void testErrorResponseNoMessage() {
         assertTimeout(ofMillis(1000), () -> {
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
 
            assertTrue(timeout && !error && !success, "Illegal response not ignored");
            service.close();
         });
     }
 
     @Test
     public void testErrorResponseEmptyMessage() {
         assertTimeout(ofMillis(1000), () -> {
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
 
            assertTrue(timeout && !error && !success, "Illegal response not ignored");
            service.close();
         });
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

        assertThrows(IllegalArgumentException.class, () -> {
            service.sendConfiguration(null, queryID, cb, 100);
            fail("Not failed despite no config params given");
        });

        assertTrue(!timeout && !error && !success, "Illegal response not ignored");
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

        assertThrows(IllegalArgumentException.class, () -> {
            service.sendConfiguration(configParams, queryID, cb, 0);
            fail("Not failed despite no config params given");
        });

        assertTrue(!timeout && !error && !success, "Illegal response not ignored");
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

        assertThrows(IllegalArgumentException.class, () -> {
            service.sendConfiguration(configParams, queryID, null, 100);
            fail("Not failed despite no config params given");
        });

        assertTrue(!timeout && !error && !success, "Illegal response not ignored");
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

        assertThrows(IllegalArgumentException.class, () -> {
            service.sendConfiguration(configParams, null, cb, 100);
            fail("Not failed despite no config params given");
        });

        assertTrue(!timeout && !error && !success, "Illegal response not ignored");
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

        assertThrows(IllegalArgumentException.class, () -> {
            service.sendConfiguration(configParams, "", cb, 100);
            fail("Not failed despite no config params given");
        });

        assertTrue(!timeout && !error && !success, "Illegal response not ignored");
        service.close();
    }
}
