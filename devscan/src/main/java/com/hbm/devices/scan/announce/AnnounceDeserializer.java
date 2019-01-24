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
package com.hbm.devices.scan.announce;

import java.lang.reflect.Type;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import com.hbm.devices.scan.JsonRpc;
import com.hbm.devices.scan.ScanConstants;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class gets JSON announce messages, parses them and notifies
 * {@link Announce} objects.
 * <p>
 * The whole class is designed as a best effort service. So invalid JSON
 * messages, or messages that do not conform to the HBM network discovery and
 * configuration protocol are simply ignored. Users of this class will
 * <em>not</em> get any error messages or exceptions.
 *
 * @since 1.0
 */
public final class AnnounceDeserializer extends Observable implements Observer {

    private final Gson gson;
    private final AnnounceCache announceCache;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    /**
     * Constructs a {@link AnnounceDeserializer} object.
     */
    public AnnounceDeserializer() {
        super();

        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(JsonRpc.class, new JsonRpcDeserializer());
        builder.registerTypeAdapter(AnnounceParams.class, new AnnounceParamsDeserializer());
        Type serviceListType = new TypeToken<List<ServiceEntry>>() {
        }.getType();
        builder.registerTypeAdapter(serviceListType, new ServiceDeserializer());
        builder.registerTypeAdapter(Interface.class, new InterfaceDeserializer());

        //  builder.registerTypeAdapter(IPEntry.class, new IPv4Deserializer());
        gson = builder.create();

        this.announceCache = new AnnounceCache();
    }

    AnnounceCache getCache() {
        return this.announceCache;
    }

    @Override
    public void update(Observable observable, Object arg) {
        final String message = (String) arg;
        Announce announce = announceCache.get(message);
        if (announce == null) {
            try {
                announce = (Announce) gson.fromJson(message, JsonRpc.class);
                if (announce != null) {
                    announce.identifyCommunicationPath();
                    if (announce.getParams().getExpiration() < 0) {
                        return;
                    }
                    announceCache.put(message, announce);
                    setChanged();
                    notifyObservers(announce);
                }
            } catch (JsonSyntaxException e) {
                /*
                 * There is no error handling necessary in this case. If somebody sends us invalid JSON,
                 * we just ignore the packet and go ahead.
                 */
                LOGGER.log(Level.SEVERE, "Can't parse JSON!", e);
            } catch (MissingDataException e) {
                /*
                 * During the creation of an Announce object it is required that some
                 * sub-objects are created in the parsed JSON object (i.e. the device's UUID). If these
                 * sub-objects are not created, the construction of the Announce object fails.
                 *
                 * Go ahead with the next packet.
                 */
                LOGGER.log(Level.SEVERE, "Some information is missing in JSON!", e);
            }
        } else {
            setChanged();
            notifyObservers(announce);
        }
    }

    private static final class JsonRpcDeserializer implements JsonDeserializer<JsonRpc> {

        JsonRpcDeserializer() {
            // This constructor is only use by the outer class.
        }

        @Override
        public JsonRpc deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

