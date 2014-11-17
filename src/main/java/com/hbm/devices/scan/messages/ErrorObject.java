package com.hbm.devices.scan.messages;

public class ErrorObject {

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

    public static void checkForErrors(ErrorObject error) throws MissingDataException {
        if (error == null) {
            return;
        }

        if (error.message == null || error.message.length() == 0) {
            throw new MissingDataException("no message in responseError");
        }
    }

}
