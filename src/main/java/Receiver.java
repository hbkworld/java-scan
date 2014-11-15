import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.AnnounceReceiver;
import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.DeviceMonitor;
import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.MessageParser;
import com.hbm.devices.scan.MessageReceiver;
import com.hbm.devices.scan.events.LostDeviceEvent;
import com.hbm.devices.scan.events.NewDeviceEvent;
import com.hbm.devices.scan.events.UpdateDeviceEvent;
import com.hbm.devices.scan.filter.FamilytypeMatch;
import com.hbm.devices.scan.filter.Filter;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.IPv6Entry;
import com.hbm.devices.scan.messages.ServiceEntry;
import com.hbm.devices.scan.util.ConnectionFinder;
import com.hbm.devices.scan.util.ScanInterfaces;

public class Receiver implements Observer {
	public static void main(String[] args) {
		try {
			/*
			 * InetAddress v4 = InetAddress.getByName("85.214.228.118"); System.out.println("v4: " +
			 * v4.getCanonicalHostName()); InetAddress v6 =
			 * InetAddress.getByName("2a01:238:43f7:d600:42ba:27f8:ca8b:96ed");
			 * System.out.println("v6: " + v6.getCanonicalHostName());
			 */
			MessageReceiver ar;
			if ((args.length > 0) && (args[0].compareTo("fake") == 0)) {
				ar = new FakeMessageReceiver();
			} else {
				ar = new AnnounceReceiver();
			}
			MessageParser jf = new MessageParser();
			ar.addObserver(jf);

			String[] families = { "QuantumX" };
			Filter ftFilter = new Filter(new FamilytypeMatch(families));
			jf.addObserver(ftFilter);

			// String[] uuids = { "0009E5001571" };
			// Filter uuidFilter = new Filter(new UUIDMatch(uuids));
			// ftFilter.addObserver(uuidFilter);

			DeviceMonitor af = new DeviceMonitor();
			ftFilter.addObserver(af);
			// uuidFilter.addObserver(af);

			Receiver r = new Receiver();
			af.addObserver(r);
			ar.start();
			synchronized (args) {
				args.wait();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private Collection<NetworkInterface> scanInterfaces;
	private ConnectionFinder connectionFinder;

	public Receiver() throws SocketException {
		scanInterfaces = new ScanInterfaces().getInterfaces();
		connectionFinder = new ConnectionFinder(scanInterfaces, false);
	}

	public void update(Observable o, Object arg) {
		CommunicationPath ap;
		if (arg instanceof NewDeviceEvent) {
			ap = ((NewDeviceEvent) arg).getAnnouncePath();
			Announce a = ap.getAnnounce();
			InetAddress connectAddress = connectionFinder.getConnectableAddress(a);
			System.out.println("New Device:");
			if (connectAddress != null) {
				System.out.println("Connectable: " + connectAddress);
			}
		} else if (arg instanceof LostDeviceEvent) {
			ap = ((LostDeviceEvent) arg).getAnnouncePath();
			System.out.println("Lost Device:");
		} else if (arg instanceof UpdateDeviceEvent) {
			UpdateDeviceEvent event = (UpdateDeviceEvent) arg;
			ap = event.getNewCommunicationPath();
			System.out.println("Update Device:");
		} else {
			System.out.println("unknown");
			return;
		}

		Announce a = ap.getAnnounce();
		System.out.print(a.getParams().getDevice());

		System.out.println("\tIP-Addresses:");
		System.out.println("\t interfaceName: "
				+ a.getParams().getNetSettings().getInterface().getName());
		System.out.println("\t method:"
				+ a.getParams().getNetSettings().getInterface().getConfigurationMethod());
		Iterable<?> ipv4 = (Iterable<?>) a.getParams().getNetSettings().getInterface().getIPv4();
		Iterable<IPv6Entry> ipv6 = a.getParams().getNetSettings().getInterface().getIPv6();
		if (ipv4 != null) {
			for (Object entry : ipv4) {
				System.out.print("\t " + entry + "\n");
			}
		}
		if (ipv6 != null) {
			for (IPv6Entry e : ipv6) {
				System.out.println("\t " + e);
			}
		}

		System.out.println("\tServices:");
		Iterable<ServiceEntry> services = a.getParams().getServices();
		for (ServiceEntry entry : services) {
			System.out.print("\t " + entry + "\n");
		}
		System.out.println();
	}
}
