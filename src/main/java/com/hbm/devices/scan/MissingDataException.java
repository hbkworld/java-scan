package com.hbm.devices.scan;

/**
 * This exception is thrown if an announce message doesn't carry the
 * information required by the specification.
 * <p>
 */
public class MissingDataException extends Exception {
	public MissingDataException(String message) {
		super(message);
	}
}
