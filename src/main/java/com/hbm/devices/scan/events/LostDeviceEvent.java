package com.hbm.devices.scan.events;

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.DeviceMonitor;

/**
 * This event is emitted by an {@link DeviceMonitor} when an annouce
 * messages wasn't refreshed during the expiration time.
 * <p>
 *
 * @since 1.0
 */

public class LostDeviceEvent {

    private CommunicationPath communicationPath;

    public LostDeviceEvent(CommunicationPath ap) {
        communicationPath = ap;
    }

    public CommunicationPath getAnnouncePath() {
        return communicationPath;
    }
}
