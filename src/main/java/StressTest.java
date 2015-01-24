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

import java.util.logging.Logger;

import com.hbm.devices.scan.DeviceMonitor;
import com.hbm.devices.scan.messages.MessageParser;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.StressTestMessageReceiver;

public final class StressTest {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private StressTest() {
    }

    public static void main(String... args) {
        LOGGER.info("Test without Cache:\n");
        final StressTestMessageReceiver mr = new StressTestMessageReceiver(53, 50000);
        final MessageParser mp = new MessageParser(false);
        mr.addObserver(mp);
        final DeviceMonitor dm = new DeviceMonitor();
        mp.addObserver(dm);

        mr.start();

        mr.stop();

        LOGGER.info("\nTest with Cache:\n");
        final StressTestMessageReceiver mr2 = new StressTestMessageReceiver(53, 50000);
        final MessageParser mp2 = new MessageParser(true);
        mr2.addObserver(mp2);
        final DeviceMonitor dm2 = new DeviceMonitor();
        mp2.addObserver(dm2);

        mr2.start();

        mr2.stop();
    }

}
