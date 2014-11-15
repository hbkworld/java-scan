package com.hbm.devices.scan.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class implements a LRUCache.
 * <p>
 * This cache has a fixed maximum number of elements (<code>maxSize</code>). If the cache is full
 * and another entry is added, the LRU (least recently used) entry is dropped.
 * 
 * @since 1.0
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = -1131809227943539842L;

	/**
	 * By default the cache size is 100.
	 */
	public static final int DEFAULT_CACHE_SIZE = 100;

	private int maxSize;

	public LRUCache() {
		this(DEFAULT_CACHE_SIZE);
	}

	public LRUCache(int maxSize) {
		super(maxSize + 1, .75f, true);
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}

}
