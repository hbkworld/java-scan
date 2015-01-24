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

package com.hbm.devices.scan;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;
import java.util.logging.Logger;

import com.hbm.devices.scan.ScanConstants;

public class StressTestMessageReceiver extends Observable implements MessageReceiver {

    private List<String> deviceUuidList;
    private int loopAmount;

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private long startNanoTime;

    public StressTestMessageReceiver(int deviceAmount, int loopAmount) {
        this.loopAmount = loopAmount;
        this.deviceUuidList = new LinkedList<String>();

        for (int i = 0; i < deviceAmount; i++) {
            this.deviceUuidList.add(getAnnounceString(UUID.randomUUID().toString()));
        }
    }

    private static String getAnnounceString(String uuid) {
        return "{" + "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
                + "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
                + "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
                + "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"" + uuid + "\"},"
                + "\"expiration\":15," + "\"netSettings\":{"
                + "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
                + "\"interface\":{\"description\":\"ethernet backplane side\","
                + "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
                + "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
                + "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}," + "\"services\":["
                + "{\"port\":7411,\"type\":\"daqStream\"},"
                + "{\"port\":8080,\"type\":\"daqStreamWS\"},"
                + "{\"port\":5001,\"type\":\"hbmProtocol\"}," + "{\"port\":80,\"type\":\"http\"},"
                + "{\"port\":11122,\"type\":\"jetd\"}," + "{\"port\":11123,\"type\":\"jetws\"},"
                + "{\"port\":22,\"type\":\"ssh\"}" + "]" + "}" + "}";
    }

    public long getStartNanoTime() {
        return this.startNanoTime;
    }

    @Override
    public void start() {
        LOGGER.info("Starting Test: " + loopAmount + " x " + deviceUuidList.size() + " Announces\n");
        this.startNanoTime = System.nanoTime();

        for (int i = 0; i < loopAmount; i++) {
            for (final String s : this.deviceUuidList) {
                setChanged();
                notifyObservers(s);
            }
        }

        LOGGER.info("Duration for sending & parsing " + loopAmount + " x "
                + deviceUuidList.size() + " announces: "
                + ((System.nanoTime() - startNanoTime) / 1000000d) + "ms\n");

    }

    @Override
    public void stop() {
    }

}
