package com.hbm.devices.scan;

import com.hbm.devices.scan.messages.*;

public class AnnouncePath {

	private Announce announce;
	private int hash;

	public AnnouncePath(Announce announce) throws MissingDataException {
		this.announce = announce;
		StringBuilder sb = new StringBuilder(100);
		try {
			String uuid = announce.getParams().getDevice().getUuid();
			if (uuid == null) {
				throw new MissingDataException();
			}
			sb.append(announce.getParams().getDevice().getUuid());

			Router router = announce.getParams().getRouter();
			if (router != null) {
				String routerUuid = router.getUuid();
				if (routerUuid == null) {
					throw new MissingDataException();
				}
				sb.append(router.getUuid());
			}

			String interfaceName = announce.getParams().getNetSettings().getInterface().getName();
			if (interfaceName == null) {
				throw new MissingDataException();
			}
			sb.append(announce.getParams().getNetSettings().getInterface().getName());
			hash = sb.toString().hashCode();
		} catch (NullPointerException e) {
			throw new MissingDataException();
		}
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		return hash == o.hashCode();
	}
	
	public Announce getAnnounce() {
		return announce;
	}
	
}
