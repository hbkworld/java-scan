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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;

import com.google.common.base.Predicate;

import com.hbm.devices.scan.configure.ConfigurationMulticastSender;
import com.hbm.devices.scan.ScanInterfaces;

public class ConfigurationMulticastSenderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createWithNoInterfaces() {
        try {
            exception.expect(IllegalArgumentException.class);
            ConfigurationMulticastSender sender = new ConfigurationMulticastSender(null);
            assertNotNull("Could not instantiate ConfigurationMulticastSender", sender);
        } catch (IOException e) {
            fail("Can't instantiate ConfigurationMulticastSender object");
        }
    }

    @Test
    public void createWithFilter() {
        try {
            Predicate<NetworkInterface> predicate = new Predicate<NetworkInterface>() {
                @Override
                public boolean apply(NetworkInterface networkInterface) {
                    try {
                        return networkInterface.getHardwareAddress() != null;
                    } catch (SocketException e) {
                        return false;
                    }
                }
            };
            final Collection<NetworkInterface> sendInterfaces = new ScanInterfaces(predicate).getInterfaces();
            for (NetworkInterface sendInterface : sendInterfaces) {
                assertTrue("Predicate wasn't applied as expected", predicate.apply(sendInterface));
            }
        } catch (IOException e) {
            fail("Can't instantiate ConfigurationMulticastSender object");
        }
    }

    @Test
    public void createAndClose() {
        try {
            final Collection<NetworkInterface> sendInterfaces = new ScanInterfaces().getInterfaces();
            ConfigurationMulticastSender sender = new ConfigurationMulticastSender(sendInterfaces);
            sender.close();
            assertTrue("ConfigurationMulticastSender was not closed", sender.isClosed());
        } catch (IOException e) {
            fail("Can't instantiate ConfigurationMulticastSender object");
        }
    }

    @Test
    public void createAndDoubleClose() {
        try {
            final Collection<NetworkInterface> sendInterfaces = new ScanInterfaces().getInterfaces();
            ConfigurationMulticastSender sender = new ConfigurationMulticastSender(sendInterfaces);
            sender.close();
            assertTrue("ConfigurationMulticastSender was not closed", sender.isClosed());
            sender.close();
            assertTrue("Sedonc close failed", sender.isClosed());
        } catch (IOException e) {
            fail("Can't instantiate ConfigurationMulticastSender object");
        }
    }
    @Test
    public void sendMessageTest() {
        try {
            final Collection<NetworkInterface> sendInterfaces = new ScanInterfaces().getInterfaces();
            ConfigurationMulticastSender sender = new ConfigurationMulticastSender(sendInterfaces);
            sender.sendMessage("hello world");
            sender.close();
            assertTrue("ConfigurationMulticastSender was not closed", sender.isClosed());
        } catch (IOException e) {
            fail("Can't instantiate ConfigurationMulticastSender object or send message");
        }
    }
}
