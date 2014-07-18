package com.hbm.devices.scan.messages;

import java.util.LinkedList;

public class ConfigureParams {

	private ConfigureParams() {	    
	}
	
	public ConfigureParams(Device device, NetSettings netSettings) {
	    this();
	    this.device = device;
	    this.netSettings = netSettings;
	}	
	
	public ConfigureParams(Device device, NetSettings netSettings, int ttl) {
	    this(device, netSettings);
	    this.ttl = ttl;
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

	/**
	 * @return      An optional key which limits the number of router hops a configure 
	 *              request/response can cross. Leaving out this key should default to a ttl 
	 *              (Time to live) of 1 when sending datagrams, so no router boundary is crossed.
	 */
	public int getTtl() {
	    return ttl;
	}
	
	private Device device;
 	private NetSettings netSettings;
 	private Router router;
	private LinkedList<ServiceEntry> services;
	private int expiration;
	private int ttl;

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
