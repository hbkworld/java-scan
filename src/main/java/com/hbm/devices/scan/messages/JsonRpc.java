package com.hbm.devices.scan.messages;

public abstract class JsonRpc {
	
	protected JsonRpc(String method) {
		jsonrpc = new String("2.0");
		this.method = method;
	}
	
	public void setJSONString(String json) {
		this.json = json;
	}

	public String getJSONString() {
		return json;
	}

	private String jsonrpc;
	private String method;
	private String json;
}
