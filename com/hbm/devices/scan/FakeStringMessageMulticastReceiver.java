package com.hbm.devices.scan;

import java.util.Observable;

public class FakeStringMessageMulticastReceiver extends Observable {

	private static final String correctMessage =
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

	private static final String invalidJsonMessage =
	"{" +
		"\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":" +
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

	private static final String missingDeviceMessage =
	"{" +
		"\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{" +
			"\"apiVersion\":\"1.0\"," +
			"\"expiration\":15," + 
			"\"netSettings\":{" +
				"\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"}," +
				"\"interface\":{\"description\":\"ethernet backplane side\"," +
					"\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}]," + 
					"\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}]," +
					"\"name\":\"eth0\",\"type\":\"ethernet\"}"+
			"}" + 
		"}" +
	"}";

	private static final String missingDeviceUuidMessage =
	"{" +
		"\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{" +
			"\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\"," + 
			"\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\"," +
			"\"name\":\"MX410 Matthias\",\"type\":\"MX410\"}," +
			"\"expiration\":15," + 
			"\"netSettings\":{" +
				"\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"}," +
				"\"interface\":{\"description\":\"ethernet backplane side\"," +
					"\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}]," + 
					"\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}]," +
					"\"name\":\"eth0\",\"type\":\"ethernet\"}"+
			"}" + 
		"}" +
	"}";

	private static final String missingParamsMessage =
	"{" +
		"\"jsonrpc\":\"2.0\",\"method\":\"announce\"" +
	"}";

	private static final String noInterfaceNameMessage =
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
					"\"type\":\"ethernet\"}"+
			"}" + 
		"}" +
	"}";

	private static final String noInterfaceMessage =
	"{" +
		"\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{" +
			"\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\"," + 
			"\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\"," +
			"\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"}," +
			"\"expiration\":15," + 
			"\"netSettings\":{" +
				"\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"}" +
			"}" + 
		"}" +
	"}";

	private static final String noNetSettingsMessage =
	"{" +
		"\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{" +
			"\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\"," + 
			"\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\"," +
			"\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"}," +
			"\"expiration\":15" + 
		"}" +
	"}";

	private static final String missingRouterUuidMessage =
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
			"\"router\": {" +
				"\"uuid\": \"0x12345\"" +
			"}" +
		"}" +
	"}";

	public void emitSingleCorrectMessage() {
		setChanged();
		notifyObservers(correctMessage);
	}

	public void emitInvalidJsonMessage() {
		setChanged();
		notifyObservers(invalidJsonMessage);
	}

	public void emitEmptyString() {
		setChanged();
		notifyObservers("");
	}

	public void emitNull() {
		setChanged();
		notifyObservers("");
	}

	public void emitMissingDeviceMessage() {
		setChanged();
		notifyObservers(missingDeviceMessage);
	}
	
	public void emitMissingDeviceUuidMessage() {
		setChanged();
		notifyObservers(missingDeviceUuidMessage);
	}

	public void emitMissingParamsMessage() {
		setChanged();
		notifyObservers(missingParamsMessage);
	}

	public void emitNoInterfaceNameMessage() {
		setChanged();
		notifyObservers(noInterfaceNameMessage);
	}

	public void emitNoInterfaceMessage() {
		setChanged();
		notifyObservers(noInterfaceMessage);
	}

	public void emitNoNetSettingsMessage() {
		setChanged();
		notifyObservers(noNetSettingsMessage);
	}

	public void emitMissingRouterUuidMessage() {
		setChanged();
		notifyObservers(missingRouterUuidMessage);
	}
		
	public void start() {
		for (int i = 0; i < 1; i++) {
			setChanged();
			notifyObservers(correctMessage);
			try {
				synchronized(this) {
					this.wait(6000);
				}
			} catch (InterruptedException e) {
			}
		}
	}

}

