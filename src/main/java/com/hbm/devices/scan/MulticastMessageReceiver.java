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

package com.hbm.devices.scan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Collection;
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
public class MulticastMessageReceiver extends MessageReceiver {

    private InetAddress multicastIP;
    private int port;
    private boolean shallRun = true;
    private MulticastSocket socket;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    public MulticastMessageReceiver(String multicastIP, int port) throws IOException {
        this(InetAddress.getByName(multicastIP), port);
    }

    public MulticastMessageReceiver(InetAddress multicastIP, int port) throws IOException {
        this.multicastIP = multicastIP;
        this.port = port;
        this.socket = setupMulticastSocket();
    }

    /**
     * This method starts the listening socket. In an infinite loop this method waits for incoming
     * messages, converts them into strings and forwards them to all observers.
     */
    public void start() {
        byte[] buffer = new byte[65536];
        Charset charset = Charset.forName("UTF-8");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (shallRun) {
            try {
                socket.receive(packet);
                String s = new String(buffer, 0, packet.getLength(), charset);
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
     * This method stops the listening socket and cancels the infinite receiving loop
     */
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
        InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
        MulticastSocket s = new MulticastSocket(null);
        s.setReuseAddress(true);
        s.bind(sa);

        joinOnAllInterfaces(s);
        return s;
    }

    private void joinOnAllInterfaces(MulticastSocket s) throws IOException {
        InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
        Collection<NetworkInterface> interfaces = new ScanInterfaces().getInterfaces();
        for (NetworkInterface ni : interfaces) {
            s.joinGroup(sa, ni);
        }
    }

    private void leaveOnAllInterfaces(MulticastSocket s) throws IOException {
        Collection<NetworkInterface> interfaces = new ScanInterfaces().getInterfaces();
        InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
        for (NetworkInterface ni : interfaces) {
            s.leaveGroup(sa, ni);
        }
    }
}
