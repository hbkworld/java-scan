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

package com.hbm.devices.scan.announce;

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

import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.MissingDataException;

/**
 * This class provides the concept of posting new/lost device events.
 * <p>

 * The class gets{@link Announce} objects via the {@link
 * Observer#update} method and notifies a {@link NewDeviceEvent} if the
 * {@link Announce} object wasn't known upto now.
 * Furthermore, it notifies a {@link LostDeviceEvent} if no new {@link
 * Announce} object was received during the expiration period
 * of the enclosed {@link Announce} object.
 *
 * @since 1.0
 */
public final class DeviceMonitor extends Observable implements Observer {

    private final Map<String, ScheduledFuture<Void>> deviceMap;
    private final Map<ScheduledFuture<Void>, AnnounceTimerTask> futureMap;
    private final ScheduledThreadPoolExecutor executor;
    private static final Logger LOGGER = 
        Logger.getLogger(ScanConstants.LOGGER_NAME);
    private static final int INITIAL_ENTRIES = 100;
    private static final int DEFAULT_EXPIRATION_S = 6;

    /**
     * Constructs a new {@code DeviceMonitor} object.
     *
     * @since 1.0
     */
    public DeviceMonitor() {
        super();
        deviceMap = new HashMap<String, ScheduledFuture<Void>>(INITIAL_ENTRIES);
        futureMap = new HashMap<ScheduledFuture<Void>, AnnounceTimerTask>(INITIAL_ENTRIES);
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
    public void update(Observable observable, Object arg) {
        final Announce announce = (Announce)arg;
        try {
            synchronized (deviceMap) {
                String path = announce.getPath();
                ScheduledFuture<Void> future = deviceMap.get(path);
                if (future != null) {
                    future.cancel(false);
                    deviceMap.remove(path);
                    AnnounceTimerTask task = futureMap.remove(future);
                    final Announce oldAnnounce = task.getAnnounce();
                    task = new AnnounceTimerTask(announce);
                    future = executor.schedule(task, getExpiration(announce), TimeUnit.MILLISECONDS);
                    deviceMap.put(path, future);
                    futureMap.put(future, task);

                    if (!oldAnnounce.equals(announce)) {
                        setChanged();
                        notifyObservers(new UpdateDeviceEvent(oldAnnounce, announce));
                    }
                } else {
                    final AnnounceTimerTask task = new AnnounceTimerTask(announce);
                    final ScheduledFuture<Void> newFuture = executor.schedule(task, getExpiration(announce),
                            TimeUnit.MILLISECONDS);
                    deviceMap.put(path, newFuture);
                    futureMap.put(newFuture, task);
                    setChanged();
                    notifyObservers(new NewDeviceEvent(announce));
                }
            }
        } catch (MissingDataException e) {
            LOGGER.log(Level.INFO, "Some information is missing in JSON!", e);
        }
    }

    private long getExpiration(Announce announce) throws MissingDataException {
        int expiration;
        expiration = announce.getParams().getExpiration();
        if (expiration == 0) {
            expiration = DEFAULT_EXPIRATION_S;
        }
        return TimeUnit.SECONDS.toMillis(expiration);
    }

    private class AnnounceTimerTask implements Callable<Void> {
        private final Announce announce;

        AnnounceTimerTask(Announce announce) {
            this.announce = announce;
        }

        @Override
        public Void call() throws Exception {
            synchronized (deviceMap) {
                deviceMap.remove(announce.getPath());
            }
            setChanged();
            notifyObservers(new LostDeviceEvent(announce));
            return null;
        }

        Announce getAnnounce() {
            return announce;
        }
    }
}
