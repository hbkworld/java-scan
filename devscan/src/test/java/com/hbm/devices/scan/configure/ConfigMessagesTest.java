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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ConfigMessagesTest {

    @Test
    public void createConfigureNullParams() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationRequest conf = new ConfigurationRequest(null, "1234");
            fail("Not failed despite no config params given");
        });
    }

    @Test
    public void parseNullUUID() {
        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", ConfigurationInterface.Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationRequest conf = new ConfigurationRequest(configParams, null);
            fail("Not failed despite no query ID given");
        });
    }

    @Test
    public void parseEmptyUUID() {
        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", ConfigurationInterface.Method.DHCP));
        ConfigurationParams configParams = new ConfigurationParams(device, settings);
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationRequest conf = new ConfigurationRequest(configParams, "");
            fail("Not failed despite empty query ID given");
        });
    }

    @Test
    public void parseInvalidTtl() {
        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", ConfigurationInterface.Method.DHCP));
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationParams configParams = new ConfigurationParams(device, settings, 0);
            fail("Not failed despite of illegal ttl");
        });
    }

    @Test
    public void constructCorrectConfig() {
        final String ip = "10.1.2.3";
        final String netMask = "255.255.0.0";
        final String gw = "10.3.4.5";
        final String interfaceName = "eth0";
        final ConfigurationInterface.Method configMethod = ConfigurationInterface.Method.MANUAL;
        final String deviceUUID = "0009E5001571";
        final int ttl = 2;
        final String queryID = "12345";

        IPv4EntryManual entry = new IPv4EntryManual(ip, netMask);
        ConfigurationInterface iface = new ConfigurationInterface(interfaceName, configMethod, entry);
        ConfigurationDefaultGateway gateway = new ConfigurationDefaultGateway(gw);
        ConfigurationNetSettings settings = new ConfigurationNetSettings(iface, gateway);
        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        ConfigurationParams configParams = new ConfigurationParams(device, settings, ttl);
        ConfigurationRequest conf = new ConfigurationRequest(configParams, queryID);

        assertNotNull(iface, "ConfigurationInterface constructor failed");
        assertEquals(conf.getQueryId(), queryID, "Query ID's not equal");
        assertEquals(conf.getParams().getTtl(), ttl, "ttl not equal");
        assertEquals(conf.getParams().getDevice().getUUID(), deviceUUID, "device UUID not equal");
        assertEquals(conf.getParams().getNetSettings().getInterface().getName(), interfaceName, "interface name not equal");
        assertEquals(conf.getParams().getNetSettings().getDefaultGateway().getIpv4Address(), gw, "default gateway not equal");
        assertEquals(conf.getParams().getNetSettings().getInterface().getConfigurationMethod(), configMethod.toString(), "config method not equal");
        assertEquals(conf.getParams().getNetSettings().getInterface().getIPv4().getAddress(), ip, "config IP's not equal");
        assertEquals(conf.getParams().getNetSettings().getInterface().getIPv4().getNetmask(), netMask, "config netmask not equal");
    }

    @Test
    public void parseNullDevice() {
        ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", ConfigurationInterface.Method.DHCP));
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationParams configParams = new ConfigurationParams(null, settings);
            fail("Method didn't throw expected IllegalArgumentException");
        });
    }

    @Test
    public void parseEmptyUuid() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationDevice device = new ConfigurationDevice("");
            fail("Not failed despite empty device uuid");
        });
    }

    @Test
    public void parseNoUuid() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationDevice device = new ConfigurationDevice(null);
            fail("Not failed despite no device uuid given");
        });
    }

    @Test
    public void parseNullNetSettings() {
        ConfigurationDevice device = new ConfigurationDevice("0009E5001571");
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationParams configParams = new ConfigurationParams(device, null);
            fail("Method didn't throw expected IllegalArgumentException");
        });
    }

    @Test
    public void parseNullInterface() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationNetSettings settings = new ConfigurationNetSettings(null);
            fail("Method didn't throw expected IllegalArgumentException");
        });
    }

    @Test
    public void parseNoInterfaceConfigMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationInterface iface = new ConfigurationInterface("eth0", null);
            fail("Method didn't fail with no given configuration method");
        });
    }

    @Test
    public void parseNullInterfaceName() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationInterface iface = new ConfigurationInterface(null, ConfigurationInterface.Method.DHCP);
            fail("Method didn't fail with no interface name");
        });
    }

    @Test
    public void parseEmptyInterfaceName() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationInterface iface = new ConfigurationInterface("", ConfigurationInterface.Method.DHCP);
            fail("Method didn't failed with empty interface name");
        });
    }

    @Test
    public void parseManualAndNoIp() {
        assertThrows(IllegalArgumentException.class, () -> {
            ConfigurationInterface iface = new ConfigurationInterface("eth0", ConfigurationInterface.Method.MANUAL, null);
            fail("Method didn't throw expected IllegalArgumentException");
        });
    }

    @Test
    public void parseManualWithIp() {
        IPv4EntryManual entry = new IPv4EntryManual("10.1.2.3", "255.255.0.0");
        ConfigurationInterface iface = new ConfigurationInterface("eth0", ConfigurationInterface.Method.MANUAL, entry);
        assertNotNull(iface, "ConfigurationInterface constructor failed");
    }

    @Test
    public void parseIPv4ManualAndNoAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            IPv4EntryManual entry = new IPv4EntryManual(null, "bla");
            fail("Method didn't throw expected IllegalArgumentException");
        });
    }

    @Test
    public void parseIPv4ManualAndEmtpyAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            IPv4EntryManual entry = new IPv4EntryManual("", "bla");
            fail("Method didn't throw expected IllegalArgumentException");
        });
    }

    @Test
    public void parseIPv4ManualCorrect() {
        final String address = "foo";
        final String netmask = "bar";
        IPv4EntryManual entry = new IPv4EntryManual(address, netmask);
        assertEquals(address, entry.getAddress(), "addresses do not match");
        assertEquals(netmask, entry.getNetmask(), "netmasks do not match");
    }

    @Test
    public void parseIPv4ManualAndNoNetmask() {
        assertThrows(IllegalArgumentException.class, () -> {
            IPv4EntryManual entry = new IPv4EntryManual("bla", null);
            fail("Method didn't throw expected IllegalArgumentException");
        });
    }

    @Test
    public void parseIPv4ManualAndEmptyNetmask() {
        assertThrows(IllegalArgumentException.class, () -> {
            IPv4EntryManual entry = new IPv4EntryManual("bla", "");
            fail("Method didn't throw expected IllegalArgumentException");
        });
    }
}
