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

package com.hbm.devices.scan.messages;

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

import com.hbm.devices.scan.ScanConstants;

/**
 * This class gets JSON announce messages, parses them and notifies {@link CommunicationPath}
 * objects.
 * <p>
 * The whole class is designed as a best effort service. So invalid JSON messages, or messages that
 * do not conform to the HBM network discovery and configuration protocol are simply ignored. Users
 * of this class will <em>not</em> get any error messages or exceptions.
 *
 * @since 1.0
 */
public class MessageParser extends Observable implements Observer {

    private Gson gson;
    private AnnounceCache announceCache;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    public MessageParser() {
        super();

        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(JsonRpc.class, new JsonRpcDeserializer());
        gson = builder.create();

        this.announceCache = new AnnounceCache();
    }

    AnnounceCache getCache() {
        return this.announceCache;
    }

    @Override
    public void update(Observable o, Object arg) {
        final String s = (String) arg;
        try {
            JsonRpc json;
            boolean newParsedString;
            if (announceCache.hasStringInCache(s)) {
                newParsedString = false;
                json = announceCache.getAnnounceByString(s);
            } else {
                newParsedString = true;
                json = gson.fromJson(s, JsonRpc.class);
            }
            if (json instanceof Announce) {
                final CommunicationPath ap = new CommunicationPath((Announce) json);
                // add the parsed AnnounceObject to the cache, if its a new, not yet cached String
                // Only cache Announce objects!
                if (newParsedString) {
                    announceCache.addCommunicationPath(s, ap);
                }
                setChanged();
                notifyObservers(ap);
            } else if (json instanceof Response) {
                final Response response = (Response)json;
                setChanged();
                notifyObservers(response);
            }
        } catch (JsonSyntaxException e) {
            /*
             * There is no error handling necessary in this case. If somebody sends us invalid JSON,
             * we just ignore the packet and go ahead.
             */
            LOGGER.log(Level.INFO, "Can't parse JSON!", e);
        } catch (MissingDataException e) {
            /*
             * During the creation of an CommunicationPath object it is required that some
             * sub-objects are created in the parsed JSON object (i.e. the device's UUID). If these
             * sub-objects are not created, the construction of the CommunicationPath object fails.
             * 
             * Go ahead with the next packet.
             */
            LOGGER.log(Level.INFO, "Some information is missing in JSON!", e);
        }
    }
}

class JsonRpcDeserializer implements JsonDeserializer<JsonRpc> {

    @Override
    public JsonRpc deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        JsonRpc rpcObject = null;
        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("method")) {
            final String type = jsonObject.get("method").getAsString();
            if ("announce".compareTo(type) == 0) {
                rpcObject = context.deserialize(json, Announce.class);
                if (rpcObject != null) {
                    rpcObject.setJSONString(jsonObject.toString());
                }
            }
        } else if (jsonObject.has("result") || jsonObject.has("error")) {
            // is a response object
            rpcObject = context.deserialize(json, Response.class);
            if (rpcObject != null) {
                rpcObject.setJSONString(jsonObject.toString());
            }
        }
        return rpcObject;
    }
}
