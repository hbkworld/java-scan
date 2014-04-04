package com.hbm.devices.scan;

public class UnregisterDeviceEvent {

	private AnnouncePath announcePath;

	public UnregisterDeviceEvent(AnnouncePath ap) {
		announcePath = ap;
	}

	public AnnouncePath getAnnouncePath() {
		return announcePath;
	}
}
