package com.hbm.devices.scan.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.IPv4Entry;

class IPv4ConnectionFinder {

    private Iterable<InterfaceAddress> ipv4Addresses;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    public IPv4ConnectionFinder(Collection<NetworkInterface> interfaces) {
        List<InterfaceAddress> addressList = new LinkedList<InterfaceAddress>();

        for (NetworkInterface iface : interfaces) {
            List<InterfaceAddress> niAddresses = iface.getInterfaceAddresses();
            for (InterfaceAddress niAddress : niAddresses) {
                InetAddress interfaceAddress = niAddress.getAddress();
                if (interfaceAddress instanceof Inet4Address) {
                    addressList.add(niAddress);
                }
            }
        }
        ipv4Addresses = addressList;
    }

    public InetAddress getConnectableAddress(Announce announce) {
        for (InterfaceAddress niAddress : ipv4Addresses) {
            InetAddress address = getConnectAddress(niAddress, announce);
            if (address != null) {
                return address;
            }
        }
        return null;
    }

    private static InetAddress getConnectAddress(InterfaceAddress interfaceAddress,
            Announce announce) {
        Iterable<?> announceAddresses = (Iterable<?>) announce.getParams().getNetSettings()
                .getInterface().getIPv4();
        if (announceAddresses == null) {
            return null;
        }

        for (Object ipv4Entry : announceAddresses) {
            try {
                InetAddress announceAddress = InetAddress.getByName(((IPv4Entry) ipv4Entry)
                        .getAddress());
                if (!(announceAddress instanceof Inet4Address)) {
                    continue;
                }
                InetAddress announceNetmask = InetAddress.getByName(((IPv4Entry) ipv4Entry)
                        .getNetmask());
                short announcePrefix = calculatePrefix(announceNetmask);

                InetAddress ifaceAddress = interfaceAddress.getAddress();
                short ifaceAddressPrefix = interfaceAddress.getNetworkPrefixLength();
                if (sameNet(announceAddress, announcePrefix, ifaceAddress, ifaceAddressPrefix)) {
                    return announceAddress;
                }
            } catch (UnknownHostException e) {
                LOGGER.log(Level.INFO, "Can't retrieve InetAddress from IP address!", e);
                continue;
            }
        }

        return null;
    }

    private static short calculatePrefix(InetAddress announceNetmask) {
        byte[] address = announceNetmask.getAddress();
        int prefix = 0;
        for (int i = 0; i < 4; i++) {
            prefix += Integer.bitCount(address[i] & 0xff);
        }
        return (short) prefix;
    }

    private static boolean sameNet(InetAddress announceAddress, short announcePrefix,
            InetAddress interfaceAddress, short interfacePrefix) {
        byte[] announceBytes = announceAddress.getAddress();
        byte[] interfaceBytes = interfaceAddress.getAddress();
        int announceInteger = convertToInteger(announceBytes);
        int interfaceInteger = convertToInteger(interfaceBytes);
        announceInteger = announceInteger >>> (32 - announcePrefix);
        interfaceInteger = interfaceInteger >>> (32 - interfacePrefix);
        return announceInteger == interfaceInteger;
    }

    private static int convertToInteger(byte[] address) {
        int value = ((int)address[0] & 0xff) << 24;
        value |= ((int)address[1] & 0xff) << 16;
        value |= ((int)address[2] & 0xff) << 8;
        value |= (((int) address[3]) & 0xff) << 0;
        return value;
    }
}
