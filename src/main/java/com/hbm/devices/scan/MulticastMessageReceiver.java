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

package com.hbm.devices.scan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.util.ScanInterfaces;

/**
 * This class receives messages from a multicast UDP socket and converts them to a
 * {@link java.lang.String}.
 * <p>
 * All network interfaces that are eligible to receive IPv4 multicast messages (see
 * {@link com.hbm.devices.scan.util.ScanInterfaces}) are joined.
 * <p>
 * Receiving messages is done infinitely when calling {@link #start() start()} method. After calling
 * {@link #stop() stop()}, {@link #start() start()} returns.
 * <p>
 * In addition, via {@link com.hbm.devices.scan.MessageReceiver} this class is also an
 * {@link java.util.Observable}. So objects which are interested in String multicast messages have
 * to implement the {@link java.util.Observer} interface and register themselves to an instance of
 * this class with addObserver().
 *
 * @since 1.0
 */
public class MulticastMessageReceiver extends Observable implements MessageReceiver {

    private InetAddress multicastIP;
    private int port;
    private boolean shallRun = true;
    private MulticastSocket socket;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    /**
     * Creates a {@link MulticastMessageReceiver} for receiving
     * multicast messsages
     *
     * @param multicastIP The multicast IP the {@link MulticastMessageReceiver} will listen to.
     * @param port The port for listening to multicast packets.
     * 
     * @throws IOException if creating the underlying socket fails.
     */
    public MulticastMessageReceiver(String multicastIP, int port) throws IOException {
        this(InetAddress.getByName(multicastIP), port);
    }

    /**
     * Creates a {@link MulticastMessageReceiver} for receiving
     * multicast messsages
     *
     * @param multicastIP The multicast IP the {@link MulticastMessageReceiver} will listen to.
     * @param port The port for listening to multicast packets.
     * 
     * @throws IOException if creating the underlying socket fails.
     */
    public MulticastMessageReceiver(InetAddress multicastIP, int port) throws IOException {
        super();

        this.multicastIP = multicastIP;
        this.port = port;
        this.socket = setupMulticastSocket();
    }

    /**
     * This method starts the listening socket.
     *
     * In an infinite loop this method waits for incoming
     * messages, converts them into strings and forwards them to all observers.
     */
    @Override
    public void start() {
        final byte[] buffer = new byte[65536];
        final Charset charset = Charset.forName("UTF-8");
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (shallRun) {
            try {
                socket.receive(packet);
                final String s = new String(buffer, 0, packet.getLength(), charset);
                setChanged();
                notifyObservers(s);
            } catch (IOException e) {
                /*
                 * No error handling by intention. Receiving announce datagrams is a best effort
                 * service. so don't bother users of the class with error handling.
                 * 
                 * Just try receiving the next datagram.
                 */
                LOGGER.log(Level.INFO, "Error receiving Multicast messages!", e);
            }
        }
    }

    /**
     * This method stops the listening socket and cancels the infinite receiving loop.
     */
    @Override
    public void stop() {
        shallRun = false;
        try {
            leaveOnAllInterfaces(socket);
            socket.close();
        } catch (IOException e) {
            /*
             * No error handling by intention. Stopping to receive datagrams is best effort.
             */
            LOGGER.log(Level.INFO, "Can't close multicast socket!", e);
        }
    }

    private MulticastSocket setupMulticastSocket() throws IOException {
        final InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
        final MulticastSocket s = new MulticastSocket(null);
        s.setReuseAddress(true);
        s.bind(sa);

        joinOnAllInterfaces(s);
        return s;
    }

    private void joinOnAllInterfaces(MulticastSocket s) throws IOException {
        final InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
        final Collection<NetworkInterface> interfaces = new ScanInterfaces().getInterfaces();
        for (final NetworkInterface ni : interfaces) {
            s.joinGroup(sa, ni);
        }
    }

    private void leaveOnAllInterfaces(MulticastSocket s) throws IOException {
        final Collection<NetworkInterface> interfaces = new ScanInterfaces().getInterfaces();
        final InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
        for (final NetworkInterface ni : interfaces) {
            s.leaveGroup(sa, ni);
        }
    }
}
