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

package com.hbm.devices.scan.messages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.messages.MessageParser;

public class AnnounceCacheTest {

    private FakeMessageReceiver fakeReceiver;
    private MessageParser parser;

    @Before
    public void setup() {
        this.fakeReceiver = new FakeMessageReceiver();
        this.parser = new MessageParser();

        fakeReceiver.addObserver(parser);
    }

    @Test
    public void AddDeviceToCacheTest() {
        fakeReceiver.emitSingleCorrectMessage();
        assertTrue(parser.getCache().getCacheAmount() == 1);
        assertTrue(parser.getCache().getPathsAmount() == 1);
        assertTrue(parser.getCache().hasStringInCache(FakeMessageReceiver.CORRECT_MESSAGE));

        fakeReceiver.emitSingleCorrectMessageDifferentDevice();

        assertTrue(parser.getCache().getCacheAmount() == 2);
        assertTrue(parser.getCache().getPathsAmount() == 2);
        assertTrue(parser.getCache().hasStringInCache(FakeMessageReceiver.CORRECT_MESSAGE));
        assertTrue(parser.getCache().hasStringInCache(
                FakeMessageReceiver.CORRECT_MESSAGE_DIFFERENT_DEVICE));
    }

    @Test
    public void GetFromCacheTest() {
        fakeReceiver.emitSingleCorrectMessage();
        assertNotNull(parser.getCache().getAnnounceByString(FakeMessageReceiver.CORRECT_MESSAGE));
    }

    @Test
    public void DontAddTwiceTest() {
        fakeReceiver.emitSingleCorrectMessage();
        fakeReceiver.emitSingleCorrectMessage();
        assertTrue(parser.getCache().getCacheAmount() == 1);
        assertTrue(parser.getCache().getPathsAmount() == 1);
    }

    @Test
    public void UpdateDeviceEntry() {
        fakeReceiver.emitSingleCorrectMessage();
        fakeReceiver.emitSingleCorrectMessageDifferentServices();

        assertTrue(parser.getCache().getCacheAmount() == 1);
        assertTrue(parser.getCache().getPathsAmount() == 1);
        assertFalse(parser.getCache().hasStringInCache(FakeMessageReceiver.CORRECT_MESSAGE));
        assertTrue(parser.getCache().hasStringInCache(
                FakeMessageReceiver.CORRECT_MESSAGE_DIFFERENT_SERVICES));
    }

}
