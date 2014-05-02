package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.MissingDataException;

public class FamilytypeMatch implements Matcher {

	private String familytype;

	public FamilytypeMatch(String f) {
		this.familytype = f;
	}

	public boolean match(Announce a) throws MissingDataException {
		try {
			String ft = a.getParams().getDevice().getFamilyType();
			return ft.equals(familytype);
		} catch (NullPointerException e) {
			throw new MissingDataException("No family type in announce object!");
		}
	}
}

