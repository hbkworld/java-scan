package com.hbm.devices.scan;

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.util.LRUCache;

/**
 * This class is used to cache incoming Messages.
 * <p>
 * A device usually sends the identical announce every 6 seconds. So this allows to store the
 * incoming messages with their parsed Announce object, so the message does not need to be parsed
 * again.
 * 
 * @since 1.0
 *
 */
public class AnnounceCache {

	private LRUCache<String, Announce> parsedMessages;

	// <hashCode that identifies a CommunicationPath, lastAnnounceString received via the
	// communicationPath>
	private LRUCache<Integer, String> awailablePaths;

	/**
	 * Default constructor to instantiate the cache with a size of 100 entries.
	 */
	public AnnounceCache() {
		this.parsedMessages = new LRUCache<String, Announce>();
		this.awailablePaths = new LRUCache<Integer, String>();
	}

	/**
	 * Constructor to instantiate the cache with a specific size.
	 * 
	 * @param cacheSize
	 *            The amount of entries which can be stored in the cache.
	 */
	public AnnounceCache(int cacheSize) {
		this.parsedMessages = new LRUCache<String, Announce>(cacheSize);
		this.awailablePaths = new LRUCache<Integer, String>(cacheSize);
	}

	/**
	 * Checks if a given String is stored in the cache.
	 * 
	 * @param string
	 *            The string which is checked, if its in the cache
	 * @return Returns if the string is in the cache
	 */
	public boolean hasStringInCache(String string) {
		return this.parsedMessages.containsKey(string);
	}

	/**
	 * Gets the parsed Announce object associated with the announce String
	 * 
	 * @param string
	 * @return Returns the announce associated with the given String
	 */
	public Announce getAnnounceByString(String string) {
		return this.parsedMessages.get(string);
	}

	/**
	 * Gets the amount of stored String-Announce pairs
	 * 
	 * @return Returns the amount of stored String-Announce pairs
	 */
	public int getCacheAmount() {
		return parsedMessages.size();
	}

	/**
	 * Gets the amount of CommunicationPaths of which the cache has stored String-Announce pairs.
	 * This amount should be equals to {@link #getCacheAmount()}, because every time a device sends
	 * a different announce, its old stored String-Announce pair is overridden with the new one.
	 * 
	 * @return Returns the amount of stored CommunicationPaths
	 */
	public int getPathsAmount() {
		return awailablePaths.size();
	}

	/**
	 * This method adds a new String-Announce pair to the cache. If the device has sent an announce
	 * earlier, its old announce is overridden.
	 * 
	 * @param announceString
	 *            The announce String
	 * @param comPath
	 *            The communication path, which contains the device information decoded in
	 *            {@link CommunicationPath#hashCode()} and the parsed Announce object
	 */
	public void addCommunicationPath(String announceString, CommunicationPath comPath) {
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
