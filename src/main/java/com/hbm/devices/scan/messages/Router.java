package com.hbm.devices.scan.messages;

public class Router {

	private Router() {
	}

	public String getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		return "Router:\n" +
		"\t uuid: " + uuid;
	}

	private String uuid;
}

