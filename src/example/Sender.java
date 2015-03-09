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

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.ScanInterfaces;
import com.hbm.devices.scan.configure.ConfigurationCallback;
import com.hbm.devices.scan.configure.ConfigurationDevice;
import com.hbm.devices.scan.configure.ConfigurationInterface.Method;
import com.hbm.devices.scan.configure.ConfigurationInterface;
import com.hbm.devices.scan.configure.ConfigurationMessageReceiver;
import com.hbm.devices.scan.configure.ConfigurationMulticastSender;
import com.hbm.devices.scan.configure.ConfigurationNetSettings;
import com.hbm.devices.scan.configure.ConfigurationParams;
import com.hbm.devices.scan.configure.ConfigurationSerializer;
import com.hbm.devices.scan.configure.ConfigurationService;
import com.hbm.devices.scan.configure.Response;
import com.hbm.devices.scan.configure.ResponseDeserializer;

/**
 * Example class to show configuration of a device.
 * <p>
 *
 * @since 1.0
 */
public final class Sender implements ConfigurationCallback {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);
    private final ConfigurationService service;
    private static final int RESPONSE_TIMEOUT_S = 5;
    private final ConfigurationMessageReceiver responseReceiver;

    private Sender() throws IOException {
        responseReceiver = new ConfigurationMessageReceiver();
        final ResponseDeserializer responseParser = new ResponseDeserializer();
        responseReceiver.addObserver(responseParser);

        final Collection<NetworkInterface> scanInterfaces = new ScanInterfaces().getInterfaces();
        final ConfigurationMulticastSender multicastSender = new ConfigurationMulticastSender(scanInterfaces);
        final ConfigurationSerializer sender = new ConfigurationSerializer(multicastSender);

        service = new ConfigurationService(sender, responseParser);

        final Thread receiverThread = new Thread(responseReceiver);
        receiverThread.start();
    }

    @Override
    public void onSuccess(Response response) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Success:\n");
            LOGGER.log(Level.INFO, " result: " + response.getResult() + "\n");
        }
        responseReceiver.close();
        service.close();
    }

    @Override
    public void onError(Response response) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "Error:\n");
            LOGGER.log(Level.INFO, " code: " + response.getError().getCode() + "\n");
            LOGGER.log(Level.INFO, " message: " + response.getError().getMessage() + "\n");
            LOGGER.log(Level.INFO, " data: " + response.getError().getData() + "\n");
        }
        responseReceiver.close();
        service.close();
    }

    @Override
    public void onTimeout(long timeout) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "No response is received in " + timeout + "ms\n");
        }
        responseReceiver.close();
        service.close();
    }

    /**
     * main method for an executable
     *
     * @param  args An array of command line paramters. Not used in the method.
     */
    public static void main(String... args) {
        try {
            final Sender sender = new Sender();
            final ConfigurationDevice device = new ConfigurationDevice("0009E5001B6C");
            final ConfigurationNetSettings settings = new ConfigurationNetSettings(new ConfigurationInterface("eth0", Method.DHCP));
            final ConfigurationParams configParams = new ConfigurationParams(device, settings);
            sender.service.sendConfiguration(configParams, sender, TimeUnit.SECONDS.toMillis(RESPONSE_TIMEOUT_S));
        } catch (IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE, "Can't create configuration service!", e);
            }
        }
     }
}
