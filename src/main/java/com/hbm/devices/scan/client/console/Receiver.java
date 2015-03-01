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

import java.io.IOException;
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
import com.hbm.devices.scan.messages.Device;
import com.hbm.devices.scan.messages.Interface;
import com.hbm.devices.scan.messages.IPv4Entry;
import com.hbm.devices.scan.messages.IPv6Entry;
import com.hbm.devices.scan.messages.AnnounceDeserializer;
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

    private final EventLogger eventLogger;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private Receiver() throws SocketException {
        eventLogger = new EventLogger();
    }

    /**
     * main method for an executable
     *
     * @param  args An array of command line paramters. Not used in the method.
     */
    public static void main(String... args) {
        final AnnounceDeserializer announceParser = new AnnounceDeserializer();
        try (final AnnounceReceiver announceReceiver = new AnnounceReceiver()) {
            announceReceiver.addObserver(announceParser);

            final String[] families = {"QuantumX"};
            final Filter ftFilter = new Filter(new FamilytypeMatch(families));
            announceParser.addObserver(ftFilter);

            final DeviceMonitor deviceMonitor = new DeviceMonitor();
            ftFilter.addObserver(deviceMonitor);

            final Receiver receiver = new Receiver();
            deviceMonitor.addObserver(receiver);
            announceReceiver.run();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error instantiating announce receiver chain!", e);
        }
    }

    @Override
    public void update(Observable observable, Object arg) {
        eventLogger.logEvent(arg);
    }
}

class EventLogger {

    private final ConnectionFinder connectionFinder;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);
    private static final int INITIAL_BUFFER_SIZE = 200;

    EventLogger() throws SocketException {
        final Collection<NetworkInterface> scanInterfaces = new ScanInterfaces().getInterfaces();
        connectionFinder = new ConnectionFinder(scanInterfaces, false);
    }

    void logEvent(Object event) {
        final StringBuilder logBuilder = new StringBuilder(INITIAL_BUFFER_SIZE);
        try {
            Announce announce;
            if (event instanceof NewDeviceEvent) {
                announce = ((NewDeviceEvent)event).getAnnounce();
                final InetAddress connectAddress = connectionFinder.getConnectableAddress(announce);
                logBuilder.append("New Device:\n");
                if (connectAddress != null) {
                    logBuilder.append("Connectable: ").append(connectAddress).append('\n');
                }
            } else if (event instanceof LostDeviceEvent) {
                announce = ((LostDeviceEvent) event).getAnnounce();
                logBuilder.append("Lost Device:\n");
            } else if (event instanceof UpdateDeviceEvent) {
                final UpdateDeviceEvent updateEvent = (UpdateDeviceEvent) event;
                announce = updateEvent.getNewAnnounce();
                logBuilder.append("Update Device:\n");
            } else {
                logBuilder.append("unknown\n");
                return;
            }
            fillDeviceInformation(announce, logBuilder);
        } catch (MissingDataException e) {
            logBuilder.append("Some data missing in Announce: ").append(e);
        }
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, logBuilder.toString());
        }
    }

    private static void fillDeviceInformation(Announce announce, StringBuilder logBuilder)
        throws MissingDataException {
        logDevice(logBuilder, announce.getParams().getDevice());
        logIpAddresses(logBuilder, announce);
        logServices(logBuilder, announce);
        logBuilder.append('\n');
    }

    private static void logServices(StringBuilder logBuilder, Announce announce) throws MissingDataException {
        final Iterable<ServiceEntry> services = announce.getParams().getServices();
        logBuilder.append("  Services\n");
        for (final ServiceEntry entry : services) {
            logBuilder.append("    ")
                .append(entry.getType())
                .append(": ")
                .append(entry.getPort())
                .append('\n');
        }
    }

    private static void logIpAddresses(StringBuilder logBuilder, Announce announce) throws MissingDataException {
        final Interface iface = announce.getParams().getNetSettings().getInterface();
        logBuilder.append("  IP-Addresses:\n    interfaceName: ")
        .append(iface.getName())
        .append('\n');

        final Iterable<IPv4Entry> ipv4 = iface.getIPv4();
        if (ipv4 != null) {
            for (final IPv4Entry entry : ipv4) {
                logBuilder.append("    ")
                    .append(entry.getAddress())
                    .append('/')
                    .append(entry.getNetmask())
                    .append('\n');
            }
        }

        final Iterable<IPv6Entry> ipv6 = iface.getIPv6();
        if (ipv6 != null) {
            for (final IPv6Entry e : ipv6) {
                logBuilder.append("    ")
                    .append(e.getAddress())
                    .append('/')
                    .append(e.getPrefix())
                    .append('\n');
            }
        }
    }

    private static void logDevice(StringBuilder logBuilder, Device device) throws MissingDataException{
        logBuilder.append("Device:\n  UUID: ")
            .append(device.getUuid())
            .append("\n  name: ")
            .append(device.getName())
            .append("\n  family: ")
            .append(device.getFamilyType())
            .append("\n  type: ")
            .append(device.getType())
            .append("\n  firmware version: ")
            .append(device.getFirmwareVersion())
            .append("\n  is router: ")
            .append(device.isRouter())
            .append('\n');
    }
}
