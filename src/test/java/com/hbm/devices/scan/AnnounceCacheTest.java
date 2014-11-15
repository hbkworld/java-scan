package com.hbm.devices.scan;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.MessageParser;

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
