import com.hbm.devices.scan.AnnounceReceiver;
import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.FakeStringMessageMulticastReceiver;
import com.hbm.devices.scan.filter.AnnounceFilter;
import com.hbm.devices.scan.filter.FamilytypeMatch;
import com.hbm.devices.scan.filter.Filter;
import com.hbm.devices.scan.filter.JsonFilter;
import com.hbm.devices.scan.messages.*;
import com.hbm.devices.scan.RegisterDeviceEvent;
import com.hbm.devices.scan.UnregisterDeviceEvent;
import com.hbm.devices.scan.util.ConnectionFinder;
import com.hbm.devices.scan.util.IPv4ScanInterfaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

public class Receiver implements Observer {
	public static void main(String[] args) {
		try {
			/*
			InetAddress v4 = InetAddress.getByName("85.214.228.118");
			System.out.println("v4: " + v4.getCanonicalHostName());
			InetAddress v6 = InetAddress.getByName("2a01:238:43f7:d600:42ba:27f8:ca8b:96ed");
			System.out.println("v6: " + v6.getCanonicalHostName());
			*/
			//AnnounceReceiver ar = new AnnounceReceiver();
			FakeStringMessageMulticastReceiver ar = new FakeStringMessageMulticastReceiver();
			JsonFilter jf = new JsonFilter();
			ar.addObserver(jf);

			Filter ftFilter = new Filter(new FamilytypeMatch("QuantumX"));
			jf.addObserver(ftFilter);
			
			AnnounceFilter af = new AnnounceFilter();
			ftFilter.addObserver(af);
			
			Receiver r = new Receiver();
			af.addObserver(r);
			ar.start();
			synchronized(args) {
				args.wait();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private Collection<NetworkInterface> scanInterfaces;
	private ConnectionFinder connectionFinder;

	public Receiver() throws SocketException {
		scanInterfaces = new IPv4ScanInterfaces().getInterfaces();
		connectionFinder = new ConnectionFinder(scanInterfaces, false);
	}

	public void update(Observable o, Object arg) {
		AnnouncePath ap;
		if (arg instanceof RegisterDeviceEvent) {
			System.out.println("registered: ");
			ap = ((RegisterDeviceEvent)arg).getAnnouncePath();
			Announce a = ap.getAnnounce();
			InetAddress connectAddress = connectionFinder.getConnectableAddress(a);
			if (connectAddress != null) {
				System.out.println("Connectable: " + connectAddress);
			}
		} else if (arg instanceof UnregisterDeviceEvent) {
			System.out.println("unregistered: ");
			ap = ((UnregisterDeviceEvent)arg).getAnnouncePath();
		} else {
			System.out.println("unknown");
			return;
		}
		Announce a = ap.getAnnounce();
		System.out.print(a.getParams().getDevice());
		Iterable<IPv4Entry> ipv4 = a.getParams().getNetSettings().getInterface().getIPv4();
		for (IPv4Entry entry : ipv4) {
			System.out.print(" " + entry);
		}
		Iterable<ServiceEntry> services = a.getParams().getServices();
		for (ServiceEntry entry : services) {
			System.out.print(" " + entry);
		}
		System.out.println();
	}
}
