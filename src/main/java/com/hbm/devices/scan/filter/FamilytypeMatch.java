package com.hbm.devices.scan.filter;

import com.hbm.devices.scan.MissingDataException;
import com.hbm.devices.scan.messages.Announce;

/**
 * This class matches family type information in Announce objects.
 * <p>
 *
 * @since 1.0
 */
public class FamilytypeMatch implements Matcher {

    private String[] familyTypes;

    public FamilytypeMatch(String[] familyTypes) {
        this.familyTypes = familyTypes;
    }

    public boolean match(Announce a) throws MissingDataException {
        try {
            String ft = a.getParams().getDevice().getFamilyType();
            for (int i = 0; i < familyTypes.length; i++) {
                if (ft.equals(familyTypes[i])) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            throw new MissingDataException("No family type in announce object!");
        }
    }

    @Override
    public String getMatcherName() {
        return "Famility type";
    }

    @Override
    public String[] getFilterStrings() {
        return familyTypes;
    }
}
