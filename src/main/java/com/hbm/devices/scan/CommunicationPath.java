/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
 */

package com.hbm.devices.scan;

import com.hbm.devices.scan.messages.*;

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
public class CommunicationPath {

    private Announce announce;
    private int hash;
    private Object cookie;
    private static final int INITIAL_HASHCODE_BUFFER_SIZE = 100;

    CommunicationPath(Announce announce) throws MissingDataException {
        this.announce = announce;
        StringBuilder sb = new StringBuilder(INITIAL_HASHCODE_BUFFER_SIZE);
        try {
            String deviceUuid = announce.getParams().getDevice().getUuid();
            if (deviceUuid == null) {
                throw new MissingDataException("No device UUID in announce object!");
            }
            sb.append(deviceUuid);

            Router router = announce.getParams().getRouter();
            if (router != null) {
                String routerUuid = router.getUuid();
                if (routerUuid == null) {
                    throw new MissingDataException("No router UUID in announce object!");
                }
                sb.append(routerUuid);
            }

            String deviceInterfaceName = announce.getParams().getNetSettings().getInterface().getName();
            if (deviceInterfaceName == null) {
                throw new MissingDataException("No network interface name in announce object!");
            }
            sb.append(deviceInterfaceName);
            hash = sb.toString().hashCode();
        } catch (NullPointerException e) {
            throw new MissingDataException("Information missing in announce object!", e);
        }
    }

    public void setCookie(Object c) {
        cookie = c;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommunicationPath)) {
            return false;
        }

        return hash == o.hashCode();
    }
    
    public Announce getAnnounce() {
        return announce;
    }
    
}
