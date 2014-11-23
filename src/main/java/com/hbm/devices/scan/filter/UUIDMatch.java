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

package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.messages.MissingDataException;
import com.hbm.devices.scan.messages.Announce;

/**
 * This class matches device uuids in Announce objects.
 * 
 * @since 1.0
 *
 */
public class UUIDMatch implements Matcher {

    private String[] uuids;

    public UUIDMatch(String[] uuids) {
        this.uuids = uuids.clone();
    }

    @Override
    public boolean match(Announce announce) throws MissingDataException {
        String deviceUUID = announce.getParams().getDevice().getUuid();
        for (String s : uuids) {
            if (s.equals(deviceUUID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getMatcherName() {
        return "UUID";
    }

    @Override
    public String[] getFilterStrings() {
        return uuids.clone();
    }
}
