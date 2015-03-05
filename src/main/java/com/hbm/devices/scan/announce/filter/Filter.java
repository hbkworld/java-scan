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

import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.messages.Announce;

/**
 * This class filters {@link Announce} objects with according to a {@link Matcher} object.
 * <p>
 * The class reads {@link Announce} objects and notifies them if
 * {@link Matcher#match(Announce)} method returns true.
 * 
 * @since 1.0
 */
public final class Filter extends Observable implements Observer {

    private final Matcher matcher;

    /**
     * Constructs a {@link Filter} object.
     *
     * @param matcher The matcher object that decides if a message is
     * filtered out or not.
     */
    public Filter(Matcher matcher) {
        super();

        this.matcher = matcher;
    }

    public Matcher getMatcher() {
        return this.matcher;
    }

    @Override
    public void update(Observable observable, Object arg) {
        final Announce announce = (Announce)arg;
        if (matcher.match(announce)) {
            setChanged();
            notifyObservers(announce);
        }
    }
}
