package com.hbm.devices.configure;

import com.hbm.devices.scan.messages.Response;

/**
 * This is the callback interface to handle the responses received from a device.
 * 
 * @since 1.0
 *
 */
public interface ConfigCallback {

	/**
	 * This is the callback method which is called, when the query was received and processed
	 * successfully by the device.
	 * 
	 * @param configQuery
	 *            This parameter contains the query, which is sent to the device
	 * @param response
	 *            This parameter contains the response, which is received from the device
	 */
	public void onSuccess(ConfigQuery configQuery, Response response);

	/**
	 * This is the callback method which is called, when an error occurred processing the query by
	 * the device.
	 * 
	 * @param configQuery
	 *            This parameter contains the query, which is sent to the device
	 * @param response
	 *            This parameter contains the response, which is received from the device
	 */
	public void onError(ConfigQuery configQuery, Response response);

	/**
	 * This is the callback method which is called, if the device does not send a response within
	 * the timeout.
	 * 
	 * @param configQuery
	 *            This parameter contains the query, which is sent to the device
	 */
	public void onTimeout(ConfigQuery configQuery);
}
