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

package com.hbm.devices.scan.announce;

import java.io.IOException;

import com.hbm.devices.scan.MulticastMessageReceiver;
import com.hbm.devices.scan.ScanConstants;

/**
 * Convenience class to receive announce multicast messages.
 * <p>
 *
 * @since 1.0
 */
public final class AnnounceReceiver extends MulticastMessageReceiver {

    /**
     * Constructs an {@code AnnounceReceiver} object.
     *
     * @throws java.io.IOException if the AnnounceReceiver can't
     * created. This might happen if the underlying socket can't be
     * created or the multicast join was not successful.
     *
     * @since 1.0
     */
    public AnnounceReceiver() throws IOException {
        super(ScanConstants.ANNOUNCE_ADDRESS, ScanConstants.ANNOUNCE_PORT);
    }
}
