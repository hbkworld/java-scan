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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

// import com.google.gson.JsonElement;
// import com.google.gson.JsonParser;
import com.hbm.devices.scan.configure.ConfigurationSender;
// import com.hbm.devices.configure.Device;
import com.hbm.devices.scan.configure.FakeMulticastSender;
// import com.hbm.devices.configure.Interface;
// import com.hbm.devices.configure.NetSettings;
// import com.hbm.devices.configure.Noticeable;
// import com.hbm.devices.scan.messages.MissingDataException;
// import com.hbm.devices.scan.messages.Configure;
// import com.hbm.devices.scan.messages.ConfigureParams;
// import com.hbm.devices.scan.messages.Interface.Method;

public class ConfigParserTest {

    private FakeMulticastSender fs;
 
    private ConfigurationSender cs;
 
    //private JsonParser parser;
 
    private Exception exception;
 
    @Before
    public void setup() {
        fs = new FakeMulticastSender();
 
        //cs = new ConfigurationSender(fs);
 
        //parser = new JsonParser();
 
        this.exception = null;
    }
// 
//     @Test
//     public void parseCorrectConfig() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         Configure conf = new Configure(configParams, "TEST-UUID");
// 
//         cp.update(null, conf);
//         String correctOutParsed = "{\"params\":{\"device\":{\"uuid\":\"0009E5001571\"},\"netSettings\":{\"interface\":{\"name\":\"eth0\",\"configurationMethod\":\"dhcp\"}},\"ttl\":1},\"id\":\"TEST-UUID\",\"jsonrpc\":\"2.0\",\"method\":\"configure\"}";
//         JsonElement correct = parser.parse(correctOutParsed);
//         JsonElement sent = parser.parse(fs.getLastSent());
//         assertTrue(sent.equals(correct));
//     }
// 
//     @Test
//     public void parseNullConfigure() {
//         cp.update(null, null);
//         assertTrue(this.exception instanceof NullPointerException);
//     }
// 
//     @Test
//     public void parseNullParams() {
//         Configure conf = new Configure(null, "TEST-UUID");
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullUUID() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         Configure conf = new Configure(configParams, null);
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullDevice() {
//         NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(null, settings);
//         Configure conf = new Configure(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof IllegalArgumentException);
//     }
// 
//     @Test
//     public void parseNoDevice() {
//         Device device = new Device("");
//         NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         Configure conf = new Configure(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertNull(fs.getLastSent());
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullNetSettings() {
//         Device device = new Device("0009E5001571");
//         ConfigureParams configParams = new ConfigureParams(device, null);
//         Configure conf = new Configure(configParams, null);
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullInterface() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(null);
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         Configure conf = new Configure(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNullInterfaceName() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(new Interface(null, Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         Configure conf = new Configure(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         assertTrue(this.exception instanceof MissingDataException);
//     }
// 
//     @Test
//     public void parseNoInterfaceName() {
//         Device device = new Device("0009E5001571");
//         NetSettings settings = new NetSettings(new Interface("", Method.DHCP, null));
//         ConfigureParams configParams = new ConfigureParams(device, settings);
//         Configure conf = new Configure(configParams, "TEST-UUID");
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
//         Configure conf = new Configure(configParams, "TEST-UUID");
//         cp.update(null, conf);
//         System.out.println(this.exception);
//         assertTrue(this.exception instanceof MissingDataException);
//     }

}
