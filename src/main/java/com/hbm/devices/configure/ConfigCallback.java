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
     * @param response
     *            This parameter contains the response, which is received from the device
     */
    void onSuccess(Response response);

    /**
     * This is the callback method which is called, when an error occurred processing the query by
     * the device.
     * 
     * @param response
     *            This parameter contains the response, which is received from the device
     */
    void onError(Response response);

    /**
     * This is the callback method which is called, if the device does not send a response within
     * the timeout.
     * 
     * @param timeout
     *            The timeout in seconds that has been expired.
     */
    void onTimeout(int timeout);
}
