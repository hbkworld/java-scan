package com.hbm.devices.scan;

import com.hbm.devices.scan.util.IPv4ScanInterfaces;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;

public class StringMessageMulticastReceiver extends Observable {

	private InetAddress multicastIP;
	private int port;
	private boolean shallRun = true;
	private MulticastSocket socket;

	public StringMessageMulticastReceiver(String multicastIP, int port) throws UnknownHostException, SocketException, IOException {
		this(InetAddress.getByName(multicastIP), port);
	}

	public StringMessageMulticastReceiver(InetAddress multicastIP, int port) throws SocketException, IOException {
		this.multicastIP = multicastIP;
		this.port = port;
		this.socket = setupMulticastSocket();
	}

	public void start() {
		byte[] buffer = new byte[65536];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (shallRun) {
			try {
				socket.receive(packet);
				String s = new String(buffer, 0, packet.getLength());
				setChanged();
				notifyObservers(s);
			} catch (IOException e) {
				/* No error handling by intention. Receiving announce
				 * datagrams is a best effort service. so don't bother
				 * users of the class with error handling.
				 *
				 * Just try receiving the next datagram.
				 */
			}
		}
	}

	public void stop() {
		shallRun = false;
		try {
			leaveOnAllInterfaces(socket);
			socket.close();
		} catch (IOException e) {
			/* No error handling by intention. Stopping to receive
			 * datagrams is best effort.
			 */ 
		}
	}

	private MulticastSocket setupMulticastSocket() throws SocketException, IOException {
		InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
		MulticastSocket s = new MulticastSocket(sa);
		s.setReuseAddress(true);
		joinOnAllInterfaces(s);
		return s;
	}

	private void joinOnAllInterfaces(MulticastSocket s) throws SocketException, IOException {
		Collection<NetworkInterface> interfaces = new IPv4ScanInterfaces().getInterfaces();
		Iterator<NetworkInterface> niIterator = interfaces.iterator();
		InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
		while (niIterator.hasNext()) {
			NetworkInterface ni = niIterator.next();
			s.joinGroup(sa, ni);
		}
	}

	private void leaveOnAllInterfaces(MulticastSocket s) throws IOException {
		Collection<NetworkInterface> interfaces = new IPv4ScanInterfaces().getInterfaces();
		Iterator<NetworkInterface> niIterator = interfaces.iterator();
		InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
		while (niIterator.hasNext()) {
			NetworkInterface ni = niIterator.next();
			s.leaveGroup(sa, ni);
		}
	}
}

