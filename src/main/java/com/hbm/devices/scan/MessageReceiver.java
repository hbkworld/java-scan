package com.hbm.devices.scan;

import java.util.Observable;

public abstract class MessageReceiver extends Observable {

	abstract void start();
	abstract void stop();
}
