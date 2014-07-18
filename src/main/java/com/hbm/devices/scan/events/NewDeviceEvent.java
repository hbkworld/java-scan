package com.hbm.devices.scan;

/**
 * This event is emitted by an {@link ExpirationMonitor}.
 * <p>
 * The event is notified when an announce method from a device that is
 * unknown upto now is received. In addition, this event is also fired
 * when the device was already announced but some data in the announce
 * message has changed.
 *
 * @since 1.0
 */
public class NewDeviceEvent {

	private AnnouncePath announcePath;

	public NewDeviceEvent(AnnouncePath ap) {
		announcePath = ap;
	}
	
	public AnnouncePath getAnnouncePath() {
		return announcePath;
	}
}
