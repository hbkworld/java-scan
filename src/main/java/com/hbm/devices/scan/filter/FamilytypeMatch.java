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

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.MissingDataException;

/**
 * This class matches family type information in Announce objects.
 * <p>
 *
 * @since 1.0
 */
public class FamilytypeMatch implements Matcher {

    private String[] familyTypes;

    public FamilytypeMatch(String[] familyTypes) {
        this.familyTypes = familyTypes.clone();
    }

    @Override
    public boolean match(Announce a) throws MissingDataException {
        try {
            String ft = a.getParams().getDevice().getFamilyType();
            for (int i = 0; i < familyTypes.length; i++) {
                if (ft.equals(familyTypes[i])) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            throw new MissingDataException("No family type in announce object!", e);
        }
    }

    @Override
    public String getMatcherName() {
        return "Famility type";
    }

    @Override
    public String[] getFilterStrings() {
        return familyTypes.clone();
    }
}
