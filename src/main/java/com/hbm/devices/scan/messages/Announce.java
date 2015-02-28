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

package com.hbm.devices.scan.messages;

/**
 * This class hold all information required to implement the HBM network
 * discovery and configuration protocol.
 */
public final class Announce extends JsonRpc {

    private AnnounceParams params;
    private String path;

    private static final int INITIAL_HASHCODE_BUFFER_SIZE = 100;

    Announce() {
        super("announce");
    }

    public AnnounceParams getParams() throws MissingDataException {
        if (params == null) {
            throw new MissingDataException("No device section in announce params!");
        }
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Announce)) {
            return false;
        }
        final Announce rhs = (Announce)obj;
        return this.getJSONString().equals(rhs.getJSONString());
    }

    @Override
    public int hashCode() {
        return getJSONString().hashCode();
    }

    public String getPath() {
        return path;
    }

    void identifyCommunicationPath() throws MissingDataException {
        final AnnounceParams parameters = getParams();
        final String deviceUuid = parameters.getDevice().getUuid();
        final StringBuilder hashBuilder = new StringBuilder(INITIAL_HASHCODE_BUFFER_SIZE);
        hashBuilder.append(deviceUuid);

        final Router router = parameters.getRouter();
        if (router != null) {
            hashBuilder.append(router.getUuid());
        }

        final String deviceInterfaceName = parameters.getNetSettings().getInterface().getName();
        hashBuilder.append(deviceInterfaceName);
        path = hashBuilder.toString();
    }
}
