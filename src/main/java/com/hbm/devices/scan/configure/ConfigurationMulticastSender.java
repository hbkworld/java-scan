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

package com.hbm.devices.scan.configure;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;

import com.hbm.devices.scan.ScanConstants;

/**
 * This class receives {@link java.lang.String} messages and sends them via multicast UDP sockets
 * <p>
 * All network interfaces that are eligible to handle multicast messages (see
 * {@link com.hbm.devices.scan.util.ScanInterfaces}) are joined.
 * <p>
 * 
 * @since 1.0
 */
public final class ConfigurationMulticastSender implements MulticastSender {

    private final MulticastSocket socket;
    private final Collection<NetworkInterface> interfaces;
    private final Charset charset;
    private final InetAddress configureAddress;

    /**
     * Creates a {@link ConfigurationMulticastSender} object for sending
     * configuration messages to a device. 
     *
     * @param ifs A {@link Collection} of {@link NetworkInterface}s over
     * which the multicast messages wild be send if {@link #sendMessage}
     * is called.
     * 
     * @throws IOException if creating the underlying socket fails.
     */
    public ConfigurationMulticastSender(Collection<NetworkInterface> ifs) throws IOException {
        charset = Charset.forName("UTF-8");
        configureAddress = InetAddress.getByName(ScanConstants.CONFIGURATION_ADDRESS);
        socket = new MulticastSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(ScanConstants.CONFIGURATION_PORT));
        
        interfaces = new LinkedList<NetworkInterface>();
        interfaces.addAll(ifs);
    }

    /**
     * Sends a multicast message over the {@link NetworkInterface}s
     * specified in {@link #ConfigurationMulticastSender(Collection)}.
     *
     * @param message The JSON string to be send.
     * @throws IOException if the underlying socket send fails.
     */
    @Override
    public void sendMessage(String message) throws IOException {
        final byte[] bytes = message.getBytes(charset);
        final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, configureAddress,
            ScanConstants.CONFIGURATION_PORT);
        for (final NetworkInterface iface : interfaces) {
            socket.setNetworkInterface(iface);
            socket.send(packet);
        }
    }

    /**
     * Shuts down the multicast sender.
     *
     * The underlying socket is closed and IGMP leave messages are send.
     */
    @Override
    public void shutdown() {
        socket.close();
    }
}
