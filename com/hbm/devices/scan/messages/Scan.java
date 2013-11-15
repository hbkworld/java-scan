package com.hbm.devices.scan.messages;

public class Scan extends JsonRpc {

	public Scan(int ttl) {
		super("scan");
		params = new ScanParams(ttl);
	}

	private Scan() {
		super("scan");
		params = new ScanParams(1);
	}

	public ScanParams getParams() {
		return params;
	}

	private ScanParams params;
}

class ScanParams {
	ScanParams(int ttl) {
		this.ttl = ttl;
	}

	private ScanParams() {
	}

	public int getTtl() {
		return ttl;
	}
	private int ttl;
}
