/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
 */

package com.hbm.devices.scan.messages;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnnounceCache {

    private LRUCache<String, Announce> parsedMessages;

    private LRUCache<Integer, String> availablePaths;

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
            String lastAnnounceString = availablePaths.get(comPath.hashCode());
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

    private static final long serialVersionUID = -1131809227943539842L;

    /**
     * By default the cache size is 100.
     */
    private static final int DEFAULT_CACHE_SIZE = 100;

    private int maxSize;

    LRUCache() {
        this(DEFAULT_CACHE_SIZE);
    }

    LRUCache(int maxSize) {
        super(maxSize + 1, .75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
