package com.hbm.devices.scan;

public class RegisterDeviceEvent {

	private AnnouncePath announcePath;

	public RegisterDeviceEvent(AnnouncePath ap) {
		announcePath = ap;
	}
	
	public AnnouncePath getAnnouncePath() {
		return announcePath;
	}
}
