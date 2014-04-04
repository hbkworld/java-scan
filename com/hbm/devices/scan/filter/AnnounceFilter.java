package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.messages.*;
import com.hbm.devices.scan.RegisterDeviceEvent;
import com.hbm.devices.scan.UnregisterDeviceEvent;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class AnnounceFilter extends Observable implements Observer {

	private HashMap<AnnouncePath, AnnounceTimerTask> deviceMap;
	private Timer timer;

	public AnnounceFilter() {
		deviceMap = new HashMap<AnnouncePath, AnnounceTimerTask>();
		timer = new Timer();
	}

	@Override
	public void update(Observable o, Object arg) {
		AnnouncePath ap = (AnnouncePath)arg;
		Announce announce = ap.getAnnounce();
		synchronized(deviceMap) {
			if (deviceMap.containsKey(ap)) {
				AnnounceTimerTask task = deviceMap.get(ap);
				task.cancel();
				Announce oldAnnounce = task.getAnnouncePath().getAnnounce();
				if (oldAnnounce.equals(announce)) {
					deviceMap.remove(ap);
					task.changeAnnouncePath(ap);
					deviceMap.put(ap, task);
					setChanged();
					notifyObservers(new RegisterDeviceEvent(ap));
				}
				timer.schedule(task, getExpiration(announce));
			} else {
				int expriationMs = getExpiration(announce);
				AnnounceTimerTask task =  new AnnounceTimerTask(ap);
				deviceMap.put(ap, task);
				setChanged();
				notifyObservers(new RegisterDeviceEvent(ap));
				timer.schedule(task, getExpiration(announce));
			}
		}
	}

	private int getExpiration(Announce announce) {
		int expiration = announce.getParams().getExpiration();
		if (expiration == 0) {
			expiration = 6;
		}
		return expiration * 1000;
	}

	class AnnounceTimerTask extends TimerTask {
		private AnnouncePath announcePath;
	
		AnnounceTimerTask(AnnouncePath ap) {
			announcePath = ap;
		}

		@Override
		public void run() {
			this.cancel();
			synchronized(deviceMap) {
				deviceMap.remove(announcePath);
			}
			setChanged();
			notifyObservers(new UnregisterDeviceEvent(announcePath));
		}
	
		void changeAnnouncePath(AnnouncePath ap) {
			announcePath = ap;
		}
	
		AnnouncePath getAnnouncePath() {
			return announcePath;
		}
	}

}

