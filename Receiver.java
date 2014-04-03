//import com.hbm.devices.scan.MulticastReceiver;
import com.hbm.devices.scan.StringMessageMulticastReceiver;
import com.hbm.devices.scan.IPv4ScanInterfaces;
import com.hbm.devices.scan.messages.JsonRpc;
import com.hbm.devices.scan.ScanConstants;

import java.util.Observable;
import java.util.Observer;
import java.io.IOException;

public class Receiver implements Observer {
	public static void main(String[] args) {
		try {
			StringMessageMulticastReceiver smr = new StringMessageMulticastReceiver(ScanConstants.SCAN_ADDRESS, ScanConstants.SCAN_PORT);
			smr.start();
			synchronized(args) {
				args.wait();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}


	public void update(Observable o, Object arg) {
		JsonRpc packet = (JsonRpc)arg;
		System.out.println(packet);
	}
}
