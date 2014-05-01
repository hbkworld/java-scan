package com.hbm.devices.scan;

public class NewDeviceEvent {

	private AnnouncePath announcePath;

	public NewDeviceEvent(AnnouncePath ap) {
		announcePath = ap;
	}
	
	public AnnouncePath getAnnouncePath() {
		return announcePath;
	}
}
