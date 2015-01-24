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

import java.util.logging.Logger;

import com.hbm.devices.configure.ConfigCallback;
import com.hbm.devices.configure.ConfigQuery;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.Response;

public class SimpleCallback implements ConfigCallback {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    @Override
    public void onSuccess(ConfigQuery configQuery, Response response) {
        LOGGER.info("Success:\n");
        LOGGER.info(" result: " + response.getResult() + "\n");
    }

    @Override
    public void onError(ConfigQuery configQuery, Response response) {
        LOGGER.info("Error:\n");
        LOGGER.info(" code: " + response.getError().getCode() + "\n");
        LOGGER.info(" message: " + response.getError().getMessage() + "\n");
        LOGGER.info(" data: " + response.getError().getData() + "\n");
    }

    @Override
    public void onTimeout(ConfigQuery configQuery) {
        LOGGER.info("No response is received in " + configQuery.getTimeout() + "ms\n");
    }
}
