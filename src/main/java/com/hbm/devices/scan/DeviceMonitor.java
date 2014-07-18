package com.hbm.devices.scan;

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.events.*;
import com.hbm.devices.scan.messages.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * This class provides the concept of posting new/lost device events.
 * <p>
 * The class reads {@link CommunicationPath} objects, looks the up in a
 * private hashmap and notifies a {@link NewDeviceEvent} if the announce
 * describes a devices that wasn't known upto now. Furthermore, it
 * notifies a {@link LostDeviceEvent} if no new announce for a
 * particular device was received during the expiration period.
 *
 * @since 1.0
 */
public class DeviceMonitor extends Observable implements Observer {

    private HashMap<CommunicationPath, ScheduledFuture<Void>> deviceMap;
    private HashMap<ScheduledFuture<Void>, AnnounceTimerTask> futureMap;
    private ScheduledThreadPoolExecutor executor;

    public DeviceMonitor() {
        deviceMap = new HashMap<CommunicationPath, ScheduledFuture<Void>>(100);
        futureMap = new HashMap<ScheduledFuture<Void>, AnnounceTimerTask>(100);
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
        CommunicationPath ap = (CommunicationPath)arg;
        Announce announce = ap.getAnnounce();

        synchronized(deviceMap) {
            if (deviceMap.containsKey(ap)) {
                ScheduledFuture<Void> sf = deviceMap.get(ap);
                sf.cancel(false);
                deviceMap.remove(ap);
                AnnounceTimerTask task = futureMap.remove(sf);
                Announce oldAnnounce = task.getCommunicationPath().getAnnounce();
                if (!oldAnnounce.equals(announce)) {
                    setChanged();
                    notifyObservers(new NewDeviceEvent(ap));
                }
                sf = executor.schedule(task, getExpiration(announce), TimeUnit.MILLISECONDS);
                deviceMap.put(ap, sf);
                futureMap.put(sf, task);
            } else {                
                AnnounceTimerTask task = new AnnounceTimerTask(ap);
                ScheduledFuture<Void> sf = executor.schedule(task, getExpiration(announce), TimeUnit.MILLISECONDS);
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

    class AnnounceTimerTask implements Callable<Void> {
        private CommunicationPath CommunicationPath;
    
        AnnounceTimerTask(CommunicationPath ap) {
            CommunicationPath = ap;
        }

        @Override
        public Void call() throws Exception {
            synchronized(deviceMap) {
                deviceMap.remove(CommunicationPath);
            }
            setChanged();
            notifyObservers(new LostDeviceEvent(CommunicationPath));
            return null;
        }
    
        CommunicationPath getCommunicationPath() {
            return CommunicationPath;
        }
    }
}

