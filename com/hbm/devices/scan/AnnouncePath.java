package com.hbm.devices.scan;

import com.hbm.devices.scan.messages.*;

public class AnnouncePath {

	private Announce announce;
	private int hash;

	public AnnouncePath(Announce announce) {
		this.announce = announce;
		StringBuilder sb = new StringBuilder(announce.getParams().getDevice().getUuid());
		Router router = announce.getParams().getRouter();
		if (router != null) {
			sb.append(router.getUuid());
		}
		sb.append(announce.getParams().getNetSettings().getInterface().getName());
		hash = sb.toString().hashCode();
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	public Announce getAnnounce() {
		return announce;
	}
	
}
