package com.hbm.devices.scan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class emits fake announce messages.
 * <p>
 *
 * @since 1.0
 */
public class FakeMessageReceiver extends MessageReceiver {

	private boolean shallRun = true;

	public static final String correctMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
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

	public static final String correctMessageDifferentIP = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
			+ "\"expiration\":15," + "\"netSettings\":{"
			+ "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
			+ "\"interface\":{\"description\":\"ethernet backplane side\","
			+ "\"ipv4\":[{\"address\":\"172.19.192.63\",\"netmask\":\"255.255.0.0\"}],"
			+ "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123b\",\"prefix\":64}],"
			+ "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}," + "\"services\":["
			+ "{\"port\":7411,\"type\":\"daqStream\"},"
			+ "{\"port\":8080,\"type\":\"daqStreamWS\"},"
			+ "{\"port\":5001,\"type\":\"hbmProtocol\"}," + "{\"port\":80,\"type\":\"http\"},"
			+ "{\"port\":11122,\"type\":\"jetd\"}," + "{\"port\":11123,\"type\":\"jetws\"},"
			+ "{\"port\":22,\"type\":\"ssh\"}" + "]" + "}" + "}";

	public static final String correctMessageDifferentServices = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
			+ "\"expiration\":15," + "\"netSettings\":{"
			+ "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
			+ "\"interface\":{\"description\":\"ethernet backplane side\","
			+ "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
			+ "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
			+ "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}," + "\"services\":["
			+ "{\"port\":7411,\"type\":\"daqStream\"},"
			+ "{\"port\":8080,\"type\":\"daqStreamWS\"},"
			+ "{\"port\":5001,\"type\":\"hbmProtocol\"}," + "{\"port\":80,\"type\":\"http\"},"
			+ "{\"port\":11122,\"type\":\"jetd\"}," + "{\"port\":22,\"type\":\"ssh\"}" + "]" + "}"
			+ "}";

	public static final String correctMessageDifferentDevice = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123C\"},"
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

	private static final String invalidJsonMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
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

	private static final String missingDeviceMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\"," + "\"expiration\":15," + "\"netSettings\":{"
			+ "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
			+ "\"interface\":{\"description\":\"ethernet backplane side\","
			+ "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
			+ "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
			+ "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}" + "}" + "}";

	private static final String missingDeviceUuidMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\"}," + "\"expiration\":15,"
			+ "\"netSettings\":{" + "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
			+ "\"interface\":{\"description\":\"ethernet backplane side\","
			+ "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
			+ "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
			+ "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}" + "}" + "}";

	private static final String missingParamsMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\"" + "}";

	private static final String noInterfaceNameMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
			+ "\"expiration\":15," + "\"netSettings\":{"
			+ "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
			+ "\"interface\":{\"description\":\"ethernet backplane side\","
			+ "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
			+ "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
			+ "\"type\":\"ethernet\"}" + "}" + "}" + "}";

	private static final String noInterfaceMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
			+ "\"expiration\":15," + "\"netSettings\":{"
			+ "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"}" + "}" + "}" + "}";

	private static final String noNetSettingsMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
			+ "\"expiration\":15" + "}" + "}";

	private static final String missingRouterUuidMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
			+ "\"expiration\":15," + "\"netSettings\":{"
			+ "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
			+ "\"interface\":{\"description\":\"ethernet backplane side\","
			+ "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
			+ "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
			+ "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}," + "\"router\": {" + "}" + "}" + "}";

	private static final String missingFamilyTypeMessage = "{"
			+ "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
			+ "\"apiVersion\":\"1.0\",\"device\":{"
			+ "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
			+ "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
			+ "\"expiration\":15," + "\"netSettings\":{"
			+ "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
			+ "\"interface\":{\"description\":\"ethernet backplane side\","
			+ "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
			+ "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
			+ "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}" + "}" + "}";

	public void emitSingleCorrectMessage() {
		setChanged();
		notifyObservers(correctMessage);
	}

	public void emitSingleCorrentMessageDifferentIP() {
		setChanged();
		notifyObservers(correctMessageDifferentIP);
	}

	public void emitSingleCorrectMessageDifferentServices() {
		setChanged();
		notifyObservers(correctMessageDifferentServices);
	}

	public void emitSingleCorrectMessageDifferentDevice() {
		setChanged();
		notifyObservers(correctMessageDifferentDevice);
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

	public void emitMissingFamilyTypeMessage() {
		setChanged();
		notifyObservers(missingFamilyTypeMessage);
	}

	public static String missingTypeResponseMessage = "{\"id\":\"TEST-UUID\",\"jsonrpc\":\"2.0\"}";
	public static String noSuccessIdResponseMessage = "{\"id\":\"TEST-UUID\",\"jsonrpc\":\"2.0\",\"result\":}";
	public static String correctErrorResponseMessage = "{\"error\":{\"code\":-3,\"message\":\"'abcd' is not a valid ip V4 address\"},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
	public static String invalidErrorSuccessResponseMessage = "{\"result\":0,\"error\":{\"code\":-3,\"message\":\"'abcd' is not a valid ip V4 address\"},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
	public static String missingErrorObjectResponseMessage = "{\"error\":,\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
	public static String missingErrorCodeResponseMessage = "{\"error\":{\"message\":\"'abcd' is not a valid ip V4 address\"},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
	public static String noErrorCodeResponseMessage = "{\"error\":{\"code\":,\"message\":\"'abcd' is not a valid ip V4 address\"},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
	public static String missingErrorMessageReponseMessage = "{\"error\":{\"code\":-3},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
	public static String noErrorMessageResponseMessage = "{\"error\":{\"code\":-3,\"message\":},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";

	public void emitSingleCorrectSuccessResponseMessage(String queryID) {
		String correctSuccessResponseMessage = "{\"id\":\"" + queryID
				+ "\",\"jsonrpc\":\"2.0\",\"result\":0}";
		setChanged();
		notifyObservers(correctSuccessResponseMessage);
	}

	public void emitMissingTypeResponseMessage() {
		setChanged();
		notifyObservers(missingTypeResponseMessage);
	}

	public void emitNoSuccessIdResponseMessage() {
		setChanged();
		notifyObservers(noSuccessIdResponseMessage);
	}

	public void emitSingleCorrectErrorResponseMessage() {
		setChanged();
		notifyObservers(correctErrorResponseMessage);
	}

	public void emitInvalidErrorSuccessReponseMessage() {
		setChanged();
		notifyObservers(invalidErrorSuccessResponseMessage);
	}

	public void emitMissingErrorObjectResponseMessage() {
		setChanged();
		notifyObservers(missingErrorObjectResponseMessage);
	}

	public void emitMissingErrorCodeResponseMessage() {
		setChanged();
		notifyObservers(missingErrorCodeResponseMessage);
	}

	public void emitNoErrorCodeResponseMessage() {
		setChanged();
		notifyObservers(noErrorCodeResponseMessage);
	}

	public void emitMissingErrorMessageReponseMessage() {
		setChanged();
		notifyObservers(missingErrorMessageReponseMessage);
	}

	public void emitNoErrorMessageResponseMessage() {
		setChanged();
		notifyObservers(noErrorMessageResponseMessage);
	}
	
	public void start() {
		InputStream is = getClass().getResourceAsStream("/messages.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				setChanged();
				notifyObservers(line);
			}
		} catch (IOException e) {
		}
		synchronized (this) {
			while (shallRun) {
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void stop() {
		synchronized (this) {
			shallRun = false;
			this.notifyAll();
		}
	}
}
