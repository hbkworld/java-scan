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
import com.hbm.devices.scan.announce.filter.Filter;
import com.hbm.devices.scan.announce.filter.Matcher;
import com.hbm.devices.scan.announce.filter.ServicetypeMatch;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.AnnounceParser;

public class ServicetypeMatchTest {

    private Announce announce;
    private FakeMessageReceiver fsmmr;
    private final String[] serviceTypes = {"http"};
    private final Matcher matcher = new ServicetypeMatch(serviceTypes);
    private Filter filter;

    @Before
    public void setUp() {
        fsmmr = new FakeMessageReceiver();
        AnnounceParser parser = new AnnounceParser();
        fsmmr.addObserver(parser);

        filter = new Filter(matcher);
        parser.addObserver(filter);
        filter.addObserver(new Observer(){
            public void update(Observable o, Object arg) {
                announce = (Announce)arg;
            }
        });
    }

    @Test
    public void checkServiceTypes() {
        assertArrayEquals("filter strings for ServicetypeMatcher are not correct", serviceTypes, matcher.getFilterStrings());
        assertEquals("matchers are not equal", matcher, filter.getMatcher());
    }

    @Test
    public void stFilterCorrectMessage() {
        fsmmr.emitSingleCorrectMessage();
        assertNotNull("Didn't got a Announce object", announce);
    }

    @Test
    public void stFilterEmptyServiceMessage() {
        fsmmr.emitEmptyServiceMessage();
        assertNull("Got Announce object despite empty service section", announce);
    }

    @Test
    public void stFilterMissingServiceMessage() {
        fsmmr.emitMissingServiceMessage();
        assertNull("Got Announce object despite missing service section", announce);
    }

    @Test
    public void stFilterMissingHttpMessage() {
        fsmmr.emitMissingHttpMessage();
        assertNull("Got Announce object despite missing http entry", announce);
    }
}

