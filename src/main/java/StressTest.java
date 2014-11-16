import java.util.logging.Logger;

import com.hbm.devices.scan.DeviceMonitor;
import com.hbm.devices.scan.MessageParser;
import com.hbm.devices.scan.MessageReceiver;
import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.StressTestMessageReceiver;

public class StressTest {

    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private StressTest() {
    }

    public static void main(String[] args) {
        LOGGER.info("Test without Cache:\n");
        MessageReceiver mr = new StressTestMessageReceiver(53, 50000);
        MessageParser mp = new MessageParser(false);
        mr.addObserver(mp);
        DeviceMonitor dm = new DeviceMonitor();
        mp.addObserver(dm);

        mr.start();

        mr.stop();

        LOGGER.info("\nTest with Cache:\n");
        MessageReceiver mr2 = new StressTestMessageReceiver(53, 50000);
        MessageParser mp2 = new MessageParser(true);
        mr2.addObserver(mp2);
        DeviceMonitor dm2 = new DeviceMonitor();
        mp2.addObserver(dm2);

        mr2.start();

        mr2.stop();
    }

}
