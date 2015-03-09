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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.announce.Announce;
import com.hbm.devices.scan.announce.AnnounceDeserializer;
import com.hbm.devices.scan.announce.filter.Filter;
import com.hbm.devices.scan.announce.filter.Matcher;
import com.hbm.devices.scan.announce.filter.UUIDMatch;

public class UUIDMatcherTest {

    private Announce announce;
    private FakeMessageReceiver fsmmr;
    private final String[] uuids = {"fred", "0009E500123A", "blah"};
    private final String[] noMatchingUuids = {"fred", "0009E500123B", "blah"};
    private Matcher matcher;
    private Filter filter;

    @Test
    public void checkUUIDs() {
        Matcher matcher = new UUIDMatch(uuids);
        filter = new Filter(matcher);
        assertArrayEquals("filter strings for ServicetypeMatcher are not correct", uuids, matcher.getFilterStrings());
        assertEquals("matchers are not equal", matcher, filter.getMatcher());
    }

    @Test
    public void filterCorrectMessage() {
        Matcher matcher = new UUIDMatch(uuids);
        filter = new Filter(matcher);
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
        fsmmr.addObserver(parser);
        parser.addObserver(filter);
        filter.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                announce = (Announce)arg;
            }
        });

        fsmmr.emitSingleCorrectMessage();
        assertNotNull("Didn't got a Announce object", announce);
    }

    @Test
    public void filterCorrectMessageNoMatch() {
        Matcher matcher = new UUIDMatch(noMatchingUuids);
        filter = new Filter(matcher);
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
        fsmmr.addObserver(parser);
        parser.addObserver(filter);
        filter.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                announce = (Announce)arg;
            }
        });

        fsmmr.emitSingleCorrectMessage();
        assertNull("Got Announce object despite not matching", announce);
    }

    @Test
    public void filterMissingUUIDMessage() {
        Matcher matcher = new UUIDMatch(uuids);
        filter = new Filter(matcher);
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
        fsmmr.addObserver(parser);
        parser.addObserver(filter);
        filter.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                announce = (Announce)arg;
            }
        });

        fsmmr.emitMissingDeviceUuidMessage();
        assertNull("Got Announce object despite empty service section", announce);
    }
}

