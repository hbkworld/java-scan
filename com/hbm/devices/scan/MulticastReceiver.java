package com.hbm.devices.scan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.JsonRpc;
import com.hbm.devices.scan.messages.Leave;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

class JsonRpcDeserializer implements JsonDeserializer<JsonRpc> {
    public JsonRpc deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        
        JsonObject jsonObject = json.getAsJsonObject();

        String type = jsonObject.get("method").getAsString();
        if (type.compareTo("announce") == 0) {
            return context.deserialize(json, Announce.class);
        } else if (type.compareTo("leave") == 0) {
            return context.deserialize(json, Leave.class);
        } else {
            return null;
        }
    }
}

public class MulticastReceiver extends Observable implements Runnable {

	public MulticastReceiver(Collection<NetworkInterface> ifs) throws IOException {
		interfaces = ifs;
		stop = false;
		InetAddress group = InetAddress.getByName(ScanConstants.SCAN_ADDRESS);
		InetSocketAddress mcastAddress = new InetSocketAddress(group, ScanConstants.SCAN_PORT);
		socket = new MulticastSocket(ScanConstants.SCAN_PORT);
		socket.setReuseAddress(true);

		Iterator<NetworkInterface> niIterator = ifs.iterator();
		while (niIterator.hasNext()) {
			NetworkInterface ni = niIterator.next();
			socket.joinGroup(mcastAddress, ni);
		}
	}

	public synchronized void run() {
		byte[] buffer = new byte[65536];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(JsonRpc.class, new JsonRpcDeserializer());
		Gson gson = builder.create();

		while(!stop) {
			try {
				socket.receive(packet);
				String s = new String(buffer, 0, packet.getLength());
				JsonRpc answer = gson.fromJson(s, JsonRpc.class);
				setChanged();
				notifyObservers(answer);
			} catch (JsonParseException e) {
				// Ignore every JSON syntax error and drop the packet
			} catch (IOException e) {
				stop = true;
			}
		}
		try {
			InetAddress group = InetAddress.getByName(ScanConstants.SCAN_ADDRESS);
			InetSocketAddress mcastAddress = new InetSocketAddress(group, ScanConstants.SCAN_PORT);
			Iterator<NetworkInterface> niIterator = interfaces.iterator();
			while (niIterator.hasNext()) {
				NetworkInterface ni = niIterator.next();
				socket.leaveGroup(mcastAddress, ni);
			}
			socket.close();
		} catch (IOException e) {
			// Because leaving the group is best effort, thats all we can do here.
		}
	}

	public void stop() {
		socket.close();
	}

	private boolean stop;
	private Collection<NetworkInterface> interfaces;
	private MulticastSocket socket;
}

