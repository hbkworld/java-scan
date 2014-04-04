package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.messages.Announce;

public interface Matcher {
	boolean match(Announce a);
}
