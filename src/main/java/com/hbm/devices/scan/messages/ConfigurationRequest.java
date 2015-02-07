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

import com.google.gson.annotations.SerializedName;

/**
 * This class hold a complete configuration request packet according to the specification.
 * 
 * @since 1.0
 */
public final class ConfigurationRequest extends JsonRpc {

    @SerializedName("id")
    private String queryID;
    private ConfigureParams params;

    private ConfigurationRequest() {
        super("configure");
    }

    /**
     * @param params
     *            the configuration parameters, which should be sent to a device
     * @param queryID
     *            A value of any type, which is used to match the response with the request that it
     *            is replying to.
     */
    public ConfigurationRequest(ConfigureParams params, String queryID) {
        this();
        if (params == null) {
            throw new IllegalArgumentException("params == null");
        }
        if ((queryID == null) || (queryID.length() == 0)) {
            throw new IllegalArgumentException("No queryID given");
        }
        this.params = params;
        this.queryID = queryID;
    }

    public ConfigureParams getParams() {
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConfigurationRequest)) {
            return false;
        }
        final ConfigurationRequest rhs = (ConfigurationRequest)obj;
        return this.getJSONString().equals(rhs.getJSONString());
    }

    @Override
    public int hashCode() {
        return getJSONString().hashCode();
    }

    public String getQueryId() {
        return queryID;
    }
}
