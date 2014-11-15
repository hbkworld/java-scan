package com.hbm.devices.scan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.configure.Noticeable;

/**
 * This class receives {@link java.lang.String} messages and sends them via multicast UDP sockets
 * <p>
 * All network interfaces that are eligible to receive IPv4 multicast messages (see
 * {@link com.hbm.devices.scan.util.ScanInterfaces}) are joined.
 * <p>
 * This class is an observer, so an {@link java.util.Observable} has to register an instance of this
 * class with addObserver(), in order to send a message.
 * 
 * @since 1.0
 */
public class MulticastSender implements Observer {

	private MulticastSocket socket;
	private Collection<NetworkInterface> multicastSender;
	private Charset charset;
	private InetAddress configureAddress;

	private Noticeable noticeable;

	public static final String CONFIGURATION_ADDRESS = "239.255.77.77";
	public static final int CONFIGURATION_PORT = 31417;

	public MulticastSender(Collection<NetworkInterface> ifs, Noticeable noticeable) throws IOException {
		charset = Charset.forName("UTF-8");
		configureAddress = InetAddress.getByName(CONFIGURATION_ADDRESS);
		socket = new MulticastSocket(null);
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(CONFIGURATION_PORT));
		
		multicastSender = new LinkedList<NetworkInterface>();
		multicastSender.addAll(ifs);
		this.noticeable = noticeable;
		// TODO: join all interfaces
	}

	public void sendMessage(String msg) throws IOException {
		byte[] bytes = msg.getBytes(charset);
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, configureAddress,
				CONFIGURATION_PORT);
		for (NetworkInterface iface : multicastSender) {
			socket.setNetworkInterface(iface);
			socket.send(packet);
		}
	}

	protected void finalize() throws Throwable {
		socket.close();
		super.finalize();
	}

	/**
	 * This method receives a String and transmits it via the multicast socket
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			try {
				this.sendMessage((String) arg);
			} catch (Exception e) {
				if (this.noticeable != null) {
					this.noticeable.onException(e);
				}
			}
		}
	}
}
