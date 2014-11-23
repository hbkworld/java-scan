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

import com.google.gson.annotations.SerializedName;

public abstract class JsonRpc {
    
    @SerializedName("jsonrpc")
    private String jsonrpcVersion;
    private String method;
    private String json;
    
    protected JsonRpc(String method) {
        jsonrpcVersion = "2.0";
        this.method = method;
    }
    
    public void setJSONString(String json) {
        this.json = json;
    }

    public String getJSONString() {
        return json;
    }

    public String getJsonrpc() {
        return jsonrpcVersion;
    }

    public String getMethod() {
        return method;
    }
}
