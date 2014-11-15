package com.hbm.devices.scan.messages;

import java.util.LinkedList;

/**
 * The AnnounceParams describe the properties of the device, which contain information of the device
 * itself (like uuid, name, type, etc), the NetSettings, router information the device is connected
 * to and running services
 * 
 * @since 1.0
 */
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

	public int getExpiration() {
		return expiration;
	}

	private Device device;
	private NetSettings netSettings;
	private Router router;
	private LinkedList<ServiceEntry> services;
	private int expiration;

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
			for (ServiceEntry se : services) {
				sb.append("\n\t" + se);
			}
		}
		sb.append("\nexpiration: " + expiration + "\n");
		sb.append("\n");

		return sb.toString();
	}
}
