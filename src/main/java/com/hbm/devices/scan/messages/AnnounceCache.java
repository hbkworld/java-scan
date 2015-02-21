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

package com.hbm.devices.scan.messages;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * This class caches parsed Announce messages to avoid unnecessary JSON
 * parsing.
 *
 * To avoid infinite growth of the cache, the cache is realized as an
 * LRU (least recently used) cache. So old announce messages will be
 * removed if the capacity of the cache exceeds.
 *
 * Parsed JSON messages are stored in parsedMessages.
 * 
 * There is a second cache called lastDeviceAnnounce. This cache is used to
 * check if a device already announced messages previously. This
 * information is used to remove no longer valid announces as soon as
 * possible from parsedMessages.
 */
final class AnnounceCache {

    private static final int DEFAULT_CACHE_SIZE = 100;

    private final LRUCache<String, Announce> parsedMessages;
    private final LRUCache<CommunicationPath, String> lastDeviceAnnounce;

    AnnounceCache() {
        this(DEFAULT_CACHE_SIZE);
    }

    AnnounceCache(int cacheSize) {
        parsedMessages = new LRUCache<String, Announce>(cacheSize);
        lastDeviceAnnounce = new LRUCache<CommunicationPath, String>(cacheSize);
    }

    Announce get(String string) {
        return parsedMessages.get(string);
    }

    int size() {
        return parsedMessages.size();
    }

    int lastAnnounceSize() {
        return lastDeviceAnnounce.size();
    }

    void put(String announceString, CommunicationPath comPath) {
        if (comPath.getAnnounce().equals(parsedMessages.get(announceString))) {
            return;
        }

        if (lastDeviceAnnounce.containsKey(comPath)) {
            // device has send an announce earlier, but it has changed its announce content (e.g.
            // its running services changed)
            final String lastAnnounceString = lastDeviceAnnounce.get(comPath);
            parsedMessages.remove(lastAnnounceString);
            parsedMessages.put(announceString, comPath.getAnnounce());
            lastDeviceAnnounce.put(comPath, announceString);
        } else {
            // the device has not sent an announce message earlier
            lastDeviceAnnounce.put(comPath, announceString);
            parsedMessages.put(announceString, comPath.getAnnounce());
        }
    }
}

final class LRUCache<K, V> extends LinkedHashMap<K, V> {

    /**
     * By default the cache size is 100.
     */
    private static final float LOAD_FACTOR = 0.75f;

    private final int maxSize;

    LRUCache(int maxSize) {
        super(maxSize + 1, LOAD_FACTOR, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
