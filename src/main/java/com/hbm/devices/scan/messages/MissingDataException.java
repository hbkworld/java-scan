package com.hbm.devices.scan.messages;

/**
 * This exception is thrown if an announce message doesn't carry the information required by the
 * specification.
 * 
 * @since 1.0
 */

public class MissingDataException extends Exception {

    private static final long serialVersionUID = -575018775746931024L;

    public MissingDataException(String message) {
        super(message);
    }

    public MissingDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
