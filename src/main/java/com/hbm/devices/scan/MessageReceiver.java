package com.hbm.devices.scan;

import java.util.Observable;

public abstract class MessageReceiver extends Observable {

	public abstract void start();
	public abstract void stop();
}
