package com.hbm.devices.scan;

import com.hbm.devices.scan.messages.*;

/**
 * This class carries parsed announce messages.
 * <p>
 * If it would be possible to retrieve the information which {@link
 * java.net.NetworkInterface} received an announce messages, this will
 * also handled here to provide all information necessary to decide if
 * an IP communication is possible to the announced device.
 * <p>
 * Please note that the {@link #hashCode()} method is overridden. The
 * hash code of this announce object is unique for the communication
 * path the {@link Announce} message traveled.
 *
 * @since 1.0
 */
public class AnnouncePath {

	private Announce announce;
	private int hash;
	public Object cookie;

	AnnouncePath(Announce announce) throws MissingDataException {
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
