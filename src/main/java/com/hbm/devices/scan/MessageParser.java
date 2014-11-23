/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
 */

package com.hbm.devices.scan;

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
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.JsonRpc;
import com.hbm.devices.scan.messages.MissingDataException;
import com.hbm.devices.scan.messages.Response;

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
    private boolean useCache;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    public MessageParser() {
        this(true);
    }

    public MessageParser(boolean useCache) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(JsonRpc.class, new JsonRpcDeserializer());
        gson = builder.create();

        this.useCache = useCache;
        if (useCache) {
            this.announceCache = new AnnounceCache();
        }
    }

    public AnnounceCache getCache() {
        return this.announceCache;
    }

    @Override
    public void update(Observable o, Object arg) {
        String s = (String) arg;
        try {
            JsonRpc json;
            boolean newParsedString;
            if (useCache && announceCache.hasStringInCache(s)) {
                newParsedString = false;
                json = announceCache.getAnnounceByString(s);
            } else {
                newParsedString = true;
                json = gson.fromJson(s, JsonRpc.class);
            }
            if (json instanceof Announce) {
                CommunicationPath ap = new CommunicationPath((Announce) json);
                // add the parsed AnnounceObject to the cache, if its a new, not yet cached String
                // Only cache Announce objects!
                if (useCache && newParsedString) {
                    announceCache.addCommunicationPath(s, ap);
                }
                setChanged();
                notifyObservers(ap);
            } else if (json instanceof Response) {
                Response response = (Response) json;
                Response.checkForErrors(response);

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
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("method")) {
            String type = jsonObject.get("method").getAsString();
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
