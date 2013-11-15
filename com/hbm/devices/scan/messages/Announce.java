package com.hbm.devices.scan.messages;

public class Announce extends JsonRpc {

	private Announce() {
		super("announce");
	}

	public AnnounceParams getParams() {
		return params;
	}

	private AnnounceParams params;

	@Override
	public String toString() {
		return params.toString();
	}
}
