package com.hbm.devices.scan;

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.util.LRUCache;

public class AnnounceCache {

	private LRUCache<String, Announce> parsedMessages;

	private LRUCache<Integer, String> awailablePaths;

	AnnounceCache() {
		this.parsedMessages = new LRUCache<String, Announce>();
		this.awailablePaths = new LRUCache<Integer, String>();
	}

	AnnounceCache(int cacheSize) {
		this.parsedMessages = new LRUCache<String, Announce>(cacheSize);
		this.awailablePaths = new LRUCache<Integer, String>(cacheSize);
	}

	boolean hasStringInCache(String string) {
		return this.parsedMessages.containsKey(string);
	}

	Announce getAnnounceByString(String string) {
		return this.parsedMessages.get(string);
	}

	int getCacheAmount() {
		return parsedMessages.size();
	}

	int getPathsAmount() {
		return awailablePaths.size();
	}

	void addCommunicationPath(String announceString, CommunicationPath comPath) {
		if (this.hasStringInCache(announceString)) {
			if (this.parsedMessages.get(announceString).equals(comPath.getAnnounce())) {
				// don't need to re-add the announce, it is already cached
				return;
			}
		}

		if (awailablePaths.containsKey(comPath.hashCode())) {
			// device has send an announce earlier, but it has changed its announce content (e.g.
			// its running services changed)
			String lastAnnounceString = awailablePaths.get(comPath.hashCode());
			parsedMessages.remove(lastAnnounceString);
			parsedMessages.put(announceString, comPath.getAnnounce());
			awailablePaths.put(comPath.hashCode(), announceString); // update last sent string
		} else {
			// the device has not sent an announce message earlier
			this.awailablePaths.put(comPath.hashCode(), announceString);
			this.parsedMessages.put(announceString, comPath.getAnnounce());
		}
	}
}
