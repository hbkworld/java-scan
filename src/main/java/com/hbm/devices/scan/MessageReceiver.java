package com.hbm.devices.scan;

import java.util.Observable;

/**
 * Super class for all message receivers.
 * <p>
 *
 * @since 1.0
 */
public abstract class MessageReceiver extends Observable {

	public abstract void start();
	public abstract void stop();
}
