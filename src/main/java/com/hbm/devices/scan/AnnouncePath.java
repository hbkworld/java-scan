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

	/**
	 * Placeholder for user defined object.
	 * <p>
	 * This object carries user defined data, it will not be used from
	 * code inside this package.
	 */
	public Object cookie;

	AnnouncePath(Announce announce) throws MissingDataException {
		this.announce = announce;
		StringBuilder sb = new StringBuilder(100);
		try {
			String deviceUuid = announce.getParams().getDevice().getUuid();
			if (deviceUuid == null) {
				throw new MissingDataException();
			}
			sb.append(deviceUuid);

			Router router = announce.getParams().getRouter();
			if (router != null) {
				String routerUuid = router.getUuid();
				if (routerUuid == null) {
					throw new MissingDataException();
				}
				sb.append(routerUuid);
			}

			String deviceInterfaceName = announce.getParams().getNetSettings().getInterface().getName();
			if (deviceInterfaceName == null) {
				throw new MissingDataException();
			}
			sb.append(deviceInterfaceName);
			hash = sb.toString().hashCode();
		} catch (NullPointerException e) {
			throw new MissingDataException();
		}
	}

	/**
	 * Calculates a unique hash for a communication path.
	 * <p>
	 * Currently the device uuid, the router uuid and the interface name
	 * of the sending device are take into the hash calculation.
	 */
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
