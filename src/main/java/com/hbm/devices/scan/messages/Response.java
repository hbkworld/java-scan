/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Stephan Gatzka
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
