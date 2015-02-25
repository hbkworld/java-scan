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
import com.hbm.devices.scan.messages.ConfigurationDevice;
import com.hbm.devices.scan.messages.ConfigurationInterface;
import com.hbm.devices.scan.messages.ConfigurationInterface.Method;
import com.hbm.devices.scan.messages.ConfigurationNetSettings;
import com.hbm.devices.scan.messages.ConfigurationParams;
import com.hbm.devices.scan.messages.ResponseDeserializer;
import com.hbm.devices.scan.messages.Response;

public class ConfigurationServiceTest {
    private ResponseDeserializer messageParser;
    private JsonParser parser;

    private boolean received;
    private boolean timeout;

    @Before
    public void setUp() {
        this.messageParser = new ResponseDeserializer();
        this.parser = new JsonParser();
        received = false;
        timeout = false;
    }

    @Test
    public void sendingTest() {
        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);
        ConfigurationCallback callback = new ConfigurationCallback() {
            public void onSuccess(Response response) {}
            public void onError(Response response) {}
            public void onTimeout(long timeout) {}
        };

        FakeMulticastSender fakeSender = new FakeMulticastSender();
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeSender);
        ConfigurationService service = new ConfigurationService(sender, messageParser);
        try {
            service.sendConfiguration(configParams, "TEST_UUID", callback, 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String testString = "{\"params\":{\"device\":{\"uuid\":\"0009E5001571\"},\"netSettings\":{\"interface\":{\"name\":\"eth0\",\"configurationMethod\":\"dhcp\"}},\"ttl\":1},\"id\":\"TEST_UUID\",\"jsonrpc\":\"2.0\",\"method\":\"configure\"}";
        JsonElement testMessage = parser.parse(testString);
        JsonElement sent = parser.parse(fakeSender.getLastSent());
        assertEquals("Sent message and test message are not equal", sent, testMessage);
    }

    @Test
    public void testShutdownWithoutOutstandingResponses() {
        FakeMulticastSender fakeSender = new FakeMulticastSender();
        ConfigurationSerializer sender = new ConfigurationSerializer(fakeSender);
        ConfigurationService service = new ConfigurationService(sender, messageParser);
        service.shutdown();
        assertTrue("Service was not shut down", service.isShutdown());
    }

    @Test
    public void sendingAndReceivingTest() {

        ConfigurationCallback cb = new ConfigurationCallback() {
            public void onSuccess(Response response) {
                received = true;
            }
            public void onError(Response response) {
                received = true;
            }
            public void onTimeout(long timeout) {
            }
        };

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

        assertTrue("No response received", received);
        assertFalse("Service is still waiting for responses", service.awaitingResponse());
    }

    @Test(timeout = 200)
    public void CheckTimeout() {

        ConfigurationCallback cb = new ConfigurationCallback() {
            public void onTimeout(long t) {
                synchronized(this) {
                    timeout = true;
                    this.notifyAll();
                }
            }
            public void onSuccess(Response response) {
                synchronized(this) {
                    received = true;
                    this.notifyAll();
                }
            }
            public void onError(Response response) {
                synchronized(this) {
                    received = true;
                    this.notifyAll();
                }
            }
        };

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
            while (!timeout && !received) {
                try {
                    cb.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        assertTrue("Haven't got timeout", !received && timeout);
        assertFalse("Service is still waiting for responses", service.awaitingResponse());
    }
}
