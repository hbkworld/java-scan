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

final class AnnounceCache {

    private final LRUCache<String, Announce> parsedMessages;
    private final LRUCache<Integer, String> availablePaths;

    AnnounceCache() {
        parsedMessages = new LRUCache<String, Announce>();
        availablePaths = new LRUCache<Integer, String>();
    }

    AnnounceCache(int cacheSize) {
        parsedMessages = new LRUCache<String, Announce>(cacheSize);
        availablePaths = new LRUCache<Integer, String>(cacheSize);
    }

    boolean hasStringInCache(String string) {
        return parsedMessages.containsKey(string);
    }

    Announce getAnnounceByString(String string) {
        return parsedMessages.get(string);
    }

    int getCacheAmount() {
        return parsedMessages.size();
    }

    int getPathsAmount() {
        return availablePaths.size();
    }

    void addCommunicationPath(String announceString, CommunicationPath comPath) {
        if (hasStringInCache(announceString) && (parsedMessages.get(announceString).equals(comPath.getAnnounce()))) {
            // don't need to re-add the announce, it is already cached
            return;
        }

        if (availablePaths.containsKey(comPath.hashCode())) {
            // device has send an announce earlier, but it has changed its announce content (e.g.
            // its running services changed)
            final String lastAnnounceString = availablePaths.get(comPath.hashCode());
            parsedMessages.remove(lastAnnounceString);
            parsedMessages.put(announceString, comPath.getAnnounce());
            availablePaths.put(comPath.hashCode(), announceString);
        } else {
            // the device has not sent an announce message earlier
            availablePaths.put(comPath.hashCode(), announceString);
            parsedMessages.put(announceString, comPath.getAnnounce());
        }
    }
}

class LRUCache<K, V> extends LinkedHashMap<K, V> {

    /**
     * By default the cache size is 100.
     */
    private static final int DEFAULT_CACHE_SIZE = 100;
    private static final float LOAD_FACTOR = 0.75f;

    private int maxSize;

    LRUCache() {
        this(DEFAULT_CACHE_SIZE);
    }

    LRUCache(int maxSize) {
        super(maxSize + 1, LOAD_FACTOR, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
