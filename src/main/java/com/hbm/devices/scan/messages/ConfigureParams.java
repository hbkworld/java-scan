package com.hbm.devices.scan.messages;

import com.hbm.devices.scan.MissingDataException;

public class ConfigureParams {

	private ConfigureParams() {
	}

	public ConfigureParams(com.hbm.devices.configure.Device device,
			com.hbm.devices.configure.NetSettings netSettings) {
		this();
		this.device = device;
		this.netSettings = netSettings;
		this.ttl = 1;
	}

	public ConfigureParams(com.hbm.devices.configure.Device device,
			com.hbm.devices.configure.NetSettings netSettings, int ttl) {
		this(device, netSettings);
		this.ttl = ttl;
	}

	public com.hbm.devices.configure.Device getDevice() {
		return device;
	}

	public com.hbm.devices.configure.NetSettings getNetSettings() {
		return netSettings;
	}

	/**
	 * @return An optional key which limits the number of router hops a configure request/response
	 *         can cross. Leaving out this key should default to a ttl (Time to live) of 1 when
	 *         sending datagrams, so no router boundary is crossed.
	 */
	public int getTtl() {
		return ttl;
	}

	private com.hbm.devices.configure.Device device;
	private com.hbm.devices.configure.NetSettings netSettings;
	private int ttl;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (device != null)
			sb.append(device);
		if (netSettings != null)
			sb.append(netSettings);
		sb.append("ttl: " + ttl + "\n");

		sb.append("\n");

		return sb.toString();
	}

	public static void checkForErrors(ConfigureParams params) throws MissingDataException,
			NullPointerException {
		if (params == null)
			throw new NullPointerException("params object must not be null");

		if (params.ttl < 1) {
			throw new MissingDataException(
					"time-to-live must be greater or equals 1 in ConfigureParams");
		}

		if (params.device == null) {
			throw new NullPointerException("No device in ConfigureParams");
		}
		com.hbm.devices.configure.Device.checkForErrors(params.device);

		if (params.netSettings == null) {
			throw new NullPointerException("No net settings in ConfigureParams");
		}
		com.hbm.devices.configure.NetSettings.checkForErrors(params.netSettings);
	}
}
