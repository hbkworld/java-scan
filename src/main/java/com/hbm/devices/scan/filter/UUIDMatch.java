package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.MissingDataException;
import com.hbm.devices.scan.messages.Announce;

/**
 * This class matches device uuids in Announce objects.
 * 
 * @since 1.0
 *
 */
public class UUIDMatch implements Matcher {

    private String[] uuids;

    public UUIDMatch(String[] uuids) {
        this.uuids = uuids;
    }

    @Override
    public boolean match(Announce announce) throws MissingDataException {
        try {
            String deviceUUID = announce.getParams().getDevice().getUuid();
            for (String s : uuids) {
                if (deviceUUID.equals(s)) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            throw new MissingDataException("No uuid type in response object");
        }
    }

    @Override
    public String getMatcherName() {
        return "UUID";
    }

    @Override
    public String[] getFilterStrings() {
        return uuids;
    }

}
