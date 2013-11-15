import com.hbm.devices.scan.MulticastReceiver;
import com.hbm.devices.scan.IPv4ScanInterfaces;
import com.hbm.devices.scan.messages.JsonRpc;

import java.util.Observable;
import java.util.Observer;

public class Receiver implements Observer {
	public static void main(String[] args) {
		Receiver r = new Receiver();
		try {
			IPv4ScanInterfaces ifs = new IPv4ScanInterfaces();
			MulticastReceiver mrs = new MulticastReceiver(ifs.getInterfaces());
			mrs.addObserver(r);
			Thread t = new Thread(mrs);
			t.start();
			synchronized(args) {
				args.wait();
			}
			mrs.stop();
			t.join();
		} catch (Exception e) {
			System.out.println("got Exception");
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public void update(Observable o, Object arg) {
		JsonRpc packet = (JsonRpc)arg;
		System.out.println(packet);
	}
}
