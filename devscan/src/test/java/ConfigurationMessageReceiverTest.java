/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Hottinger Baldwin Messtechnik GmbH
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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.io.IOException;

import com.hbm.devices.scan.configure.ConfigurationMessageReceiver;

public class ConfigurationMessageReceiverTest {

    @Test
    public void intantiation() {
        try (final ConfigurationMessageReceiver cr = new ConfigurationMessageReceiver()) {
            assertNotNull("Could not instantiate ConfigurationMessageReceiver", cr);
        } catch (IOException e) {
            fail("Got IOException while instantiating ConfigurationMessageReceiver");
        }
    }

    @Test
    public void runAndStop() {
        try {
            ConfigurationMessageReceiver cr = new ConfigurationMessageReceiver();
            assertNotNull("Could not instantiate ConfigurationMessageReceiver", cr);
            Thread crThread = new Thread(cr);
            crThread.start();
            Thread.sleep(10);
            cr.close();
            crThread.join(1000);
            assertFalse("Thread still alive after close", crThread.isAlive());
        } catch (IOException e) {
            fail("Got IOException while instantiating ConfigurationMessageReceiver");
        } catch (InterruptedException e) {
            fail("Got InterruptedExcpetion while joining ConfigurationMessageReceiver thread");
        }
    }
}
