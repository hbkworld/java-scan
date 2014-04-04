import com.hbm.devices.scan.AnnounceReceiver;
import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.filter.Filter;
import com.hbm.devices.scan.filter.FamilytypeMatch;
import com.hbm.devices.scan.filter.JsonFilter;
import com.hbm.devices.scan.filter.AnnounceFilter;
import com.hbm.devices.scan.IPv4ScanInterfaces;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.RegisterDeviceEvent;
import com.hbm.devices.scan.UnregisterDeviceEvent;

import java.util.Observable;
import java.util.Observer;
import java.io.IOException;

public class Receiver implements Observer {
	public static void main(String[] args) {
		try {
			AnnounceReceiver ar = new AnnounceReceiver();
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
		System.out.println(a.getParams().getDevice().getUuid() + " " + a.getParams().getDevice().getFamilyType());
	}
}
