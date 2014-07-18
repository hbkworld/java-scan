package com.hbm.devices.scan;

/**
 * This event is emitted by an {@link ExpirationMonitor} when an annouce
 * messages wasn't refreshed during the expiration time.
 * <p>
 *
 * @since 1.0
 */

public class LostDeviceEvent {

	private AnnouncePath announcePath;

	public LostDeviceEvent(AnnouncePath ap) {
		announcePath = ap;
	}

	public AnnouncePath getAnnouncePath() {
		return announcePath;
	}
}
