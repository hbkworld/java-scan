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

	public static final String CONFIGURATION_ADDRESS = "239.255.77.77";
	public static final int CONFIGURATION_PORT = 31417;

	public MulticastSender(Collection<NetworkInterface> ifs) throws IOException {
		charset = Charset.forName("UTF-8");
		configureAddress = InetAddress.getByName(CONFIGURATION_ADDRESS);
		socket = new MulticastSocket(CONFIGURATION_PORT);
		multicastSender = new HashSet<NetworkInterface>();
		multicastSender.addAll(ifs);
		// join all interfaces
	}

	public void sendMessage(String msg) throws IOException, SocketException {
		byte[] bytes = msg.getBytes(charset);
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length,	configureAddress, CONFIGURATION_PORT);
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
