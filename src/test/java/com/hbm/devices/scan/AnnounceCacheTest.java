/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
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
