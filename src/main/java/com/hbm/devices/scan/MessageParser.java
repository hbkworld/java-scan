package com.hbm.devices.scan;

import java.lang.reflect.Type;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.JsonRpc;
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
		} catch (MissingDataException e) {
			/*
			 * During the creation of an CommunicationPath object it is required that some
			 * sub-objects are created in the parsed JSON object (i.e. the device's UUID). If these
			 * sub-objects are not created, the construction of the CommunicationPath object fails.
			 * 
			 * Go ahead with the next packet.
			 */
		}
	}
}

class JsonRpcDeserializer implements JsonDeserializer<JsonRpc> {
	public JsonRpc deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonRpc rpcObject = null;
		JsonObject jsonObject = json.getAsJsonObject();

		if (jsonObject.has("method")) {
			String type = jsonObject.get("method").getAsString();
			if (type.compareTo("announce") == 0) {
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
