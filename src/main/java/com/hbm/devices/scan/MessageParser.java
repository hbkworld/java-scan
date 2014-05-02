package com.hbm.devices.scan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.messages.*;
import com.hbm.devices.scan.MissingDataException;

import java.lang.reflect.Type;
import java.util.Observable;
import java.util.Observer;
/**
 * This class gets JSON announce messages, parses them and notifies
 * {@link AnnouncePath} objects.
 *
 * The whole class is designed as a best effort service. So invalid JSON
 * messages, or messages that do not conform to the HBM network
 * discovery and configuration protocol are simply ignored. Users of
 * this class will <em>not</em> get any error messages or exceptions.
 *
 * @since 1.0
 */
public class MessageParser extends Observable implements Observer {

	private Gson gson;

	public MessageParser() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(JsonRpc.class, new JsonRpcDeserializer());
		gson = builder.create();
	}

	@Override
	public void update(Observable o, Object arg) {
		String s = (String)arg;
		try {
			JsonRpc json = gson.fromJson(s, JsonRpc.class);
			if (json instanceof Announce) {
				AnnouncePath ap = new AnnouncePath((Announce)json);
				setChanged();
				notifyObservers(ap);
			}
		} catch (JsonSyntaxException e) {
			/* There is no error handling necessary in this case. If
			 * somebody sends us invalid JSON, we just ignore the packet
			 * and go ahead.
			 */
		} catch (MissingDataException e) {
			/* During the creation of an AnnouncePath object it is
			 * required that some sub-objects are created in the parsed
			 * JSON object (i.e. the device's UUID). If these
			 * sub-objects are not created, the construction of the
			 * AnnouncePath object failes.
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

        String type = jsonObject.get("method").getAsString();
        if (type.compareTo("announce") == 0) {
            rpcObject = context.deserialize(json, Announce.class);
			rpcObject.setJSONString(jsonObject.toString());
        } 
		return rpcObject;
    }
}
