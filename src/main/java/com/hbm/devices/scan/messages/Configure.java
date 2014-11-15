package com.hbm.devices.scan.messages;

import com.hbm.devices.scan.MissingDataException;

/**
 * Network configuration request datagrams are dedicated requests, so only the device addressed by
 * request/configure/device/uuid must answer with a network configuration response datagram.
 * 
 * @since 1.0
 */
public class Configure extends JsonRpc {

	private Configure() {
		super("configure");
	}

	/**
	 * @param params
	 *            the configuration parameters, which should be sent to a device
	 * @param queryId
	 *            A value of any type, which is used to match the response with the request that it
	 *            is replying to.
	 */
	public Configure(ConfigureParams params, String queryId) {
		this();
		this.params = params;
		this.id = queryId;
	}

	public ConfigureParams getParams() {
		return params;
	}

	private ConfigureParams params;

	@Override
	public String toString() {
		return params.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Configure)) {
			return false;
		}
		Configure rhs = (Configure) o;
		return this.getJSONString().equals(rhs.getJSONString());
	}

	public String getQueryId() {
		return id;
	}

	/**
	 * This method checks a {@link Configure} object for errors; especially it checks if the
	 * {@link Configure} object conforms the specification.
	 * 
	 * @param config
	 * @throws MissingDataException
	 * @throws NullPointerException
	 */
	public static void checkForErrors(Configure config) throws MissingDataException,
			NullPointerException {
		if (config == null)
			throw new NullPointerException("config object must not be null");

		if (config.id == null) {
			throw new NullPointerException("no queryId in configure");
		} else if (config.id.length() == 0) {
			throw new MissingDataException("no queryId in configure");
		}

		if (config.params == null) {
			throw new NullPointerException("no params in configure");
		}

		ConfigureParams.checkForErrors(config.params);
	}

	private String id;
}
