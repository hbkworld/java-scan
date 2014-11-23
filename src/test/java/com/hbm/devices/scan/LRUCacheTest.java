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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.messages.LRUCache;

public class LRUCacheTest {

    private LRUCache<Integer, Integer> cache;

    @Before
    public void setup() {
        this.cache = new LRUCache<Integer, Integer>(3);
    }

    @Test
    public void DropTest() {
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.put(4, 4);

        assertFalse(cache.containsKey(1));
        assertTrue(cache.containsKey(2));
        assertTrue(cache.containsKey(3));
        assertTrue(cache.containsKey(4));
    }

    @Test
    public void DropEldestTest() {
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.get(1);
        cache.put(4, 4);
        
        assertFalse(cache.containsKey(2));
        assertTrue(cache.containsKey(1));
        assertTrue(cache.containsKey(3));
        assertTrue(cache.containsKey(4));
    }

}
