package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.MissingDataException;

/**
 * An object able to match information in {@link Announce} objects.
 *
 * @since 1.0
 */
public interface Matcher {
    /**
     * @param announce
     *            {@link Announce} packet to be investigated.
     * @return <code>true</code> if the information is in the {@link Announce} object,
     *         <code>false</code> otherwise.
     * @throws MissingDataException
     *             if some information in the JSON packet ist missing for a comparison.
     */
    boolean match(Announce announce) throws MissingDataException;

    /**
     * This method is used to get the name of the matcher. This simplifies displaying the filter
     * settings.
     * 
     * @return the name of the Mather.
     */
    public String getMatcherName();

    /**
     * This method returns all strings the filter allows. This simplifies displaying the filter
     * settings.
     * 
     * @return an array containing all filter strings.
     */
    public String[] getFilterStrings();
}
