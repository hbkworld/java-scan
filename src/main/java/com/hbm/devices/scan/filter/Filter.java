package com.hbm.devices.scan.filter;

import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.MissingDataException;
import com.hbm.devices.scan.messages.Announce;

/**
 * This class filters {@link CommunicationPath} objects with according to a {@link Matcher} object.
 * <p>
 * The class reads {@link CommunicationPath} objects and notifies them if
 * {@link Matcher#match(Announce)} method returns true.
 * 
 * @since 1.0
 */
public class Filter extends Observable implements Observer {

    private Matcher matcher;

    public Filter(Matcher m) {
        this.matcher = m;
    }

    public Matcher getMatcher() {
        return this.matcher;
    }

    @Override
    public void update(Observable o, Object arg) {
        CommunicationPath ap = (CommunicationPath) arg;
        Announce announce = ap.getAnnounce();
        try {
            if (matcher.match(announce)) {
                setChanged();
                notifyObservers(ap);
            }
        } catch (MissingDataException e) {
            // Ignore
        }
    }
}
