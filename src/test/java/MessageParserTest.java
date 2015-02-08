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
import com.hbm.devices.scan.messages.CommunicationPath;
import com.hbm.devices.scan.messages.MessageParser;
import com.hbm.devices.scan.messages.Response;

public class MessageParserTest {

    private CommunicationPath cp;
    private Response res;
    private FakeMessageReceiver fsmmr;

    @Before
    public void setUp() {
        fsmmr = new FakeMessageReceiver();
        MessageParser jf = new MessageParser();
        fsmmr.addObserver(jf);
        jf.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof CommunicationPath) {
                    cp = (CommunicationPath) arg;
                } else if (arg instanceof Response) {
                    res = (Response) arg;
                }
            }
        });
    }

    @Test
    public void parseCorrectMessage() {
        fsmmr.emitSingleCorrectMessage();
        assertNotNull("No CommunictionPath object after correct message", cp);
    }

    @Test
    public void parseInvalidJsonMessage() {
        fsmmr.emitInvalidJsonMessage();
        assertNull("Got CommunicationPath object after invalid message", cp);
    }

    @Test
    public void parseEmptyMessage() {
        fsmmr.emitEmptyString();
        assertNull("Got CommunicationPath object after empty message", cp);
    }

    @Test
    public void parseNullMessage() {
        fsmmr.emitNull();
        assertNull("Got CommunicationPath object after null", cp);
    }

    @Test
    public void parseMissingDeviceMessage() {
        fsmmr.emitMissingDeviceMessage();
        assertNull("Got CommunicationPath from message without device", cp);
    }

    @Test
    public void parseMissingDeviceUuidMessage() {
        fsmmr.emitMissingDeviceUuidMessage();
        assertNull("Got CommunicationPath from message without UUID", cp);
    }

    @Test
    public void parseMissingParamsMessage() {
        fsmmr.emitMissingParamsMessage();
        assertNull("Got CommunicationPath from message without params", cp);
    }

    @Test
    public void parseNoInterfaceNameMessage() {
        fsmmr.emitNoInterfaceNameMessage();
        assertNull("Got CommunicationPath from message without interface name", cp);
    }

    @Test
    public void parseNoInterfaceMessage() {
        fsmmr.emitNoInterfaceMessage();
        assertNull("Got CommunicationPath from message without interface", cp);
    }

    @Test
    public void parseNoNetSettingsMessage() {
        fsmmr.emitNoNetSettingsMessage();
        assertNull("Got CommunicationPath from message without network settings", cp);
    }

    @Test
    public void parseMissingRouterUuidMessage() {
        fsmmr.emitMissingRouterUuidMessage();
        assertNull("Got CommunicationPath from message without router UUID", cp);
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
    public void parseNoSuccessIdResponseMessage() {
        fsmmr.emitNoSuccessIdResponseMessage();
        assertNull("Got result object from response without ID", res);
    }

    @Test
    public void parseMissingErrorObjectResponseMessage() {
        fsmmr.emitMissingErrorObjectResponseMessage();
        assertNull("Got result object from response without error object", res);
    }

    @Test
    public void parseNoErrorCodeResponseMessage() {
        fsmmr.emitNoErrorCodeResponseMessage();
        assertNull("Got result object from response without error code", res);
    }

    @Test
    public void parseNoErrorMessageResponseMessage() {
        fsmmr.emitNoErrorMessageResponseMessage();
        assertNull("Got result object from response without error message", res);
    }
}
