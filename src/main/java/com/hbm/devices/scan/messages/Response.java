package com.hbm.devices.scan.messages;

import com.hbm.devices.scan.MissingDataException;

/**
 * The Response contains the information, a device sent back, after it received a configuration
 * request
 * 
 * @author rene
 *
 */
public class Response extends JsonRpc {

	protected Response() {
		super("response"); // but the method field will not be received from responding device
	}

	private String result; // Device specific
	private ResponseError error;
	private String id;

	public String getResult() {
		return result;
	}

	public ResponseError getError() {
		return error;
	}

	public String getId() {
		return id;
	}

	public static void checkForErrors(Response response) throws MissingDataException {
		if (response == null)
			return;

		if (response.id == null || response.id.length() <= 0) {
			throw new MissingDataException("No response id in response object");
		}

		if (response.result == null && response.error == null) {
			throw new MissingDataException("Neither result nor error in response object");
		} else if (response.result != null && response.error != null) {
			throw new MissingDataException(
					"Either result OR error must be specified, but never both");
		}

		if (response.error != null) {
			ResponseError.checkForErrors(response.error);
		}
	}

}
