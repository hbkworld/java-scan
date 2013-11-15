package com.hbm.devices.scan.messages;

public class Leave extends JsonRpc {

	private Leave() {
		super("leave");
	}

	public LeaveParams getParams() {
		return params;
	}

	private LeaveParams params;
}
