package com.hbm.devices.scan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;

public class MulticastSender {
	
	private MulticastSocket socket;
	private HashSet<NetworkInterface> multicastSender;
	private Charset charset;
	private InetAddress configureAddress;

	public MulticastSender(Collection<NetworkInterface> ifs) throws IOException {
		charset = Charset.forName("UTF-8");
		configureAddress = InetAddress.getByName(ScanConstants.SCAN_ADDRESS);
		socket = new MulticastSocket(ScanConstants.SCAN_PORT);
		multicastSender = new HashSet<NetworkInterface>();
		multicastSender.addAll(ifs);
		// join all interfaces
	}

	public void sendMessage(String msg) throws IOException, SocketException {
		byte[] bytes = msg.getBytes(charset);
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length,	configureAddress, ScanConstants.SCAN_PORT);
		for (NetworkInterface iface : multicastSender) { 
			socket.setNetworkInterface(iface);
			socket.send(packet);
		}
	}

	protected void finalize() throws Throwable {
		socket.close();
		super.finalize();
	}
}
