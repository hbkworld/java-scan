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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.announce.AnnounceParams;
import com.hbm.devices.scan.announce.AnnounceDeserializer;
import com.hbm.devices.scan.announce.Announce;
import com.hbm.devices.scan.announce.DefaultGateway;
import com.hbm.devices.scan.announce.Device;
import com.hbm.devices.scan.announce.IPEntry;
import com.hbm.devices.scan.announce.Interface;
import com.hbm.devices.scan.announce.NetSettings;
import com.hbm.devices.scan.announce.Router;
import com.hbm.devices.scan.announce.ServiceEntry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnounceDeserializerTest {

    private Announce announce;
    private FakeMessageReceiver fsmmr;

    @BeforeEach
    public void setUp() {
        announce = null;
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
        fsmmr.addObserver(parser);
        parser.addObserver(new Observer() {
            @Override
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
        assertNotNull(announce, "No Announce object after correct message");
    }

    @Test
    public void parseInvalidJsonMessage() {
        fsmmr.emitInvalidJsonMessage();
        assertNull(announce, "Got Announce object after invalid message");
    }

    @Test
    public void parseNotAnnounceMethodMessage() {
        fsmmr.emitNotAnnounceMessage();
        assertNull(announce, "Got Announce object from message with method that's not an announce");
    }

    @Test
    public void parseEmptyMessage() {
        fsmmr.emitEmptyString();
        assertNull(announce, "Got Announce object after empty message");
    }

    @Test
    public void parseNullMessage() {
        fsmmr.emitNull();
        assertNull(announce, "Got Announce object after null");
    }

    @Test
    public void parseMissingDeviceMessage() {
        fsmmr.emitMissingDeviceMessage();
        assertNull(announce, "Got Announce from message without device");
    }

    @Test
    public void parseEmptyDeviceUuidMessage() {
        fsmmr.emitEmptyDeviceUuidMessage();
        assertNull(announce, "Got Announce from message with empty UUID");
    }

    @Test
    public void parseMissingParamsMessage() {
        fsmmr.emitMissingParamsMessage();
        assertNull(announce, "Got Announce from message without params");
    }

    @Test
    public void parseNoInterfaceNameMessage() {
        fsmmr.emitNoInterfaceNameMessage();
        assertNull(announce, "Got Announce from message without interface name");
    }

    @Test
    public void parseEmptyInterfaceNameMessage() {
        fsmmr.emitEmptyInterfaceNameMessage();
        assertNull(announce, "Got Announce from message with empty interface name");
    }

    @Test
    public void parseInterfaceNameIsNumberMessage() {
        fsmmr.emitInterfaceNameIsNumberMessage();
        assertNull(announce, "Got Announce from message with interface name is a number");
    }

    @Test
    public void parseInterfaceNameIsObjectMessage() {
        fsmmr.emitInterfaceNameIsObjectMessage();
        assertNull(announce, "Got Announce from message with interface name is an object");
    }

    @Test
    public void parseNoInterfaceDescriptionMessage() {
        fsmmr.emitNoInterfaceDescriptionMessage();
        assertNotNull(announce, "Got no Announce object from message with a missing (but optional) interface description");
    }

    @Test
    public void parseInterfaceDescriptionIsNumberMessage() {
        fsmmr.emitInterfaceDescriptionIsNumber();
        assertNull(announce, "Got an Announce object from message with a wrong interface description type (number)");
    }

    @Test
    public void parseNoInterfaceTypeMessage() {
        fsmmr.emitNoInterfaceTypeMessage();
        assertNotNull(announce, "Got no Announce object from message with a missing (but optional) interface type");
    }

    @Test
    public void parseInterfaceTypeIsNumberMessage() {
        fsmmr.emitInterfaceTypeIsNumberMessage();
        assertNull(announce, "Got an Announce object from message with a wrong interface type (number)");
    }

    @Test
    public void parseNoInterfaceMessage() {
        fsmmr.emitNoInterfaceMessage();
        assertNull(announce, "Got Announce from message without interface");
    }

    @Test
    public void parseNoNetSettingsMessage() {
        fsmmr.emitNoNetSettingsMessage();
        assertNull(announce, "Got Announce from message without network settings");
    }

    @Test
    public void parseMissingRouterUuidMessage() {
        fsmmr.emitMissingRouterUuidMessage();
        assertNull(announce, "Got Announce from message without router UUID");
    }

    @Test
    public void parseEmptyRouterUuidMessage() {
        fsmmr.emitEmptyRouterUuidMessage();
        assertNull(announce, "Got Announce from message with empty router UUID");
    }

    @Test
    public void parseMissingTypeMessage() {
        fsmmr.emitMissingMethodMessage();
        assertNull(announce, "Got Announce message without type");
    }
    
    @Test
    public void illegalIpv4AddressTest() {
        fsmmr.emitIllegalIpv4();
        assertNull(announce, "Got Announce object for illegal IPv4 address");
    }

    @Test
    public void illegalIpv6AddressTest() {
        fsmmr.emitIllegalIpv6();
        assertNull(announce, "Got Announce object for illegal IPv6 address");
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
        assertNotNull(announce, "No Announce object after correct message");

        assertEquals(announce.getJsonrpc(), jsonRpcVersion, "JSON-RPC versions not equal");
        assertEquals(announce.getMethod(), jsonRpcMethod, "JSON-RPC methods not equal");
        AnnounceParams checkAnnounceParams = announce.getParams();
        
        assertEquals(checkAnnounceParams.getExpiration(), expire, "expiration does not match");
        assertEquals(checkAnnounceParams.getApiVersion(), apiVersionString, "API version does not match");

        Device checkDevice = checkAnnounceParams.getDevice();
        assertEquals(checkDevice.getFamilyType(), familyTypeString, "family type does not match");
        assertEquals(checkDevice.getFirmwareVersion(), fwVersionString, "firmware version does not match");
        assertEquals(checkDevice.getHardwareId(), hwIdString, "Hardware ID does not match");
        assertEquals(checkDevice.getName(), nameString, "device name does not match");
        assertEquals(checkDevice.getType(), typeString, "device type does not match");
        assertEquals(checkDevice.getLabel(), labelString, "device label does not match");
        assertEquals(checkDevice.getUuid(), uuidString, "device uuid does not match");
        assertEquals(checkDevice.isRouter(), isRouter, "isRouter entry does not match");

        NetSettings checkNetSettings = checkAnnounceParams.getNetSettings();
        DefaultGateway checkDefaultGateway = checkNetSettings.getDefaultGateway();
        assertEquals(checkDefaultGateway.getIpv4Address(), gwAddressString, "Gateway addres does not match");
        Interface checkIface = checkNetSettings.getInterface();
        assertEquals(checkIface.getName(), ifaceNameString, "Interface name does not match");
        assertEquals(checkIface.getType(), ifaceType, "Interface type does not match");
        assertEquals(checkIface.getDescription(), ifDescriptionString, "Interface description does not match");
        
        Iterable<IPEntry> checkIPEntries = checkIface.getIPList();
        IPEntry entry = findIpInList(checkIPEntries, ipv4Address);
        assertNotNull(entry, "IPv4 address does not match");
        try {
            assertEquals(entry.getPrefix(), calculatePrefix(InetAddress.getByName(ipv4Mask)), "IPv4 prefix does not match");
        } catch (UnknownHostException ex) {
            Logger.getLogger(AnnounceDeserializerTest.class.getName()).log(Level.SEVERE, "Could not convert netmask", ex);
        }
        
        entry = findIpInList(checkIPEntries, ipv6Address);
        assertNotNull(entry, "IPv6 address does not match");

        assertEquals(entry.getPrefix(), ipv6Prefix, "IPv6 prefix does not match");
        
        Router checkRouter = checkAnnounceParams.getRouter();
        assertEquals(checkRouter.getUuid(), routerUUID, "router uuid entry does not match");
        Iterable<ServiceEntry> checkServices = checkAnnounceParams.getServices();
        for (ServiceEntry s : checkServices) {
            String serviceType = s.getType();
            assertTrue(serviceType.equals(httpType) || serviceType.equals(streamType), "Service type neither http nor streaming");
            int port = s.getPort(); 
            if (serviceType.equals(httpType)) {
                assertEquals(port, httpPort, "http port does not match");
            } else {
                assertEquals(port, streamPort, "streaming port does not match");
            }
        }
    }
    
    private static IPEntry findIpInList(Iterable<IPEntry> list, String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            for (IPEntry entry : list) {
                if (entry.getAddress().equals(address)) {
                    return entry;
                }
            }
            
            return null;
        } catch (UnknownHostException ex) {
            return null;
        }
    }
    
    private static int calculatePrefix(InetAddress announceNetmask) {
        final byte[] address = announceNetmask.getAddress();
        final int length = address.length;
        int prefix = 0;
        for (int i = 0; i < length; i++) {
            prefix += Integer.bitCount(address[i] & 0xff);
        }
    
        return prefix;
    }
}
