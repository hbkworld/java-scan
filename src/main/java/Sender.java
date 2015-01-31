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

import java.net.NetworkInterface;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.configure.ConfigCallback;
import com.hbm.devices.scan.configure.ConfigurationSender;
import com.hbm.devices.scan.configure.ConfigurationService;
import com.hbm.devices.scan.messages.ConfigureInterface;
import com.hbm.devices.scan.configure.ResponseListener;
import com.hbm.devices.scan.MulticastSender;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.ConfigureDevice;
import com.hbm.devices.scan.messages.ConfigureNetSettings;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Interface.Method;
import com.hbm.devices.scan.messages.Response;
import com.hbm.devices.scan.util.ScanInterfaces;


public final class Sender implements ConfigCallback {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private final ConfigurationService service;

    private Sender() throws IOException {
        final ResponseListener listener = new ResponseListener();
        final Collection<NetworkInterface> scanInterfaces = new ScanInterfaces().getInterfaces();
        final MulticastSender sender = new MulticastSender(scanInterfaces);
        final ConfigurationSender parser = new ConfigurationSender(sender);
        service = new ConfigurationService(parser, listener);
    }

    @Override
    public void onSuccess(Response response) {
        LOGGER.info("Success:\n");
        LOGGER.info(" result: " + response.getResult() + "\n");
    }

    @Override
    public void onError(Response response) {
        LOGGER.info("Error:\n");
        LOGGER.info(" code: " + response.getError().getCode() + "\n");
        LOGGER.info(" message: " + response.getError().getMessage() + "\n");
        LOGGER.info(" data: " + response.getError().getData() + "\n");
    }

    @Override
    public void onTimeout(int timeout) {
        LOGGER.info("No response is received in " + timeout + "ms\n");
        service.shutdown();
    }

    public static void main(String... args) {

        try {
            final Sender s = new Sender();
            final ConfigureDevice device = new ConfigureDevice("0009E5001571");
            final ConfigureNetSettings settings = new ConfigureNetSettings(new ConfigureInterface("eth0", Method.DHCP, null));
            final ConfigureParams configParams = new ConfigureParams(device, settings);
            s.service.sendConfiguration(configParams, s, 5000);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't create configuration service!", e);
        }
     }
}
