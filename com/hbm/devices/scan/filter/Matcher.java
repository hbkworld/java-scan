package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.MissingDataException;

public interface Matcher {
	boolean match(Announce a) throws MissingDataException;
}
