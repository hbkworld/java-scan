import com.hbm.devices.configure.ConfigurationService;
import com.hbm.devices.configure.Device;
import com.hbm.devices.configure.Interface;
import com.hbm.devices.configure.NetSettings;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Interface.Method;

public class Sender {

	public static void main(String[] args) {
		try {

			ConfigurationService service = new ConfigurationService();

			Device device = new Device("0009E5001571");
			// Device device = new Device("0009E500087B");
			
			// NetSettings settings = new NetSettings(new Interface("eth0", Method.manual,
			// new IPv4EntryManual("172.19.170.53", "255.255.0.0")));
			
			// NetSettings settings = new NetSettings(new Interface("eth0", Method.manual,
			// new IPv4EntryManual("abcd", "255.255.0.0")));
			
			NetSettings settings = new NetSettings(new Interface("eth0", Method.dhcp, null));

			ConfigureParams configParams = new ConfigureParams(device, settings);

			try {
				service.sendConfiguration(configParams, new SimpleCallback(), 5000);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
