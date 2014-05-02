package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.MissingDataException;
import java.util.Observable;
import java.util.Observer;

/**
 * This class filters {@link AnnouncePath} objects with according to a
 * {@link Matcher} object.
 * <p>
 * The class reads {@link AnnouncePath} objects and notifies them if
 * {@link Matcher#match(Announce)} method returns true.
 * @since 1.0
 */
public class Filter extends Observable implements Observer {
	
	private Matcher matcher;

	public Filter(Matcher m) {
		this.matcher = m;
	}

	@Override
	public void update(Observable o, Object arg) {
		AnnouncePath ap = (AnnouncePath)arg;
		Announce announce = ap.getAnnounce();
		try {
			if (matcher.match(announce)) {
				setChanged();
				notifyObservers(ap);
			}
		} catch (MissingDataException e) {
		}
	}
}

