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

package com.hbm.devices.scan.announce.filter;

import com.hbm.devices.scan.messages.MissingDataException;
import com.hbm.devices.scan.messages.Announce;

/**
 * This class matches device uuids in Announce objects.
 * 
 * @since 1.0
 *
 */
public final class UUIDMatch implements Matcher {

    private final String[] uuids;

    public UUIDMatch(String... uuids) {
        this.uuids = uuids.clone();
    }

    @Override
    public boolean match(Announce announce) throws MissingDataException {
        final String deviceUUID = announce.getParams().getDevice().getUuid();
        for (final String s : uuids) {
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
