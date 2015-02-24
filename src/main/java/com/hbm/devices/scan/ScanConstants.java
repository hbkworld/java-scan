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

package com.hbm.devices.scan;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Convenience class to receive response multicast messages.
 * <p>
 *
 * @since 1.0
 */
public final class ScanConstants {

    public static final String ANNOUNCE_ADDRESS;
    public static final int ANNOUNCE_PORT;

    public static final String CONFIGURATION_ADDRESS;
    public static final int CONFIGURATION_PORT;

    public static final int DEFAULT_EXPIRATION_S = 6;

    public static final String LOGGER_NAME = "scan";

    private ScanConstants() {
    }

    static {
        try (final InputStream inputStream = ScanConstants.class.getResourceAsStream("/scan.properties")) {
            final Properties props = new Properties();
            props.load(inputStream);

            ANNOUNCE_ADDRESS = props.getProperty("scan.announce.address");
            ANNOUNCE_PORT = Integer.parseInt(props.getProperty("scan.announce.port"));
            CONFIGURATION_ADDRESS = props.getProperty("scan.configure.address");
            CONFIGURATION_PORT = Integer.parseInt(props.getProperty("scan.configure.port"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
