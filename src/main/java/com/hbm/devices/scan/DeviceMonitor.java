/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Stephan Gatzka
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hbm.devices.scan;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.events.LostDeviceEvent;
import com.hbm.devices.scan.events.NewDeviceEvent;
import com.hbm.devices.scan.events.UpdateDeviceEvent;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.CommunicationPath;

/**
 * This class provides the concept of posting new/lost device events.
 * <p>

 * The class gets{@link CommunicationPath} objects via the {@link
 * Observer#update} method and notifies a {@link NewDeviceEvent} if the
 * {@link CommunicationPath} object wasn't known upto now.
 * Furthermore, it notifies a {@link LostDeviceEvent} if no new {@link
 * CommunicationPath} object was received during the expiration period
 * of the enclosed {@link Announce} object.
 *
 * @since 1.0
 */
public class DeviceMonitor extends Observable implements Observer {

    private final Map<CommunicationPath, ScheduledFuture<Void>> deviceMap;
    private final Map<ScheduledFuture<Void>, AnnounceTimerTask> futureMap;
    private final ScheduledThreadPoolExecutor executor;
    private static final Logger LOGGER = 
        Logger.getLogger(ScanConstants.LOGGER_NAME);

    /**
     * Constructs a new {@code DeviceMonitor} object.
     *
     * @since 1.0
     */
    public DeviceMonitor() {
        deviceMap = new HashMap<CommunicationPath, ScheduledFuture<Void>>(100);
        futureMap = new HashMap<ScheduledFuture<Void>, AnnounceTimerTask>(100);
        executor = new ScheduledThreadPoolExecutor(1);
    }

    /**
     * Stops the {@code DeviceMonitor}.
     *
     * There is no guarantee that connected {@link Observer}s will be
     * notified.
     *
     * @since 1.0
     */
    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    LOGGER.log(Level.SEVERE, "Interrupted while waiting for termination of timer tasks!\n");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        final CommunicationPath ap = (CommunicationPath) arg;
        final Announce announce = ap.getAnnounce();

        synchronized (deviceMap) {
            if (deviceMap.containsKey(ap)) {
                ScheduledFuture<Void> sf = deviceMap.get(ap);
                sf.cancel(false);
                deviceMap.remove(ap);
                AnnounceTimerTask task = futureMap.remove(sf);
                final Announce oldAnnounce = task.getCommunicationPath().getAnnounce();
                if (!oldAnnounce.equals(announce)) {
                    setChanged();
                    notifyObservers(new UpdateDeviceEvent(task.getCommunicationPath(), ap));
                }
                task = new AnnounceTimerTask(ap);
                sf = executor.schedule(task, getExpiration(announce), TimeUnit.MILLISECONDS);
                deviceMap.put(ap, sf);
                futureMap.put(sf, task);
            } else {
                final AnnounceTimerTask task = new AnnounceTimerTask(ap);
                final ScheduledFuture<Void> sf = executor.schedule(task, getExpiration(announce),
                        TimeUnit.MILLISECONDS);
                deviceMap.put(ap, sf);
                futureMap.put(sf, task);
                setChanged();
                notifyObservers(new NewDeviceEvent(ap));
            }
        }
    }

    private int getExpiration(Announce announce) {
        int expiration;
        expiration = announce.getParams().getExpiration();
        if (expiration == 0) {
            expiration = 6;
        }
        return expiration * 1000;
    }

    private class AnnounceTimerTask implements Callable<Void> {
        private final CommunicationPath communicationPath;

        AnnounceTimerTask(CommunicationPath ap) {
            communicationPath = ap;
        }

        @Override
        public Void call() throws Exception {
            synchronized (deviceMap) {
                deviceMap.remove(communicationPath);
            }
            setChanged();
            notifyObservers(new LostDeviceEvent(communicationPath));
            return null;
        }

        CommunicationPath getCommunicationPath() {
            return communicationPath;
        }
    }
}
