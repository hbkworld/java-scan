import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.DeviceMonitor;
import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.MessageParser;
import com.hbm.devices.scan.events.NewDeviceEvent;
import com.hbm.devices.scan.events.UpdateDeviceEvent;

public class DeviceMonitorTest {

    private FakeMessageReceiver fsmmr;

    private boolean newDevice;
    private boolean updateDevice;

    @Before
    public void setup() {
        this.newDevice = false;
        this.updateDevice = false;
        fsmmr = new FakeMessageReceiver();
        MessageParser jf = new MessageParser();
        fsmmr.addObserver(jf);
        DeviceMonitor af = new DeviceMonitor();
        jf.addObserver(af);
        af.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof NewDeviceEvent) {
                    newDevice = true;
                } else if (arg instanceof UpdateDeviceEvent) {
                    updateDevice = true;
                }
            }
        });
    }

    @Test
    public void NewDeviceEvent() {
        fsmmr.emitSingleCorrectMessage();
        assertTrue(newDevice);
        assertFalse(updateDevice);
    }

    @Test
    public void UpdateDeviceEvent() {
        fsmmr.emitSingleCorrectMessage();
        assertTrue(newDevice);
        newDevice = false;

        fsmmr.emitSingleCorrentMessageDifferentIP();
        assertTrue(updateDevice && !newDevice);
        updateDevice = false;

        // Check if the event is not fired again
        fsmmr.emitSingleCorrentMessageDifferentIP();
        assertFalse(updateDevice || newDevice);
    }

}
