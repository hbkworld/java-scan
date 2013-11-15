import com.hbm.devices.scan.IPv4ScanInterfaces;
import com.hbm.devices.scan.messages.Scan;
import com.hbm.devices.scan.MulticastSender;

import com.google.gson.Gson;

public class Scanner {
	public static void main(String[] args) {
		Gson gson = new Gson();

		Scan scan = new Scan(1);
		String scanJson = gson.toJson(scan);

		try {
			IPv4ScanInterfaces ifs = new IPv4ScanInterfaces();
			MulticastSender mcss = new MulticastSender(ifs.getInterfaces());
			mcss.sendMessage(scanJson);
		} catch (Exception e) {
			System.out.println("got Exception");
			System.out.println(e);
		}
	}
}
