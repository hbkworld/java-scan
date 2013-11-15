package com.hbm.devices.scan.messages;

public abstract class JsonRpc {
	
	protected JsonRpc(String method) {
		jsonrpc = new String("2.0");
		this.method = method;
	}

	private String jsonrpc;
	private String method;
}
