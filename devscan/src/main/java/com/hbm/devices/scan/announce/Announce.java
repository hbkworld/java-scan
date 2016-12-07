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

package com.hbm.devices.scan.announce;

import java.io.Serializable;

import com.hbm.devices.scan.JsonRpc;

/**
 * This class holds all information required to implement the HBM network
 * discovery and configuration protocol.
 */
public final class Announce extends JsonRpc implements Serializable {

    private AnnounceParams params;
    private String path;
    private transient Object cookie;

    private static final long serialVersionUID = 3398751494808132238L;

    private static final int INITIAL_HASHCODE_BUFFER_SIZE = 100;

    Announce() {
        super("announce");
    }
    
    /**
     * Get the parameters of an announce message.
     *
     * @return the parameters. It is guaranteed by the {@link
     * AnnounceDeserializer} that only valid announces are forwarded
     * through the chain of observers, so a null reference is never
     * returned from this method.
     */
    public AnnounceParams getParams() {
        return params;
    }
    
    /**
     * Retrieve the auxiliary data previously set by {@link #setCookie}
     *
     * @return the auxiliary data
     */
    public Object getCookie() {
        return cookie;
    }
    
    /**
     * This method can be used to bind some auxiliary data to an
     * Announce object.
     *
     * Caution! This object will not serialized!
     *
     * @param cookie the auxiliary data to be set
     */
    public void setCookie(Object cookie) {
        this.cookie = cookie;
    }

    /**
     * This method checks if another Announce object has the same
     * communication path.
     *
     * @param other the Announce object to check against
     *
     * @return true if both Announces have the same communication path,
     * false otherwise.
     */
    public boolean sameCommunicationPath(Announce other) {
        return other.getPath().equals(path);
    }

    /**
     * Computes a unique ID representing the communication path of the
     * Announce.
     *
     * @return an integer representing the communication path.
     */
    public int getCommunicationPathId() {
        return path.hashCode();
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

    String getPath() {
        return path;
    }

    private static String getDeviceUUID(AnnounceParams parameters) throws MissingDataException {
        final Device device = parameters.getDevice();
        if (device == null) {
            throw new MissingDataException("No device section in announce!");
        }
        final String deviceUUID = parameters.getDevice().getUuid();
        if (deviceUUID == null || deviceUUID.length() == 0) {
            throw new MissingDataException("No device UUID in announce!");
        }
        return deviceUUID;
    }

    private static String getRouterUUID(Router router) throws MissingDataException {
        final String uuid = router.getUuid();
        if (uuid == null || uuid.length() == 0) {
            throw new MissingDataException("Router uuid either null or of zero length!");
        }
        return uuid;
    }

    private static String getInterfaceName(AnnounceParams parameters) throws MissingDataException {
        final NetSettings settings = parameters.getNetSettings();
        if (settings == null) {
            throw new MissingDataException("No network settings in announce!");
        }
        final Interface iface = settings.getInterface();
        if (iface == null) {
            throw new MissingDataException("No interface section in announce!");
        }
        final String interfaceName = iface.getName();
        if (interfaceName == null || interfaceName.length() == 0) {
            throw new MissingDataException("No interface name in announce!");
        }
        return interfaceName;
    }

    void identifyCommunicationPath() throws MissingDataException {
        final AnnounceParams parameters = getParams();
        if (parameters == null) {
            throw new MissingDataException("No parameters in announce!");
        }

        final StringBuilder hashBuilder = new StringBuilder(INITIAL_HASHCODE_BUFFER_SIZE);
        hashBuilder.append(getDeviceUUID(parameters));

        final Router router = parameters.getRouter();
        if (router != null) {
            hashBuilder.append(getRouterUUID(router));
        }
        
        hashBuilder.append(getInterfaceName(parameters));
        path = hashBuilder.toString();
    }
}