            JsonRpc rpcObject = null;
            final JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("method")) {
                final String type = jsonObject.get("method").getAsString();
                if ("announce".compareTo(type) == 0) {
                    rpcObject = context.deserialize(json, Announce.class);
                    rpcObject.setJSONString(jsonObject.toString());
                }
            }
            return rpcObject;
        }
    }

    private static final class InterfaceDeserializer implements JsonDeserializer<Interface> {

        @Override
        public Interface deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();

            JsonElement name = jsonObject.get("name");
            if (!isStringElement(name)) {
                return null;
            }

            String interfaceName = name.getAsString();
            Interface iface = new Interface();
            iface.name = interfaceName;
            JsonElement description = jsonObject.get("description");
            if (description != null) {
                if (isStringElement(description)) {
                    iface.description = description.getAsString();
                } else {
                    return null;
                }
            }

            JsonElement type = jsonObject.get("type");
            if (type != null) {
                if (isStringElement(type)) {
                    iface.type = type.getAsString();
                } else {
                    return null;
                }
            }

            iface.ipList = new LinkedList<>();
            JsonElement ipv4 = jsonObject.get("ipv4");
            if (ipv4 != null) {
                for (JsonElement e : ipv4.getAsJsonArray()) {
                    JsonElement address = e.getAsJsonObject().get("address");
                    JsonElement netMask = e.getAsJsonObject().get("netmask");
                    if ((address != null) && (netMask != null)) {
                        InetAddress value = context.deserialize(address, InetAddress.class);
                        InetAddress mask = context.deserialize(netMask, InetAddress.class);
                        if ((value != null) && (mask != null)) {
                            IPEntry entry = new IPEntry();
                            entry.address = value;
                            entry.prefix = calculatePrefix(mask);
                            iface.ipList.add(entry);
                        }
                    }
                }
            }

            JsonElement ipv6 = jsonObject.get("ipv6");
            if (ipv6 != null) {
                for (JsonElement e : ipv6.getAsJsonArray()) {
                    JsonElement address = e.getAsJsonObject().get("address");
                    JsonElement prefix = e.getAsJsonObject().get("prefix");
                    if ((address != null) && (prefix != null)) {
                        InetAddress value = context.deserialize(address, InetAddress.class);
                        if (value != null) {
                            IPEntry entry = new IPEntry();
                            entry.address = value;
                            entry.prefix = prefix.getAsInt();
                            iface.ipList.add(entry);
                        }
                    }
                }
            }

            return iface;
        }
    }

    private static boolean isStringElement(JsonElement element) {
        return ((element != null) && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString());
    }

    private static final class ServiceDeserializer implements JsonDeserializer<List<ServiceEntry>> {

        @Override
        public List<ServiceEntry> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<ServiceEntry> vals = null;
            if (json.isJsonArray()) {
                vals = new ArrayList<>();
                for (JsonElement e : json.getAsJsonArray()) {
                    vals.add((ServiceEntry) context.deserialize(e, ServiceEntry.class));
                }
            }

            return vals;
        }
    }

    private static final class AnnounceParamsDeserializer implements JsonDeserializer<AnnounceParams> {

        private final Type serviceListType;

        AnnounceParamsDeserializer() {
            serviceListType = new TypeToken<List<ServiceEntry>>() {
            }.getType();
        }

        @Override
        public AnnounceParams deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            AnnounceParams params = null;
            final JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("apiVersion")) {
                final String version = jsonObject.get("apiVersion").getAsString();
                if ("1.0".compareTo(version) == 0) {
                    params = new AnnounceParams();
                    params.apiVersion = version;

                    Device device = context.deserialize(jsonObject.get("device"), Device.class);
                    params.device = device;
                    NetSettings netSettings = context.deserialize(jsonObject.get("netSettings"), NetSettings.class);
                    params.netSettings = netSettings;
                    Router router = context.deserialize(jsonObject.get("router"), Router.class);
                    params.router = router;
                    JsonElement e = jsonObject.get("services");
                    List<ServiceEntry> services = context.deserialize(e, serviceListType);
                    params.services = services;
                    e = jsonObject.get("expiration");
                    if (e != null) {
                        params.expiration = e.getAsInt();
                    }
                } else if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "Can't handle apiVersion: {0}\n{1}", new Object[]{version, jsonObject});
                }
            } else if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "No apiVersion set in announce packet!\n{0}", jsonObject);
            }
            return params;
        }
    }

    static int calculatePrefix(InetAddress announceNetmask) {
        final byte[] address = announceNetmask.getAddress();
        final int length = address.length;
        int prefix = 0;
        for (int i = 0; i < length; i++) {
            prefix += Integer.bitCount(address[i] & 0xff);
        }
        return prefix;
    }
}
