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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.announce.Announce;
import com.hbm.devices.scan.announce.AnnounceParams;
import com.hbm.devices.scan.announce.AnnounceDeserializer;
import com.hbm.devices.scan.announce.Announce;
import com.hbm.devices.scan.announce.DefaultGateway;
import com.hbm.devices.scan.announce.Device;
import com.hbm.devices.scan.announce.IPv4Entry;
import com.hbm.devices.scan.announce.IPv6Entry;
import com.hbm.devices.scan.announce.Interface;
import com.hbm.devices.scan.announce.NetSettings;
import com.hbm.devices.scan.announce.Router;
import com.hbm.devices.scan.announce.ServiceEntry;

public class AnnounceDeserializerTest {

    private Announce announce;
    private FakeMessageReceiver fsmmr;

    @Before
    public void setUp() {
        announce = null;
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
        fsmmr.addObserver(parser);
        parser.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof Announce) {
                    announce = (Announce) arg;
                }
            }
        });
    }

    @Test
    public void parseCorrectMessage() {
        fsmmr.emitSingleCorrectMessage();
        assertNotNull("No Announce object after correct message", announce);
    }

    @Test
    public void parseInvalidJsonMessage() {
        fsmmr.emitInvalidJsonMessage();
        assertNull("Got Announce object after invalid message", announce);
    }

    @Test
    public void parseNotAnnounceMethodMessage() {
        fsmmr.emitNotAnnounceMessage();
        assertNull("Got Announce object from message with method that's not an announce", announce);
    }

    @Test
    public void parseEmptyMessage() {
        fsmmr.emitEmptyString();
        assertNull("Got Announce object after empty message", announce);
    }

    @Test
    public void parseNullMessage() {
        fsmmr.emitNull();
        assertNull("Got Announce object after null", announce);
    }

    @Test
    public void parseMissingDeviceMessage() {
        fsmmr.emitMissingDeviceMessage();
        assertNull("Got Announce from message without device", announce);
    }

    @Test
    public void parseEmptyDeviceUuidMessage() {
        fsmmr.emitEmptyDeviceUuidMessage();
        assertNull("Got Announce from message with empty UUID", announce);
    }

    @Test
    public void parseMissingParamsMessage() {
        fsmmr.emitMissingParamsMessage();
        assertNull("Got Announce from message without params", announce);
    }

    @Test
    public void parseNoInterfaceNameMessage() {
        fsmmr.emitNoInterfaceNameMessage();
        assertNull("Got Announce from message without interface name", announce);
    }

    @Test
    public void parseEmptyInterfaceNameMessage() {
        fsmmr.emitEmptyInterfaceNameMessage();
        assertNull("Got Announce from message with empty interface name", announce);
    }

    @Test
    public void parseNoInterfaceMessage() {
        fsmmr.emitNoInterfaceMessage();
        assertNull("Got Announce from message without interface", announce);
    }

    @Test
    public void parseNoNetSettingsMessage() {
        fsmmr.emitNoNetSettingsMessage();
        assertNull("Got Announce from message without network settings", announce);
    }

    @Test
    public void parseMissingRouterUuidMessage() {
        fsmmr.emitMissingRouterUuidMessage();
        assertNull("Got Announce from message without router UUID", announce);
    }

    @Test
    public void parseEmptyRouterUuidMessage() {
        fsmmr.emitEmptyRouterUuidMessage();
        assertNull("Got Announce from message with empty router UUID", announce);
    }

    @Test
    public void parseMissingTypeMessage() {
        fsmmr.emitMissingMethodMessage();
        assertNull("Got Announce message without type", announce);
    }

    @Test
    public void testGetters() {
        final String apiVersionString = "1.0";
        final String familyTypeString = "QuantumX";
        final String fwVersionString = "1.234";
        final String hwIdString = "MX410_R0";
        final String nameString = "MX410 Matthias";
        final String typeString = "MX410";
        final String labelString = "MX410B";
        final String uuidString = "0009E500123A";
        final boolean isRouter = true;
        final String ifaceNameString = "eth0";
        final String ifaceType = "ethernet";
        final String gwAddressString = "172.19.169.254";
        final String ifDescriptionString = "ethernet backplane side";
        final String ipv4Address = "172.19.192.57";
        final String ipv4Mask = "255.255.0.0";
        final String ipv6Address = "fe80::209:e5ff:fe00:123a";
        final int ipv6Prefix = 64;
        final int expire = 15;
        final String routerUUID = "0009E50013E9";
        final String streamType = "daqStream";
        final int streamPort = 7411;
        final String httpType = "http";
        final int httpPort = 80;
        final String jsonRpcVersion = "2.0";
        final String jsonRpcMethod = "announce";

        final JsonObject root = new JsonObject();
        root.addProperty("jsonrpc", jsonRpcVersion);
        root.addProperty("method", jsonRpcMethod);
        final JsonObject params = new JsonObject();
        root.add("params", params);
        params.addProperty("expiration", expire);
        params.addProperty("apiVersion", apiVersionString);
        
        final JsonObject device = new JsonObject();
        params.add("device", device);
        device.addProperty("familyType", familyTypeString);
        device.addProperty("firmwareVersion", fwVersionString);
        device.addProperty("hardwareId", hwIdString);
        device.addProperty("name", nameString);
        device.addProperty("type", typeString);
        device.addProperty("label", labelString);
        device.addProperty("uuid", uuidString);
        device.addProperty("isRouter", isRouter);

        final JsonObject netSettings = new JsonObject();
        params.add("netSettings", netSettings);
        final JsonObject defaultGW = new JsonObject();
        netSettings.add("defaultGateway", defaultGW);
        defaultGW.addProperty("ipv4Address", gwAddressString);
        final JsonObject iface = new JsonObject();
        netSettings.add("interface", iface);
        iface.addProperty("name", ifaceNameString);
        iface.addProperty("type", ifaceType);
        iface.addProperty("description", ifDescriptionString);
        JsonArray ipv4Addresses = new JsonArray();
        iface.add("ipv4", ipv4Addresses);
        final JsonObject ipv4Entry = new JsonObject();
        ipv4Addresses.add(ipv4Entry);
        ipv4Entry.addProperty("address", ipv4Address);
        ipv4Entry.addProperty("netmask", ipv4Mask);
        JsonArray ipv6Addresses = new JsonArray();
        iface.add("ipv6", ipv6Addresses);
        final JsonObject ipv6Entry = new JsonObject();
        ipv6Addresses.add(ipv6Entry);
        ipv6Entry.addProperty("address", ipv6Address);
        ipv6Entry.addProperty("prefix", ipv6Prefix);

        final JsonObject router = new JsonObject();
        params.add("router", router);
        router.addProperty("uuid", routerUUID);

        final JsonArray services = new JsonArray();
        params.add("services", services);
        JsonObject streamService = new JsonObject();
        services.add(streamService);
        streamService.addProperty("type", streamType);
        streamService.addProperty("port", streamPort);
        JsonObject httpService = new JsonObject();
        services.add(httpService);
        httpService.addProperty("type", httpType);
        httpService.addProperty("port", httpPort);

        final Gson gson = new Gson();
        fsmmr.emitString(gson.toJson(root));
        assertNotNull("No Announce object after correct message", announce);

        assertEquals("JSON-RPC versions not equal", announce.getJsonrpc(), jsonRpcVersion);
        assertEquals("JSON-RPC methods not equal", announce.getMethod(), jsonRpcMethod);
        AnnounceParams checkAnnounceParams = announce.getParams();
        
        assertEquals("expiration does not match", checkAnnounceParams.getExpiration(), expire);
        assertEquals("API version does not match", checkAnnounceParams.getApiVersion(), apiVersionString);

        Device checkDevice = checkAnnounceParams.getDevice();
        assertEquals("family type does not match", checkDevice.getFamilyType(), familyTypeString);
        assertEquals("firmware version does not match", checkDevice.getFirmwareVersion(), fwVersionString);
        assertEquals("Hardware ID does not match", checkDevice.getHardwareId(), hwIdString);
        assertEquals("device name does not match", checkDevice.getName(), nameString);
        assertEquals("device type does not match", checkDevice.getType(), typeString);
        assertEquals("device label does not match", checkDevice.getLabel(), labelString);
        assertEquals("device uuid does not match", checkDevice.getUuid(), uuidString);
        assertEquals("isRouter entry does not match", checkDevice.isRouter(), isRouter);

        NetSettings checkNetSettings = checkAnnounceParams.getNetSettings();
        DefaultGateway checkDefaultGateway = checkNetSettings.getDefaultGateway();
        assertEquals("Gateway addres does not match", checkDefaultGateway.getIpv4Address(), gwAddressString);
        Interface checkIface = checkNetSettings.getInterface();
        assertEquals("Interface name does not match", checkIface.getName(), ifaceNameString);
        assertEquals("Interface type does not match", checkIface.getType(), ifaceType);
        assertEquals("Interface description does not match", checkIface.getDescription(), ifDescriptionString);
        Iterable<IPv4Entry> checkIPv4Entries = checkIface.getIPv4();
        IPv4Entry checkIPv4 = checkIPv4Entries.iterator().next();
        assertEquals("IPv4 address does not match", checkIPv4.getAddress(), ipv4Address);
        assertEquals("IPv4 netmask does not match", checkIPv4.getNetmask(), ipv4Mask);
        Iterable<IPv6Entry> checkIPv6Entries = checkIface.getIPv6();
        IPv6Entry checkIPv6 = checkIPv6Entries.iterator().next();
        assertEquals("IPv6 address does not match", checkIPv6.getAddress(), ipv6Address);
        assertEquals("IPv6 prefix does not match", checkIPv6.getPrefix(), ipv6Prefix);
        
        Router checkRouter = checkAnnounceParams.getRouter();
        assertEquals("router uuid entry does not match", checkRouter.getUuid(), routerUUID);

        Iterable<ServiceEntry> checkServices = checkAnnounceParams.getServices();
        for (ServiceEntry s : checkServices) {
            String serviceType = s.getType();
            assertTrue("Service type neither http nor streaming", serviceType.equals(httpType) || serviceType.equals(streamType));
            int port = s.getPort(); 
            if (serviceType.equals(httpType)) {
                assertEquals("http port does not match", port, httpPort);
            } else {
                assertEquals("streaming port does not match", port, streamPort);
            }
        }
    }
}
