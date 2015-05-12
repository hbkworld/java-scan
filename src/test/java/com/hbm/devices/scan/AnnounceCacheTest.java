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

package com.hbm.devices.scan.announce;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.FakeMessageReceiver;

public class AnnounceCacheTest {

    private static final String CORRECT_MESSAGE;
    private static final String CORRECT_MESSAGE_DIFFERENT_SERVICES;
    private static final String CORRECT_MESSAGE_DIFFERENT_DEVICE;

    private FakeMessageReceiver fakeReceiver;
    private AnnounceDeserializer parser;

    @Before
    public void setUp() {
        this.fakeReceiver = new FakeMessageReceiver();
        this.parser = new AnnounceDeserializer();
        fakeReceiver.addObserver(parser);
    }

    @Test
    public void addDeviceToCacheTest() {
        fakeReceiver.emitSingleCorrectMessage();
        assertSame("Entries in cache != 1", parser.getCache().size(), 1);
        assertSame("Paths in cache != 1", parser.getCache().lastAnnounceSize(), 1);
        assertNotNull("JSON message not in cache", parser.getCache().get(CORRECT_MESSAGE));

        fakeReceiver.emitSingleCorrectMessageDifferentDevice();
        assertSame("Entries in cache != 2", parser.getCache().size(), 2);
        assertSame("Paths in cache != 2", parser.getCache().lastAnnounceSize(), 2);
        assertNotNull("JSON message not in cache", parser.getCache().get(CORRECT_MESSAGE));
        assertNotNull("JSON message not in cache", parser.getCache().get(CORRECT_MESSAGE_DIFFERENT_DEVICE));
    }

    @Test
    public void getFromCacheTest() {
        fakeReceiver.emitSingleCorrectMessage();
        assertNotNull("Correct message not in cache", parser.getCache().get(CORRECT_MESSAGE));
    }

    @Test
    public void dontAddTwiceTest() {
        fakeReceiver.emitSingleCorrectMessage();
        fakeReceiver.emitSingleCorrectMessage();
        assertSame("Message was added more than once", parser.getCache().size(), 1);
        assertSame("Message was added more than once", parser.getCache().lastAnnounceSize(), 1);
    }

    @Test
    public void updateDeviceEntry() {
        fakeReceiver.emitSingleCorrectMessage();
        fakeReceiver.emitSingleCorrectMessageDifferentServices();

        assertSame("Correct message not in cache", parser.getCache().size(), 1);
        assertSame("Correct message not in cache", parser.getCache().lastAnnounceSize(), 1);
        assertNull("Original message still in cache", parser.getCache().get(CORRECT_MESSAGE));
        assertNotNull("New message not in cache", parser.getCache().get(CORRECT_MESSAGE_DIFFERENT_SERVICES));
    }

    static {
        try (final InputStream is = AnnounceCacheTest.class.getResourceAsStream("/fakemessages.properties")) {
            final Properties props = new Properties();
            props.load(is);

            CORRECT_MESSAGE = props.getProperty("scan.announce.correct_message");
            CORRECT_MESSAGE_DIFFERENT_SERVICES = props.getProperty("scan.announce.correct_message_different_services");
            CORRECT_MESSAGE_DIFFERENT_DEVICE = props.getProperty("scan.announce.correct_message_different_device");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
