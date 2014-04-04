package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.messages.Announce;
import java.util.Observable;
import java.util.Observer;

public class Filter extends Observable implements Observer {
	
	private Matcher matcher;

	public Filter(Matcher m) {
		this.matcher = m;
	}

	@Override
	public void update(Observable o, Object arg) {
		AnnouncePath ap = (AnnouncePath)arg;
		Announce announce = ap.getAnnounce();
		if (matcher.match(announce)) {
			setChanged();
			notifyObservers(ap);
		}
	}
}

