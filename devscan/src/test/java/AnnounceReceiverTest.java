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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.NetworkInterface;

import com.google.common.base.Predicates;
import com.hbm.devices.scan.announce.AnnounceReceiver;

public class AnnounceReceiverTest {

    @Test
    public void instantiation() {
        try (final AnnounceReceiver ar = new AnnounceReceiver()) {
            assertNotNull(ar, "Could not instantiate AnnounceReceiver");
        } catch (IOException e) {
            fail("Got IOException while instantiating AnnounceReceiver");
        }
        try (final AnnounceReceiver ar = new AnnounceReceiver(Predicates.<NetworkInterface>alwaysFalse())) {
            assertNotNull(ar, "Could not instantiate AnnounceReceiver");
        } catch (IOException e) {
            fail("Got IOException while instantiating AnnounceReceiver");
        }
    }

    @Test
    public void runAndStop() {
        try {
            AnnounceReceiver ar = new AnnounceReceiver();
            assertNotNull(ar, "Could not instantiate AnnounceReceiver");
            Thread arThread = new Thread(ar);
            arThread.start();
            Thread.sleep(10);
            ar.close();
            arThread.join(1000);
            assertFalse(arThread.isAlive(), "Thread still alive after stop");
        } catch (IOException e) {
            fail("Got IOException while instantiating AnnounceReceiver");
        } catch (InterruptedException e) {
            fail("Got InterruptedExcpetion while joining AnnounceReceiver thread");
        }
    }
}
