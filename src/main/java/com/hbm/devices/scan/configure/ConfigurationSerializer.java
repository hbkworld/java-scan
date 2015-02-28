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

package com.hbm.devices.scan.configure;

import java.io.Closeable;
import java.io.IOException;

import com.google.gson.Gson;
import com.hbm.devices.scan.messages.ConfigurationRequest;

/**
 * This class is able to send {@link ConfigurationRequest} messages via multicast.
 * 
 * @since 1.0
 *
 */
public final class ConfigurationSerializer implements Closeable {

    private final MulticastSender sender;
    private final Gson gson;

    /**
     * Constructs a ConfigurationSerializer object.
     *
     * @param sender The multicast sender that will be used when calling
     * {@link #sendConfiguration}.
     */
    public ConfigurationSerializer(MulticastSender sender) {
        this.sender = sender;
        gson = new Gson();
    }

    /**
     * Sends a network configuration via multicast.
     *
     * @param configuration The network configuration which shall be
     * send.
     *
     * @throws IOException if sending of the configuration fails.
     * @throws IllegalArgumentException if configuration == null
     */
    public void sendConfiguration(ConfigurationRequest configuration) throws IOException {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration == null");
        }
        final String message = getJsonString(configuration);
        sender.sendMessage(message);
    }

    /**
     * Shuts down the underlying multicast sender.
     *
     * This involves multicast leave IGMP messagesfor the configuration
     * address.
     */
    @Override
    public void close() {
        sender.close();
    }

    public boolean isClosed() {
        return sender.isClosed();
    }

    private String getJsonString(ConfigurationRequest configuration) {
        return gson.toJson(configuration);
    }
}
