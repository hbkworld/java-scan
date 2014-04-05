package com.hbm.devices.scan;

import java.util.Observable;

public class FakeStringMessageMulticastReceiver extends Observable {

	public static final String correctMessage =
	"{" +
		"\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{" +
			"\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\"," + 
			"\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\"," +
			"\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"}," +
			"\"expiration\":15," + 
			"\"netSettings\":{" +
				"\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"}," +
				"\"interface\":{\"description\":\"ethernet backplane side\"," +
					"\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}]," + 
					"\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}]," +
					"\"name\":\"eth0\",\"type\":\"ethernet\"}"+
			"}," + 
			"\"services\":[" +
				"{\"port\":7411,\"type\":\"daqStream\"}," +
				"{\"port\":8080,\"type\":\"daqStreamWS\"}," +
				"{\"port\":5001,\"type\":\"hbmProtocol\"}," +
				"{\"port\":80,\"type\":\"http\"}," +
				"{\"port\":11122,\"type\":\"jetd\"}," +
				"{\"port\":11123,\"type\":\"jetws\"}," +
				"{\"port\":22,\"type\":\"ssh\"}" +
			"]" +
		"}" +
	"}";

	public FakeStringMessageMulticastReceiver() {
	}

}

