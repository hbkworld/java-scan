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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hbm.devices.scan.configure.ConfigurationSerializer;
import com.hbm.devices.scan.configure.FakeMulticastSender;
import com.hbm.devices.scan.configure.ConfigurationRequest;
import com.hbm.devices.scan.configure.ConfigurationDevice;
import com.hbm.devices.scan.configure.ConfigurationInterface;
import com.hbm.devices.scan.configure.ConfigurationInterface.Method;
import com.hbm.devices.scan.configure.ConfigurationNetSettings;
import com.hbm.devices.scan.configure.ConfigurationParams;

public class ConfigurationSerializerTest {

    private FakeMulticastSender fs;
    private ConfigurationSerializer cs;
    private JsonParser parser;

    @Before
    public void setUp() {
        fs = new FakeMulticastSender();
        cs = new ConfigurationSerializer(fs);
        parser = new JsonParser();
    }

    @Test
    public void parseCorrectConfig() {
        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP, null));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);
        ConfigurationRequest conf = new ConfigurationRequest(configParams, "TEST-UUID");
 
        try {
            cs.sendConfiguration(conf);
        } catch (IOException e) {
            fail("Got IOException during test: " + e);
        }
        String correctOutParsed = "{\"params\":{\"device\":{\"uuid\":\"0009E5001571\"},\"netSettings\":{\"interface\":{\"name\":\"eth0\",\"configurationMethod\":\"dhcp\"}},\"ttl\":1},\"id\":\"TEST-UUID\",\"jsonrpc\":\"2.0\",\"method\":\"configure\"}";
        JsonElement correct = parser.parse(correctOutParsed);
        JsonElement sent = parser.parse(fs.getLastSent());
        assertEquals("Configuration request and check string are not equal", sent, correct);
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseNullConfigure() {
        try {
            cs.sendConfiguration(null);
        } catch (IOException e) {
        }
    }

    @Test
    public void closeTest() {
        cs.close();
        assertTrue("Sender was not closed", cs.isClosed());
    }
}
