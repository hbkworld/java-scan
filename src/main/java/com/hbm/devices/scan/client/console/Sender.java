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

package com.hbm.devices.scan.client.console;

import java.net.NetworkInterface;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.configure.ConfigurationCallback;
import com.hbm.devices.scan.configure.ConfigurationSender;
import com.hbm.devices.scan.configure.ConfigurationService;
import com.hbm.devices.scan.messages.ConfigureInterface;
import com.hbm.devices.scan.configure.ResponseListener;
import com.hbm.devices.scan.configure.ConfigurationMulticastSender;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.ConfigureDevice;
import com.hbm.devices.scan.messages.ConfigureNetSettings;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Interface.Method;
import com.hbm.devices.scan.messages.Response;
import com.hbm.devices.scan.util.ScanInterfaces;

/**
 * Example class to show configuration of a device.
 * <p>
 *
 * @since 1.0
 */
public final class Sender implements ConfigurationCallback {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private final ConfigurationService service;

    private Sender() throws IOException {
        final ResponseListener listener = new ResponseListener();
        final Collection<NetworkInterface> scanInterfaces = new ScanInterfaces().getInterfaces();
        final ConfigurationMulticastSender sender = new ConfigurationMulticastSender(scanInterfaces);
        final ConfigurationSender parser = new ConfigurationSender(sender);
        service = new ConfigurationService(parser, listener);
    }

    @Override
    public void onSuccess(Response response) {
        LOGGER.log(Level.INFO, "Success:\n");
        LOGGER.log(Level.INFO, " result: " + response.getResult() + "\n");
    }

    @Override
    public void onError(Response response) {
        LOGGER.log(Level.INFO, "Error:\n");
        LOGGER.log(Level.INFO, " code: " + response.getError().getCode() + "\n");
        LOGGER.log(Level.INFO, " message: " + response.getError().getMessage() + "\n");
        LOGGER.log(Level.INFO, " data: " + response.getError().getData() + "\n");
    }

    @Override
    public void onTimeout(int timeout) {
        LOGGER.log(Level.INFO, "No response is received in " + timeout + "ms\n");
        service.shutdown();
    }

    /**
     * main method for an executable
     *
     * @param  args An array of command line paramters. Not used in the method.
     */
    public static void main(String... args) {
        try {
            final Sender sender = new Sender();
            final ConfigureDevice device = new ConfigureDevice("0009E5001571");
            final ConfigureNetSettings settings = new ConfigureNetSettings(new ConfigureInterface("eth0", Method.DHCP, null));
            final ConfigureParams configParams = new ConfigureParams(device, settings);
            sender.service.sendConfiguration(configParams, sender, 5000);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't create configuration service!", e);
        }
     }
}
