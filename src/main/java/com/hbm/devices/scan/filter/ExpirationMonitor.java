package com.hbm.devices.scan;

import com.hbm.devices.scan.AnnouncePath;
import com.hbm.devices.scan.LostDeviceEvent;
import com.hbm.devices.scan.messages.*;
import com.hbm.devices.scan.NewDeviceEvent;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ExpirationMonitor extends Observable implements Observer {

	private HashMap<AnnouncePath, ScheduledFuture> deviceMap;
	private HashMap<ScheduledFuture, AnnounceTimerTask> futureMap;
	private ScheduledThreadPoolExecutor executor;

	public ExpirationMonitor() {
		deviceMap = new HashMap<AnnouncePath, ScheduledFuture>(100);
		futureMap = new HashMap<ScheduledFuture, AnnounceTimerTask>(100);
		executor = new ScheduledThreadPoolExecutor(1);
	}

	public void stop() {
		executor.shutdownNow();
		try {
			executor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}
	}

	@Override
	public void update(Observable o, Object arg) {
		AnnouncePath ap = (AnnouncePath)arg;
		Announce announce = ap.getAnnounce();

		synchronized(deviceMap) {
			if (deviceMap.containsKey(ap)) {
				ScheduledFuture sf = deviceMap.get(ap);
				sf.cancel(false);
				deviceMap.remove(ap);
				AnnounceTimerTask task = futureMap.remove(sf);
				Announce oldAnnounce = task.getAnnouncePath().getAnnounce();
				if (!oldAnnounce.equals(announce)) {
					setChanged();
					notifyObservers(new NewDeviceEvent(ap));
				}
				sf = executor.schedule(task, getExpiration(announce), TimeUnit.MILLISECONDS);
				deviceMap.put(ap, sf);
				futureMap.put(sf, task);
			} else {
				int expriationMs = getExpiration(announce);
				AnnounceTimerTask task = new AnnounceTimerTask(ap);
				ScheduledFuture sf = executor.schedule(task, getExpiration(announce), TimeUnit.MILLISECONDS);
				deviceMap.put(ap, sf);
				futureMap.put(sf, task);
				setChanged();
				notifyObservers(new NewDeviceEvent(ap));
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

	class AnnounceTimerTask implements Runnable {
		private AnnouncePath announcePath;
	
		AnnounceTimerTask(AnnouncePath ap) {
			announcePath = ap;
		}

		@Override
		public void run() {
			synchronized(deviceMap) {
				deviceMap.remove(announcePath);
			}
			setChanged();
			notifyObservers(new LostDeviceEvent(announcePath));
		}
	
		AnnouncePath getAnnouncePath() {
			return announcePath;
		}
	}

}

