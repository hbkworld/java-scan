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
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.announce.filter.Filter;
import com.hbm.devices.scan.announce.filter.Matcher;
import com.hbm.devices.scan.announce.filter.ServicetypeMatch;
import com.hbm.devices.scan.announce.Announce;
import com.hbm.devices.scan.announce.AnnounceDeserializer;

public class ServicetypeMatchTest {

    private Announce announce;
    private FakeMessageReceiver fsmmr;
    private final String[] serviceTypes = {"http"};
    private final Matcher matcher = new ServicetypeMatch(serviceTypes);
    private Filter filter;

    @BeforeEach
    public void setUp() {
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
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
        assertArrayEquals(serviceTypes, matcher.getFilterStrings(), "filter strings for ServicetypeMatcher are not correct");
        assertEquals(matcher, filter.getMatcher(), "matchers are not equal");
    }

    @Test
    public void stFilterCorrectMessage() {
        fsmmr.emitSingleCorrectMessage();
        assertNotNull(announce, "Didn't got a Announce object");
    }

    @Test
    public void stFilterEmptyServiceMessage() {
        fsmmr.emitEmptyServiceMessage();
        assertNull(announce, "Got Announce object despite empty service section");
    }

    @Test
    public void stFilterMissingServiceMessage() {
        fsmmr.emitMissingServiceMessage();
        assertNull(announce, "Got Announce object despite missing service section");
    }

    @Test
    public void stFilterMissingHttpMessage() {
        fsmmr.emitMissingHttpMessage();
        assertNull(announce, "Got Announce object despite missing http entry");
    }
}

