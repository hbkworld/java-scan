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
