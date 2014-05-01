package com.hbm.devices.scan;

public class LostDeviceEvent {

	private AnnouncePath announcePath;

	public LostDeviceEvent(AnnouncePath ap) {
		announcePath = ap;
	}

	public AnnouncePath getAnnouncePath() {
		return announcePath;
	}
}
