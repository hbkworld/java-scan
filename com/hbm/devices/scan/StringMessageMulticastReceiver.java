package com.hbm.devices.scan;

import java.nio.channels.MembershipKey;
import java.util.Iterator;
import java.util.Collection;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.net.InetAddress;
import java.util.Observable;

public class StringMessageMulticastReceiver extends Observable {

	private InetAddress multicastIP;
	private int port;
	private boolean shallRun = true;
	private DatagramChannel channel;

	public StringMessageMulticastReceiver(String multicastIP, int port) throws UnknownHostException, SocketException, IOException {
		this(InetAddress.getByName(multicastIP), port);
	}

	public StringMessageMulticastReceiver(InetAddress multicastIP, int port) throws SocketException, IOException {
		this.multicastIP = multicastIP;
		this.port = port;
		this.channel = setupMulticastSocket();
	}

	public void start() throws IOException {
		System.out.println("in start");
		while (shallRun) {
			ByteBuffer buffer = ByteBuffer.allocate(10000);
			SocketAddress addr = channel.receive(buffer);
			buffer.flip();
			System.out.println(Charset.defaultCharset().decode(buffer));
			buffer.rewind();
        	buffer.limit(buffer.capacity());
		}
	}

	public void stop() throws IOException {
		shallRun = false;
		channel.close();
	}

	private DatagramChannel setupMulticastSocket() throws SocketException, IOException {
		InetSocketAddress sa = new InetSocketAddress(multicastIP, port);
		DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
			.setOption(StandardSocketOptions.SO_REUSEADDR, true)
			.bind(sa);
		
		joinOnAllInterfaces(dc);
		System.out.println("after join");
		dc.configureBlocking(true);
		return dc;
	}

	private void joinOnAllInterfaces(DatagramChannel dc) throws SocketException, IOException {
		Collection<NetworkInterface> interfaces = new IPv4ScanInterfaces().getInterfaces();
		Iterator<NetworkInterface> niIterator = interfaces.iterator();
		while (niIterator.hasNext()) {
			NetworkInterface ni = niIterator.next();
			System.out.println("join: " + multicastIP + " " + ni);
			MembershipKey mkey = dc.join(multicastIP, ni);
		}
	}
}

