package com.hbm.devices.scan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;

public class MulticastSender {

	public MulticastSender(Collection<NetworkInterface> ifs) throws IOException {
		socket = new MulticastSocket(ScanConstants.SCAN_PORT);
		multicastSender = new HashSet<NetworkInterface>();
		multicastSender.addAll(ifs);
	}

	public void sendMessage(String msg) throws IOException, SocketException {
		Iterator<NetworkInterface> it = multicastSender.iterator();
		while (it.hasNext()) {
			NetworkInterface iface = it.next();
			socket.setNetworkInterface(iface);
			DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(),
				InetAddress.getByName(ScanConstants.SCAN_ADDRESS), ScanConstants.SCAN_PORT);
			socket.send(packet);
		}
	}

	protected void finalize() throws Throwable {
		socket.close();
		super.finalize();
	}
	
	private MulticastSocket socket;
	private HashSet<NetworkInterface> multicastSender;
}
