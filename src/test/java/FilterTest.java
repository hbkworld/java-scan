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

import org.junit.*;
import static org.junit.Assert.*;

import com.hbm.devices.scan.CommunicationPath;
import com.hbm.devices.scan.MessageParser;
import com.hbm.devices.scan.filter.Filter;
import com.hbm.devices.scan.filter.FamilytypeMatch;
import com.hbm.devices.scan.FakeMessageReceiver;
import java.util.Observable;
import java.util.Observer;

public class FilterTest {

    private CommunicationPath ap;
    private FakeMessageReceiver fsmmr;

    @Before
    public void setup() {
        fsmmr = new FakeMessageReceiver();
        MessageParser jf = new MessageParser();
        fsmmr.addObserver(jf);
        String[] families = {"QuantumX"};
        Filter ftFilter = new Filter(new FamilytypeMatch(families));
        jf.addObserver(ftFilter);
        ftFilter.addObserver(new Observer(){
            public void update(Observable o, Object arg) {
                ap = (CommunicationPath)arg;
            }
        });
    }

    @Test
    public void parseMissingFamilyTypeMessage() {
        fsmmr.emitMissingFamilyTypeMessage();
        assertTrue(ap == null);
    }
}

