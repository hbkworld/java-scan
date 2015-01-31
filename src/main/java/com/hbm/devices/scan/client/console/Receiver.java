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

package com.hbm.devices.scan.client.console;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.announce.AnnounceReceiver;
import com.hbm.devices.scan.announce.DeviceMonitor;
import com.hbm.devices.scan.announce.filter.FamilytypeMatch;
import com.hbm.devices.scan.announce.filter.Filter;
import com.hbm.devices.scan.announce.LostDeviceEvent;
import com.hbm.devices.scan.announce.NewDeviceEvent;
import com.hbm.devices.scan.announce.UpdateDeviceEvent;
import com.hbm.devices.scan.MessageReceiver;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.CommunicationPath;
import com.hbm.devices.scan.messages.IPv6Entry;
import com.hbm.devices.scan.messages.MessageParser;
import com.hbm.devices.scan.messages.MissingDataException;
import com.hbm.devices.scan.messages.ServiceEntry;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.util.ConnectionFinder;
import com.hbm.devices.scan.util.ScanInterfaces;

/**
 * Example class to show handling of announce messages.
 * <p>
 *
 * @since 1.0
 */
public class Receiver implements Observer {

    private final ConnectionFinder connectionFinder;

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    /**
     * Constructs a new {@link Receiver} object.
     *
     * @throws SocketException if an I/O error occurs.
     */
    public Receiver() throws SocketException {
        final Collection<NetworkInterface> scanInterfaces = new ScanInterfaces().getInterfaces();
        connectionFinder = new ConnectionFinder(scanInterfaces, false);
    }

    /**
     * main method for an executable
     *
     * @param  args An array of command line paramters. Not used in the method.
     */
    public static void main(String... args) {
        try {
            final MessageParser jf = new MessageParser();
            MessageReceiver mr;
            final AnnounceReceiver ar = new AnnounceReceiver();
            ar.addObserver(jf);
            mr = ar;

            final String[] families = { "QuantumX" };
            final Filter ftFilter = new Filter(new FamilytypeMatch(families));
            jf.addObserver(ftFilter);

            final DeviceMonitor af = new DeviceMonitor();
            ftFilter.addObserver(af);

            final Receiver r = new Receiver();
            af.addObserver(r);
            mr.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error instantiating announce receiver chain!", e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            CommunicationPath ap;
            if (arg instanceof NewDeviceEvent) {
                ap = ((NewDeviceEvent) arg).getAnnouncePath();
                final Announce a = ap.getAnnounce();
                final InetAddress connectAddress = connectionFinder.getConnectableAddress(a);
                LOGGER.info("New Device:\n");
                if (connectAddress != null) {
                    LOGGER.info("Connectable: " + connectAddress + "\n");
                }
            } else if (arg instanceof LostDeviceEvent) {
                ap = ((LostDeviceEvent) arg).getAnnouncePath();
                LOGGER.info("Lost Device:\n");
            } else if (arg instanceof UpdateDeviceEvent) {
                final UpdateDeviceEvent event = (UpdateDeviceEvent) arg;
                ap = event.getNewCommunicationPath();
                LOGGER.info("Update Device:\n");
            } else {
                LOGGER.info("unknown\n");
                return;
            }

            final Announce a = ap.getAnnounce();
            LOGGER.info(a.getParams().getDevice().toString());

            LOGGER.info("\tIP-Addresses:\n");
            LOGGER.info("\t interfaceName: "
                    + a.getParams().getNetSettings().getInterface().getName() + "\n");
            LOGGER.info("\t method:"
                    + a.getParams().getNetSettings().getInterface().getConfigurationMethod() + "\n");
            final Iterable<?> ipv4 = (Iterable<?>) a.getParams().getNetSettings().getInterface().getIPv4();
            final Iterable<IPv6Entry> ipv6 = a.getParams().getNetSettings().getInterface().getIPv6();
            if (ipv4 != null) {
                for (final Object entry : ipv4) {
                    LOGGER.info("\t " + entry + "\n");
                }
            }
            if (ipv6 != null) {
                for (final IPv6Entry e : ipv6) {
                    LOGGER.info("\t " + e + "\n");
                }
            }

            LOGGER.info("\tServices:\n");
            final Iterable<ServiceEntry> services = a.getParams().getServices();
            for (final ServiceEntry entry : services) {
                LOGGER.info("\t " + entry + "\n");
            }
            LOGGER.info("\n");
        } catch (MissingDataException e) {
            LOGGER.info("Some data missing in Announce: " + e);
        }
    }
}
