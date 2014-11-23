/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
 */

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
