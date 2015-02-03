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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hbm.devices.scan.configure.ConfigurationSender;
import com.hbm.devices.scan.configure.FakeMulticastSender;
import com.hbm.devices.scan.messages.ConfigurationRequest;
import com.hbm.devices.scan.messages.ConfigureDevice;
import com.hbm.devices.scan.messages.ConfigureInterface;
import com.hbm.devices.scan.messages.ConfigureNetSettings;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Interface.Method;

public class ConfigParserTest {

    private FakeMulticastSender fs;
    private ConfigurationSender cs;
    private JsonParser parser;

    @Rule
    public ExpectedException exception = ExpectedException.none();
 
    @Before
    public void setup() {
        fs = new FakeMulticastSender();
        cs = new ConfigurationSender(fs);
        parser = new JsonParser();
    }

    @Test
    public void parseCorrectConfig() {
        ConfigureDevice device = new ConfigureDevice("0009E5001571");
        ConfigureNetSettings settings = new ConfigureNetSettings(new ConfigureInterface("eth0", Method.DHCP, null));
        ConfigureParams configParams = new ConfigureParams(device, settings);
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

    @Test
    public void parseNullConfigure() {
        try {
            exception.expect(IllegalArgumentException.class);
            cs.sendConfiguration(null);
        } catch (IOException e) {
        }
    }

    @Test
    public void createConfigureNullParams() {
        exception.expect(IllegalArgumentException.class);
        ConfigurationRequest conf = new ConfigurationRequest(null, "1234");
    }

// 
//     @Test
//     public void parseNullUUID() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         ConfigurationRequest conf = new ConfigurationRequest(configParams, null);
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullDevice() {
//         NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(null, settings);
//         ConfigurationRequest conf = new ConfigurationRequest(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof IllegalArgumentException);
//     }
// 
//     @Test
//     public void parseNoDevice() {
//         Device device = new Device("");
//         NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         ConfigurationRequest conf = new ConfigurationRequest(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertNull(fs.getLastSent());
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullNetSettings() {
//         Device device = new Device("0009E5001571");
//         ConfigureParams configParams = new ConfigureParams(device, null);
//         ConfigurationRequest conf = new ConfigurationRequest(configParams, null);
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullInterface() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(null);
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         ConfigurationRequest conf = new ConfigurationRequest(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullInterfaceName() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(new Interface(null, Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         ConfigurationRequest conf = new ConfigurationRequest(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNoInterfaceName() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(new Interface("", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         ConfigurationRequest conf = new ConfigurationRequest(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertNull(fs.getLastSent());
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseManualAndNoIp() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(new Interface("eth0", Method.MANUAL, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         ConfigurationRequest conf = new ConfigurationRequest(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         System.out.println(this.exception);
//         assertTrue(this.exception instanceof MissingDataException);
//     }

}
