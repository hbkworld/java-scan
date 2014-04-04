package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.messages.Announce;

public class FamilytypeMatch implements Matcher {

	private String familytype;

	public FamilytypeMatch(String f) {
		this.familytype = f;
	}

	public boolean match(Announce a) {
		return a.getParams().getDevice().getFamilyType().equals(familytype);
	}
}

