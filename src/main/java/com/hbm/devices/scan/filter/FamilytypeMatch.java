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

    private final String[] familyTypes;

    public FamilytypeMatch(String[] familyTypes) {
        this.familyTypes = familyTypes.clone();
    }

    @Override
    public boolean match(Announce a) throws MissingDataException {
        final String ft = a.getParams().getDevice().getFamilyType();
        for (int i = 0; i < familyTypes.length; i++) {
            if (familyTypes[i].equals(ft)) {
                return true;
            }
        }
        return false;
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
