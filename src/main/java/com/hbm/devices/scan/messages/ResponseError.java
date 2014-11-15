package com.hbm.devices.scan.messages;

import com.hbm.devices.scan.MissingDataException;

public class ResponseError {

	private int code;
	private String message;
	private String data; // optional

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getData() {
		return data;
	}

	public static void checkForErrors(ResponseError error) throws MissingDataException {
		if (error == null) {
			return;
		}

		if (error.message == null || error.message.length() == 0) {
			throw new MissingDataException("no message in responseError");
		}
	}

}
