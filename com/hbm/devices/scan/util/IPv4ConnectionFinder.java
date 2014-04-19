package com.hbm.devices.scan.util;

import com.hbm.devices.scan.messages.Announce;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;

class IPv4ConnectionFinder {
	
	public IPv4ConnectionFinder(Announce announce) {
	}

	public InetAddress getConnectableAddress(Collection<NetworkInterface> interfaces) {
		return null;
	}
/*
	public static InetAddress getConnectableIPv4Address(Collection<NetworkInterface> ni, Iterable<IPv4Entry> addresses) {
		Iterator<NetworkInterface> interfaceIterator = ni.iterator();
		while (interfaceIterator.hasNext()) {
			NetworkInterface iface = interfaceIterator.next();
			Iterator<IPv4Entry> addressIterator = addresses.iterator();
			while (addressIterator.hasNext()) {
				IPv4Entry address = addressIterator.next();
				InetAddress connectAddress = getConnectAddress(iface, address);
				if (connectAddress != null) {
					return connectAddress;
				}
			}
		}
		return null;
	}
	
	private static InetAddress getConnectAddress(NetworkInterface ni, IPv4Entry address) {
		InetAddress announceAddress;
		InetAddress announceNetmask;
		try {
			announceAddress = InetAddress.getByName(address.getAddress());
			announceNetmask = InetAddress.getByName(address.getNetmask());
		} catch (UnknownHostException e) {
			return null;
		}

		List<InterfaceAddress> niAddresses = ni.getInterfaceAddresses();
		Iterator<InterfaceAddress> niIterator = niAddresses.iterator();
		while (niIterator.hasNext()) {
			InterfaceAddress niAddress = niIterator.next();
			InetAddress interfaceAddress = niAddress.getAddress();
			if (interfaceAddress instanceof Inet4Address) {
				short interfacePrefix = niAddress.getNetworkPrefixLength();
				short announcePrefix = getPrefix(announceNetmask);
				if (sameNet(announceAddress, announcePrefix, interfaceAddress, interfacePrefix)) {
					return announceAddress;
				}
			}
		}
		return null;
	}
	
	private static short getPrefix(InetAddress announceNetmask) {
		byte[] address = announceNetmask.getAddress();
		int prefix = 0;
		for (int i = 0; i < 4; i++) {
			prefix += Integer.bitCount(address[i] & 0xff);
		}
		return (short)prefix;
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
		int value = ((((int)address[0]) & 0xff) << 24);
		value |= ((((int)address[1]) & 0xff) << 16);
		value |= ((((int)address[2]) & 0xff) << 8);
		value |= ((((int)address[3]) & 0xff) << 0);
		return value;
	}
*/
}
