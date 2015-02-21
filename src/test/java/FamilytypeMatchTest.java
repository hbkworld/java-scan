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

import org.junit.Before;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.announce.filter.FamilytypeMatch;
import com.hbm.devices.scan.announce.filter.Filter;
import com.hbm.devices.scan.announce.filter.Matcher;
import com.hbm.devices.scan.messages.AnnounceParser;
import com.hbm.devices.scan.messages.CommunicationPath;

public class FamilytypeMatchTest {

    private CommunicationPath cp;
    private FakeMessageReceiver fsmmr;
    private final String[] families = {"QuantumX", "PMX"};
    private final Matcher matcher = new FamilytypeMatch(families);
    private Filter filter;

    @Before
    public void setUp() {
        fsmmr = new FakeMessageReceiver();
        AnnounceParser parser = new AnnounceParser();
        fsmmr.addObserver(parser);
        filter = new Filter(matcher);
        parser.addObserver(filter);
        filter.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                cp = (CommunicationPath)arg;
            }
        });
    }

    @Test
    public void checkFamilyTypes() {
        assertArrayEquals("filter strings for FamilytypeMatcher are not correct", families, matcher.getFilterStrings());
        assertEquals("matcher are not equal", matcher, filter.getMatcher());
    }

    @Test
    public void ftFilterCorrectMessage() {
        fsmmr.emitSingleCorrectMessage();
        assertNotNull("Didn't got a CommunicationPath object", cp);
    }

    @Test
    public void ftFilterMissingFamilyTypeMessage() {
        fsmmr.emitMissingFamilyTypeMessage();
        assertNull("Got CommunicationPath object despite missing family type", cp);
    }
}

