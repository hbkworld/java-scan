package com.hbm.devices.configure;

import com.hbm.devices.scan.MissingDataException;

/**
 * The device class specifies which device should be configured.
 * 
 * @since 1.0
 *
 */
public class Device {

	private String uuid;

	public Device(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * 
	 * @return returns the unique ID of the device
	 */
	public String getUUID() {
		return this.uuid;
	}

	@Override
	public String toString() {
		return "Device:\n\t uuid: " + uuid + "\n";
	}

	/**
	 * This method checks the {@link Device} object for errors and if it conforms to the HBM network
	 * discovery and configuration protocol.
	 * 
	 * @param device
	 *            the {@link Device} object, which should be checked for errors
	 * @throws MissingDataException
	 * @throws NullPointerException
	 */
	public static void checkForErrors(Device device) throws MissingDataException,
			NullPointerException {
		if (device == null)
			throw new NullPointerException("device object must not be null");

		if (device.uuid == null) {
			throw new NullPointerException("No uuid in Device");
		} else if (device.uuid.length() == 0) {
			throw new MissingDataException("No uuid in Device");
		}
	}

}
