package com.hbm.devices.scan.events;

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.DeviceMonitor;

/**
 * This event is emitted by an {@link DeviceMonitor}.
 * <p>
 * The event is notified when an announce method from a device that is unknown upto now is received.
 * This event is not fired when the device was already announced but some data in the announce
 * message has changed. In this case {@link UpdateDeviceEvent} is fired.
 *
 * @since 1.0
 */
public class NewDeviceEvent {

    private CommunicationPath communicationPath;

    public NewDeviceEvent(CommunicationPath ap) {
        communicationPath = ap;
    }

    public CommunicationPath getAnnouncePath() {
        return communicationPath;
    }
}
