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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.hbm.devices.scan.messages.LRUCache;

public class LRUCacheTest {

    private LRUCache<Integer, Integer> cache;

    @Before
    public void setUp() {
        this.cache = new LRUCache<Integer, Integer>(3);
    }

    @Test
    public void DropTest() {
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.put(4, 4);

        assertFalse("Eldest element still in cache", cache.containsKey(1));
        assertTrue("New element not in cache", cache.containsKey(2));
        assertTrue("New element not in cache", cache.containsKey(3));
        assertTrue("New element not in cache", cache.containsKey(4));
    }

    @Test
    public void DropEldestTest() {
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.get(1);
        cache.put(4, 4);
        
        assertFalse("Eldest element still in cache", cache.containsKey(2));
        assertTrue("Younger element not in cache", cache.containsKey(1));
        assertTrue("Younger element not in cache", cache.containsKey(3));
        assertTrue("Younger element not in cache", cache.containsKey(4));
    }
}

