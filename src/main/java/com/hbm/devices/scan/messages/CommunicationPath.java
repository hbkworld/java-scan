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
 * This class carries parsed announce messages.
 * <p>
 * If it would be possible to retrieve the information which {@link
 * java.net.NetworkInterface} received an announce messages, this will
 * also handled here to provide all information necessary to decide if
 * an IP communication is possible to the announced device.
 * <p>
 * Please note that the {@link #hashCode()} method is overridden. The
 * hash code of this announce object is unique for the communication
 * path the {@link Announce} message traveled.
 *
 * @since 1.0
 */
public final class CommunicationPath {

    private final Announce announce;
    private final int hash;
    private Object cookie;
    private static final int INITIAL_HASHCODE_BUFFER_SIZE = 100;

    CommunicationPath(Announce announce) throws MissingDataException {
        this.announce = announce;

        final String deviceUuid = announce.getParams().getDevice().getUuid();
        final StringBuilder hashBuilder = new StringBuilder(INITIAL_HASHCODE_BUFFER_SIZE);
        hashBuilder.append(deviceUuid);

        final Router router = announce.getParams().getRouter();
        if (router != null) {
            final String routerUuid = router.getUuid();
            if (routerUuid == null) {
                throw new MissingDataException("No router UUID in announce object!");
            }
            hashBuilder.append(routerUuid);
        }

        final String deviceInterfaceName = announce.getParams().getNetSettings().getInterface().getName();
        hashBuilder.append(deviceInterfaceName);
        hash = hashBuilder.toString().hashCode();
    }

    public void setCookie(Object cookie) {
        this.cookie = cookie;
    }

    public Object getCookie() {
        return cookie;
    }

    /**
     * Calculates a unique hash for a communication path.
     * <p>
     * Currently the device uuid, the router uuid and the interface name
     * of the sending device are take into the hash calculation.
     */
    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CommunicationPath)) {
            return false;
        }

        return hash == obj.hashCode();
    }
    
    public Announce getAnnounce() {
        return announce;
    }
    
}
