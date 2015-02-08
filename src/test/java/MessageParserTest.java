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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.messages.CommunicationPath;
import com.hbm.devices.scan.messages.MessageParser;
import com.hbm.devices.scan.messages.Response;

public class MessageParserTest {

    private CommunicationPath cp;
    private Response res;
    private FakeMessageReceiver fsmmr;

    @Before
    public void setUp() {
        fsmmr = new FakeMessageReceiver();
        MessageParser jf = new MessageParser();
        fsmmr.addObserver(jf);
        jf.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof CommunicationPath) {
                    cp = (CommunicationPath) arg;
                } else if (arg instanceof Response) {
                    res = (Response) arg;
                }
            }
        });
    }

    @Test
    public void parseCorrectMessage() {
        fsmmr.emitSingleCorrectMessage();
        assertNotNull("No CommunictionPath object after correct message", cp);
    }

    @Test
    public void parseInvalidJsonMessage() {
        fsmmr.emitInvalidJsonMessage();
        assertNull("Got CommunicationPath object after invalid message", cp);
    }

    @Test
    public void parseNotAnnounceMethodMessage() {
        fsmmr.emitNotAnnounceMessage();
        assertNull("Got CommunicationPath object from message with method that's not an announce", cp);
    }

    @Test
    public void parseEmptyMessage() {
        fsmmr.emitEmptyString();
        assertNull("Got CommunicationPath object after empty message", cp);
    }

    @Test
    public void parseNullMessage() {
        fsmmr.emitNull();
        assertNull("Got CommunicationPath object after null", cp);
    }

    @Test
    public void parseMissingDeviceMessage() {
        fsmmr.emitMissingDeviceMessage();
        assertNull("Got CommunicationPath from message without device", cp);
    }

    @Test
    public void parseMissingDeviceUuidMessage() {
        fsmmr.emitMissingDeviceUuidMessage();
        assertNull("Got CommunicationPath from message without UUID", cp);
    }

    @Test
    public void parseMissingParamsMessage() {
        fsmmr.emitMissingParamsMessage();
        assertNull("Got CommunicationPath from message without params", cp);
    }

    @Test
    public void parseNoInterfaceNameMessage() {
        fsmmr.emitNoInterfaceNameMessage();
        assertNull("Got CommunicationPath from message without interface name", cp);
    }

    @Test
    public void parseNoInterfaceMessage() {
        fsmmr.emitNoInterfaceMessage();
        assertNull("Got CommunicationPath from message without interface", cp);
    }

    @Test
    public void parseNoNetSettingsMessage() {
        fsmmr.emitNoNetSettingsMessage();
        assertNull("Got CommunicationPath from message without network settings", cp);
    }

    @Test
    public void parseMissingRouterUuidMessage() {
        fsmmr.emitMissingRouterUuidMessage();
        assertNull("Got CommunicationPath from message without router UUID", cp);
    }

    @Test
    public void parseCorrectSuccessReponseMessage() {
        fsmmr.emitSingleCorrectSuccessResponseMessage("TEST-UUID");
        assertNotNull("No result object after correct success response", res);
    }

    @Test
    public void parseCorrectErrorReponseMessage() {
        fsmmr.emitSingleCorrectErrorResponseMessage();
        assertNotNull("No result object after correct error response", res);
    }

    @Test
    public void parseErrorAndResultResponseMessage() {
        fsmmr.emitErrorAndResultResponseMessage();
        assertNull("Got result object from response with error and result", res);
    }

    @Test
    public void parseMissingTypeMessage() {
        fsmmr.emitMissingTypeResponseMessage();
        assertNull("Got result object from response with error and result", res);
    }

    @Test
    public void testGetters() {
        final String apiVersionString = "1.0";
        final String familyTypeString = "QuantumX";
        final String fwVersionString = "1.234";
        final String hwIdString = "MX410_R0";
        final String nameString = "MX410 Matthias";
        final String typeString = "MX410";
        final String uuidString = "0009E500123A";
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

        final JsonObject root = new JsonObject();
        root.addProperty("jsonrpc", "2.0");
        root.addProperty("method", "announce");
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
        device.addProperty("uuid", uuidString);

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
        assertNotNull("No CommunictionPath object after correct message", cp);
    }
}
