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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.messages.Response;
import com.hbm.devices.scan.messages.ResponseParser;

public class ResponseParserTest {

    private Response res;
    private FakeMessageReceiver fsmmr;

    @Before
    public void setUp() {
        fsmmr = new FakeMessageReceiver();
        ResponseParser parser = new ResponseParser();
        fsmmr.addObserver(parser);
        parser.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof Response) {
                    res = (Response)arg;
                }
            }
        });
    }

    @Test
    public void parseCorrectSuccessReponseMessage() {
        fsmmr.emitSingleCorrectSuccessResponseMessage("TEST-UUID");
        assertNotNull("No result object after correct success response", res);
    }

    @Test
    public void parseCorrectErrorReponseMessage() {
        fsmmr.emitSingleCorrectErrorResponseMessage();
        assertNotNull("No result object after correct error response", res);
    }

    @Test
    public void parseErrorAndResultResponseMessage() {
        fsmmr.emitErrorAndResultResponseMessage();
        assertNull("Got result object from response with error and result", res);
    }

    @Test
    public void parseMissingTypeMessage() {
        fsmmr.emitMissingTypeResponseMessage();
        assertNull("Got result object from response with error and result", res);
    }
}
