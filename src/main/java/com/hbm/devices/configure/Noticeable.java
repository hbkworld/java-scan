package com.hbm.devices.configure;

/**
 * This interface is used to notify an obserable, that an observer had an Exception.
 * 
 * @author rene
 *
 */
public interface Noticeable {

	public void onException(Exception e);

}
