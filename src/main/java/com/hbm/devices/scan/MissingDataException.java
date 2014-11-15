package com.hbm.devices.scan;

/**
 * This exception is thrown if an announce message doesn't carry the information required by the
 * specification.
 * 
 * @since 1.0
 */

public class MissingDataException extends Exception {
    public MissingDataException(String message) {
        super(message);
    }

    private static final long serialVersionUID = -575018775746931024L;
}
