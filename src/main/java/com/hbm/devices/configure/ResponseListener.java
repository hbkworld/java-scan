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

package com.hbm.devices.configure;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.ResponseReceiver;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.MessageParser;
import com.hbm.devices.scan.messages.Response;

/**
 * This class is used to start a thread which listens to incoming messages. When a message is
 * received, it is parsed into an object via {@link MessageParser}. If this object is a
 * {@link Response} object, it is forwarded to all {@link Observer}.
 * 
 * @since 1.0
 *
 */
public class ResponseListener extends Observable implements Observer, Runnable {

    private ResponseReceiver responseReceiver;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    /**
     * This is the default constructor to instantiate a ReponseListener
     */
    public ResponseListener() {
        try {
            responseReceiver = new ResponseReceiver();

            MessageParser messageParser = new MessageParser();

            responseReceiver.addObserver(messageParser);
            messageParser.addObserver(this);

        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Can't instantiate response receiver!", e);
        }
    }

    /**
     * Don't use this constructor. It is only used for the JUnit tests
     * 
     */
    @SuppressWarnings("unused")
    private ResponseListener(Observable receiver) {
        MessageParser messageParser = new MessageParser();

        receiver.addObserver(messageParser);
        messageParser.addObserver(this);
    }

    /**
     * This method starts the listening socket to receive incoming responses
     */
    @Override
    public void run() {
        responseReceiver.start();
    }

    /**
     * This method stops the listening socket
     */
    public void stop() {
        responseReceiver.stop();
    }

    /**
     * This method filters the received and parsed packets. So only {@link Response} objects are
     * forwarded.
     */
    @Override
    public void update(Observable o, Object obj) {
        if (obj instanceof Response) {
            Response response = (Response) obj;

            setChanged();
            notifyObservers(response);
        }
    }
}
