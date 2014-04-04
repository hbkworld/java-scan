package com.hbm.devices.scan.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.messages.*;
import java.lang.reflect.Type;
import java.util.Observable;
import java.util.Observer;

public class JsonFilter extends Observable implements Observer {

	private Gson gson;

	public JsonFilter() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(JsonRpc.class, new JsonRpcDeserializer());
		gson = builder.create();
	}

	@Override
	public void update(Observable o, Object arg) {
		String s = (String)arg;
		JsonRpc json = gson.fromJson(s, JsonRpc.class);
		if (json instanceof Announce) {
			AnnouncePath ap = new AnnouncePath((Announce)json);
			setChanged();
			notifyObservers(ap);
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
