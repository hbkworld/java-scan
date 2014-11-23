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

package com.hbm.devices.configure;

import java.util.Observable;
import java.util.Observer;

/**
 * This class simulates a multicast sender. But it does not send any message via the network, it
 * only stores the last send message, so the a test routine can easy check, which message would be
 * sent.
 * <p>
 * This is a class is only used for the JUnit tests.
 * 
 * @since 1.0
 *
 */
public class FakeMulticastSender implements Observer {

    private String lastSent;

    public FakeMulticastSender() {
    }

    public String getLastSent() {
        return this.lastSent;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.lastSent = (String) arg;
    }

}
