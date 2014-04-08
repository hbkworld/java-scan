import com.hbm.devices.scan.AnnounceReceiver;
import com.hbm.devices.scan.FakeStringMessageMulticastReceiver;
import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.filter.Filter;
import com.hbm.devices.scan.filter.FamilytypeMatch;
import com.hbm.devices.scan.filter.JsonFilter;
import com.hbm.devices.scan.filter.AnnounceFilter;
import com.hbm.devices.scan.IPv4ScanInterfaces;
import com.hbm.devices.scan.messages.*;
import com.hbm.devices.scan.RegisterDeviceEvent;
import com.hbm.devices.scan.UnregisterDeviceEvent;

import java.util.Observable;
import java.util.Observer;
import java.util.Iterator;
import java.io.IOException;

import java.net.InetAddress;

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


	public void update(Observable o, Object arg) {
		AnnouncePath ap;
		if (arg instanceof RegisterDeviceEvent) {
			System.out.print("registered: ");
			ap = ((RegisterDeviceEvent)arg).getAnnouncePath();
		} else if (arg instanceof UnregisterDeviceEvent) {
			System.out.print("unregistered: ");
			ap = ((UnregisterDeviceEvent)arg).getAnnouncePath();
		} else {
			System.out.println("unknown");
			return;
		}

		Announce a = ap.getAnnounce();
		System.out.print(a.getParams().getDevice().getUuid());
		Iterable<IPv4Entry> ipv4 = a.getParams().getNetSettings().getInterface().getIPv4();
		Iterator<IPv4Entry> iterator = ipv4.iterator();
		while (iterator.hasNext()) {
			System.out.print(" " + iterator.next());
		}
		Iterable<ServiceEntry> sentries = a.getParams().getServices();
		Iterator<ServiceEntry> siterator = sentries.iterator();
		while (siterator.hasNext()) {
			System.out.print(" " + siterator.next());
		}
		System.out.println();
	}
}
