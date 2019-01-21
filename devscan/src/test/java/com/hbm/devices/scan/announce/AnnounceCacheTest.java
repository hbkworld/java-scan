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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import com.hbm.devices.scan.FakeMessageReceiver;

public class AnnounceCacheTest {

    private static final String CORRECT_MESSAGE;
    private static final String CORRECT_MESSAGE_DIFFERENT_SERVICES;
    private static final String CORRECT_MESSAGE_DIFFERENT_DEVICE;

    private FakeMessageReceiver fakeReceiver;
    private AnnounceDeserializer parser;

    @BeforeEach
    public void setUp() {
        this.fakeReceiver = new FakeMessageReceiver();
        this.parser = new AnnounceDeserializer();
        fakeReceiver.addObserver(parser);
    }

    @Test
    public void addDeviceToCacheTest() {
        fakeReceiver.emitSingleCorrectMessage();
        assertSame(parser.getCache().size(), 1, "Entries in cache != 1");
        assertSame(parser.getCache().lastAnnounceSize(), 1, "Paths in cache != 1");
        assertNotNull(parser.getCache().get(CORRECT_MESSAGE), "JSON message not in cache");

        fakeReceiver.emitSingleCorrectMessageDifferentDevice();
        assertSame(parser.getCache().size(), 2, "Entries in cache != 2");
        assertSame(parser.getCache().lastAnnounceSize(), 2, "Paths in cache != 2");
        assertNotNull(parser.getCache().get(CORRECT_MESSAGE), "JSON message not in cache");
        assertNotNull(parser.getCache().get(CORRECT_MESSAGE_DIFFERENT_DEVICE), "JSON message not in cache");
    }

    @Test
    public void getFromCacheTest() {
        fakeReceiver.emitSingleCorrectMessage();
        assertNotNull(parser.getCache().get(CORRECT_MESSAGE), "Correct message not in cache");
    }

    @Test
    public void dontAddTwiceTest() {
        fakeReceiver.emitSingleCorrectMessage();
        fakeReceiver.emitSingleCorrectMessage();
        assertSame(parser.getCache().size(), 1, "Message was added more than once");
        assertSame(parser.getCache().lastAnnounceSize(), 1, "Message was added more than once");
    }

    @Test
    public void updateDeviceEntry() {
        fakeReceiver.emitSingleCorrectMessage();
        fakeReceiver.emitSingleCorrectMessageDifferentServices();

        assertSame(parser.getCache().size(), 1, "Correct message not in cache");
        assertSame(parser.getCache().lastAnnounceSize(), 1, "Correct message not in cache");
        assertNull(parser.getCache().get(CORRECT_MESSAGE), "Original message still in cache");
        assertNotNull(parser.getCache().get(CORRECT_MESSAGE_DIFFERENT_SERVICES), "New message not in cache");
    }

    static {
        try (final InputStream is = AnnounceCacheTest.class.getResourceAsStream("/fakemessages.properties")) {
            final Properties props = new Properties();
            props.load(is);

            CORRECT_MESSAGE = props.getProperty("scan.announce.correctMessage");
            CORRECT_MESSAGE_DIFFERENT_SERVICES = props.getProperty("scan.announce.correctMessageDifferentServices");
            CORRECT_MESSAGE_DIFFERENT_DEVICE = props.getProperty("scan.announce.correctMessageDifferentDevice");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
