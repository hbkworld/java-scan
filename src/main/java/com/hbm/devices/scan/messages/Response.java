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

/**
 * The Response contains the information, a device sent back, after it received a configuration
 * request
 * 
 * @author rene
 *
 */
public class Response extends JsonRpc {

    private String result;
    private ErrorObject error;
    private String id;

    protected Response() {
        super("response");
    }

    public String getResult() {
        return result;
    }

    public ErrorObject getError() {
        return error;
    }

    public String getId() {
        return id;
    }

    public static void checkForErrors(Response response) throws MissingDataException {
        if (response == null) {
            return;
        }

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
            ErrorObject.checkForErrors(response.error);
        }
    }

}
