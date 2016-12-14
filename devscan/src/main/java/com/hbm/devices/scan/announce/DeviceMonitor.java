/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Hottinger Baldwin Messtechnik GmbH
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

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.ScanConstants;

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
public final class DeviceMonitor extends Observable implements Observer, Closeable {

    private final Map<String, TimerContainer> deviceMap;
    private final ScheduledThreadPoolExecutor executor;
    private boolean stopped;

    private static final Logger LOGGER = 
        Logger.getLogger(ScanConstants.LOGGER_NAME);
    private static final int INITIAL_ENTRIES = 100;

    /**
     * Constructs a new {@code DeviceMonitor} object.
     *
     * @since 1.0
     */
    public DeviceMonitor() {
        super();
        deviceMap = new HashMap<>(INITIAL_ENTRIES);
        executor = new ScheduledThreadPoolExecutor(1);
        stopped = false;
    }

    /**
     * Stops the {@code DeviceMonitor}.
     *
     * There is no guarantee that connected {@link Observer}s will be
     * notified. 
     *
     * @since 1.0
     */
    @Override
    public void close() {
        stopped = true;
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

    public boolean isClosed() {
        return stopped;
    }

    @Override
    public void update(Observable observable, Object arg) {
        final Announce announce = (Announce)arg;
        if (!stopped) {
            armTimer(announce);
        }
    }

    private void armTimer(Announce announce) {
        synchronized (deviceMap) {
            final String path = announce.getPath();
            TimerContainer container = deviceMap.get(path);
            try {
                if (container == null) {
                    final AnnounceTimerTask task = new AnnounceTimerTask(announce);
                    final ScheduledFuture<Void> newFuture = executor.schedule(task, getExpiration(announce),
                            TimeUnit.MILLISECONDS);
                    container = new TimerContainer(task, newFuture);
                    deviceMap.put(path, container);
                    setChanged();
                    notifyObservers(new NewDeviceEvent(announce));
                } else {
                    container.future.cancel(false);
                    final Announce oldAnnounce = container.task.getAnnounce();
                    container.task.setAnnounce(announce);
                    final ScheduledFuture<Void> future =
                        executor.schedule(container.task, getExpiration(announce), TimeUnit.MILLISECONDS);
                    container.future = future;
                    if (!oldAnnounce.equals(announce)) {
                        setChanged();
                        notifyObservers(new UpdateDeviceEvent(oldAnnounce, announce));
                    }
                }
            } catch (RejectedExecutionException e) {
                /*
                 * There is no error handling necessary in this case.
                 * If work is scheduled when the executor was shutdown,
                 * we just ignore that issue and go ahead.
                 */
                LOGGER.log(Level.WARNING, "Task scheduled in shutdown executor!", e);
            }
        }
    }

    private static long getExpiration(Announce announce) {
        int expiration;
        expiration = announce.getParams().getExpiration();
        return TimeUnit.SECONDS.toMillis(expiration);
    }

    private class TimerContainer {
        private final AnnounceTimerTask task;
        private ScheduledFuture<Void> future;

        TimerContainer(AnnounceTimerTask task, ScheduledFuture<Void> future) {
            this.task = task;
            this.future = future;
        }
    }

    private class AnnounceTimerTask implements Callable<Void> {
        private Announce announce;

        AnnounceTimerTask(Announce announce) {
            this.announce = announce;
        }

        @Override
        public Void call() throws Exception {
            synchronized (deviceMap) {
                deviceMap.remove(announce.getPath());
                setChanged();
                notifyObservers(new LostDeviceEvent(announce));
                return null;
            }
        }

        Announce getAnnounce() {
            return announce;
        }

        void setAnnounce(Announce announce) {
            this.announce = announce;
        }
    }
}
