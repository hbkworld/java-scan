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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.configure.ConfigurationService;
import com.hbm.devices.configure.Device;
import com.hbm.devices.configure.Interface;
import com.hbm.devices.configure.NetSettings;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Interface.Method;

public final class Sender {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private Sender() {
    }

    public static void main(String[] args) {
        try {

            final ConfigurationService service = new ConfigurationService();
            final Device device = new Device("0009E5001571");
            final NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));
            final ConfigureParams configParams = new ConfigureParams(device, settings);

            try {
                service.sendConfiguration(configParams, new SimpleCallback(), 5000);
                service.shutdown();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Can't send device configuration!", e);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't create configuration service!", e);
        }
    }
}
