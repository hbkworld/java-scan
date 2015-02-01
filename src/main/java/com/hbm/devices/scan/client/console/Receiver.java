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
public final class Receiver implements Observer {

    private final ConnectionFinder connectionFinder;

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private Receiver() throws SocketException {
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
            final MessageParser messageParser = new MessageParser();
            final AnnounceReceiver announceReceiver = new AnnounceReceiver();
            announceReceiver.addObserver(messageParser);

            final String[] families = { "QuantumX" };
            final Filter ftFilter = new Filter(new FamilytypeMatch(families));
            messageParser.addObserver(ftFilter);

            final DeviceMonitor deviceMonitor = new DeviceMonitor();
            ftFilter.addObserver(deviceMonitor);

            final Receiver receiver = new Receiver();
            deviceMonitor.addObserver(receiver);
            announceReceiver.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error instantiating announce receiver chain!", e);
        }
    }

    @Override
    public void update(Observable observable, Object arg) {
        try {
            CommunicationPath communicationPath;
            if (arg instanceof NewDeviceEvent) {
                communicationPath = ((NewDeviceEvent) arg).getAnnouncePath();
                final Announce announce = communicationPath.getAnnounce();
                final InetAddress connectAddress = connectionFinder.getConnectableAddress(announce);
                LOGGER.info("New Device:\n");
                if (connectAddress != null) {
                    LOGGER.info("Connectable: " + connectAddress + "\n");
                }
            } else if (arg instanceof LostDeviceEvent) {
                communicationPath = ((LostDeviceEvent) arg).getAnnouncePath();
                LOGGER.info("Lost Device:\n");
            } else if (arg instanceof UpdateDeviceEvent) {
                final UpdateDeviceEvent event = (UpdateDeviceEvent) arg;
                communicationPath = event.getNewCommunicationPath();
                LOGGER.info("Update Device:\n");
            } else {
                LOGGER.info("unknown\n");
                return;
            }

            final Announce announce = communicationPath.getAnnounce();
            LOGGER.info(announce.getParams().getDevice().toString());

            LOGGER.info("\tIP-Addresses:\n");
            LOGGER.info("\t interfaceName: "
                    + announce.getParams().getNetSettings().getInterface().getName() + "\n");
            LOGGER.info("\t method:"
                    + announce.getParams().getNetSettings().getInterface().getConfigurationMethod() + "\n");
            final Iterable<?> ipv4 = (Iterable<?>) announce.getParams().getNetSettings().getInterface().getIPv4();
            final Iterable<IPv6Entry> ipv6 = announce.getParams().getNetSettings().getInterface().getIPv6();
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
            final Iterable<ServiceEntry> services = announce.getParams().getServices();
            for (final ServiceEntry entry : services) {
                LOGGER.info("\t " + entry + "\n");
            }
            LOGGER.info("\n");
        } catch (MissingDataException e) {
            LOGGER.info("Some data missing in Announce: " + e);
        }
    }
}
