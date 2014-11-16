import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.configure.ConfigurationService;
import com.hbm.devices.configure.Device;
import com.hbm.devices.configure.Interface;
import com.hbm.devices.configure.NetSettings;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.ConfigureParams;
import com.hbm.devices.scan.messages.Interface.Method;

public class Sender {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private Sender() {
    }

    public static void main(String[] args) {
        try {

            ConfigurationService service = new ConfigurationService();

            Device device = new Device("0009E5001571");
            
            NetSettings settings = new NetSettings(new Interface("eth0", Method.DHCP, null));

            ConfigureParams configParams = new ConfigureParams(device, settings);

            try {
                service.sendConfiguration(configParams, new SimpleCallback(), 5000);
                service.shutdown();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Can't send device configuration!", e);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't create configuration service!", e);
        }
    }
}
