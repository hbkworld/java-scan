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

package com.hbm.devices.scan.announce.filter;

import com.hbm.devices.scan.announce.Announce;

/**
 * This class matches family type information in Announce objects.
 * <p>
 *
 * @since 1.0
 */
public final class FamilytypeMatch implements Matcher {

    private final String[] familyTypes;

    /**
     * Creates a {@link Matcher} object that matches the familyType
     * member of an {@link Announce} object againts an array of
     * {@link String}s.
     *
     * @param familyTypes An array of {@link String}s used to match
     * against the familyType in {@link Announce} objects.
     */
    public FamilytypeMatch(String... familyTypes) {
        this.familyTypes = familyTypes.clone();
    }

    @Override
    public boolean match(Announce announce) {
        final String familyType = announce.getParams().getDevice().getFamilyType();
        for (int i = 0; i < familyTypes.length; i++) {
            if (familyTypes[i].equals(familyType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getFilterStrings() {
        return familyTypes.clone();
    }
}
