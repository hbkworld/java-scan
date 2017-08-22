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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Objects of this class hold the information describing the properties
 * of an network interface.
 * 
 * @since 1.0
 */
public final class Interface implements Serializable {

    private static final long serialVersionUID = 8110401485735664487L;

    String name;
    String type;
    String description;
    List<IPEntry> ipList;

    Interface() {
    }

    /**
     * @return
     *      A string containing the name of the interface. For
     *      Linux systems typically something like eth0,
     *      eth1, ... .
     *      It is guaranteed by the {@link AnnounceDeserializer} that
     *      only valid announces are forwarded through the chain of
     *      observers, so a null reference is never returned from this
     *      method.
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     *      A string containing the type of the interface or {@code null} if not
     *      announced.
     *      For QuantumX systems it might be useful to distinguish
     *      Ethernet and Firewire interfaces.
     */
    public String getType() {
        return type;
    }

    /**
     * @return
     *      A string containing some additional
     *      information or {@code null} if not announced. QuantumX devices
     *      report whether the
     *      interface is on the front or back side.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return
     *      an {@link java.util.Collections#unmodifiableList(List list) unmodifiable List}
     *      containing all IPv4 addresses of the interface
     *      with their netmask. If no IP address was announced,
     *      an empty {@link java.util.List} is returned.
     */
    public List<IPEntry> getIPList() {
        if (ipList == null) {
            return Collections.unmodifiableList(new LinkedList<IPEntry>());
        } else {
            return Collections.unmodifiableList(ipList);
        }
    }
}
