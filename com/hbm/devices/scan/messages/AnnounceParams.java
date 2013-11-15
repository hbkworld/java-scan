package com.hbm.devices.scan.messages;

import java.util.Iterator;
import java.util.LinkedList;

public class AnnounceParams {

	private AnnounceParams() {
	}

	public Device getDevice() {
		return device;
	}

	public NetSettings getNetSettings() {
		return netSettings;
	}

	public Router getRouter() {
		return router;
	}

	public Iterable<ServiceEntry> getServices() {
		return services;
	}

	private Device device;
 	private NetSettings netSettings;
 	private Router router;
	private LinkedList<ServiceEntry> services;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (device != null)
			sb.append(device);
		if (netSettings != null)
			sb.append(netSettings);
		if (router != null)
			sb.append(router);
		if (services != null) {
			sb.append("Services:");
			Iterator<ServiceEntry> i = services.iterator();
			while (i.hasNext()) {
				ServiceEntry se = i.next();
				sb.append("\n\t" + se);
			}
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
