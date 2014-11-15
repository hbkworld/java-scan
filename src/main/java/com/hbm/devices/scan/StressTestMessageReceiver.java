package com.hbm.devices.scan;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class StressTestMessageReceiver extends MessageReceiver {

	private List<String> deviceUuidList;
	private int loopAmount;

	private long startNanoTime;

	public StressTestMessageReceiver(int deviceAmount, int loopAmount) {
		this.loopAmount = loopAmount;
		this.deviceUuidList = new LinkedList<String>();

		for (int i = 0; i < deviceAmount; i++) {
			this.deviceUuidList.add(getAnnounceString(UUID.randomUUID().toString()));
		}
	}

	private static String getAnnounceString(String uuid) {
		return "{" + "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
				+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
				+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
				+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"" + uuid + "\"},"
				+ "\"expiration\":15," + "\"netSettings\":{"
				+ "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
				+ "\"interface\":{\"description\":\"ethernet backplane side\","
				+ "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
				+ "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
				+ "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}," + "\"services\":["
				+ "{\"port\":7411,\"type\":\"daqStream\"},"
				+ "{\"port\":8080,\"type\":\"daqStreamWS\"},"
				+ "{\"port\":5001,\"type\":\"hbmProtocol\"}," + "{\"port\":80,\"type\":\"http\"},"
				+ "{\"port\":11122,\"type\":\"jetd\"}," + "{\"port\":11123,\"type\":\"jetws\"},"
				+ "{\"port\":22,\"type\":\"ssh\"}" + "]" + "}" + "}";
	}

	public long getStartNanoTime() {
		return this.startNanoTime;
	}

	@Override
	public void start() {
		System.out.println("Starting Test: " + loopAmount + " x " + deviceUuidList.size()
				+ " Announces");
		this.startNanoTime = System.nanoTime();

		for (int i = 0; i < loopAmount; i++) {
			for (String s : this.deviceUuidList) {
				setChanged();
				notifyObservers(s);
			}
		}

		System.out.println("Duration for sending & parsing " + loopAmount + " x "
				+ deviceUuidList.size() + " announces: "
				+ ((System.nanoTime() - startNanoTime) / 1000000d) + "ms");

	}

	@Override
	public void stop() {
	}

}
