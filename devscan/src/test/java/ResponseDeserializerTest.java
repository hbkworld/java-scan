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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.configure.ErrorObject;
import com.hbm.devices.scan.configure.Response;
import com.hbm.devices.scan.configure.ResponseDeserializer;

public class ResponseDeserializerTest {

    private Response res;
    private FakeMessageReceiver fsmmr;

    @BeforeEach
    public void setUp() {
        fsmmr = new FakeMessageReceiver();
        ResponseDeserializer parser = new ResponseDeserializer();
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
        assertNotNull(res, "No result object after correct success response");
    }

    @Test
    public void parseCorrectErrorReponseMessage() {
        fsmmr.emitSingleCorrectErrorResponseMessage();
        assertNotNull(res, "No result object after correct error response");
    }

    @Test
    public void parseErrorAndResultResponseMessage() {
        fsmmr.emitErrorAndResultResponseMessage();
        assertNull(res, "Got result object from response with error and result");
    }

    @Test
    public void parseMissingIdMessage() {
        fsmmr.emitNoSuccessIdResponseMessage();
        assertNull(res, "Got result object from response without id");
    }

    @Test
    public void parseEmptyIdMessage() {
        fsmmr.emitEmtpySuccessIdResponseMessage();
        assertNull(res, "Got result object from response without id");
    }

    @Test
    public void parseMethodMessage() {
        fsmmr.emitMissingMethodMessage();
        assertNull(res, "Got result object from response without error and result");
    }

    @Test
    public void parseInvalidJsonMessage() {
        fsmmr.emitInvalidJsonMessage();
        assertNull(res, "Got result object after invalid message");
    }

    @Test
    public void parseErrorResponse() {
        final String jsonRpcVersion = "2.0";
        final String id = "12345";
        final int errorCode = -12;
        final String errorMessage = "Karoline";
        final String errorData = "Error data";

        final JsonObject root = new JsonObject();
        root.addProperty("jsonrpc", jsonRpcVersion);
        root.addProperty("id", id);
        final JsonObject error = new JsonObject();
        root.add("error", error);
        error.addProperty("code", errorCode);
        error.addProperty("message", errorMessage);
        error.addProperty("data", errorData);

        final Gson gson = new Gson();
        fsmmr.emitString(gson.toJson(root));

        assertNotNull(res, "No result object after correct success response");
        assertEquals(res.getJsonrpc(), jsonRpcVersion, "JSON-RPC versions not equal");
        assertEquals(res.getId(), id, "Id's not equal");
        ErrorObject checkError = res.getError();
        assertNotNull(checkError, "Expected error object");
        assertNull(res.getResult(), "Unexpected result object");
        assertEquals(checkError.getCode(), errorCode, "error code not equal");
        assertEquals(checkError.getMessage(), errorMessage, "error message not equal");
        assertEquals(checkError.getData(), errorData, "error data not equal");
    }
}
