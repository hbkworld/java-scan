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
import com.google.gson.JsonSyntaxException;

import com.hbm.devices.scan.JsonRpc;
import com.hbm.devices.scan.ScanConstants;

/**
 * This class gets JSON announce messages, parses them and notifies {@link Announce}
 * objects.
 * <p>
 * The whole class is designed as a best effort service. So invalid JSON messages, or messages that
 * do not conform to the HBM network discovery and configuration protocol are simply ignored. Users
 * of this class will <em>not</em> get any error messages or exceptions.
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
        gson = builder.create();

        this.announceCache = new AnnounceCache();
    }

    AnnounceCache getCache() {
        return this.announceCache;
    }

    @Override
    public void update(Observable observable, Object arg) {
        final String message = (String)arg;
        Announce announce = announceCache.get(message);
        if (announce == null) {
            try {
                announce = (Announce)gson.fromJson(message, JsonRpc.class);
                if (announce != null) {
                    announce.identifyCommunicationPath();
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
                LOGGER.log(Level.SEVERE, announce.getJSONString());
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

    private static final class AnnounceParamsDeserializer implements JsonDeserializer<AnnounceParams> {

        private final Gson gson;

        AnnounceParamsDeserializer() {
            // This constructor is only use by the outer class.
            gson = new Gson();
        }
        @Override
        public AnnounceParams deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            AnnounceParams params = null;
            final JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("apiVersion")) {
                final String version = jsonObject.get("apiVersion").getAsString();
                if ("1.0".compareTo(version) == 0) {
                    params = gson.fromJson(json, AnnounceParams.class);
                } else {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.log(Level.INFO, "Can't handle apiVersion: " + version + '\n' + jsonObject);
                    }
                }
            } else {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE, "No apiVersion set in announce packet!\n" + jsonObject);
                }
            }
            return params;
        }
    }
}

