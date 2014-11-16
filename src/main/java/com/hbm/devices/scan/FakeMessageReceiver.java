package com.hbm.devices.scan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * This class emits fake announce messages.
 * <p>
 *
 * @since 1.0
 */
public class FakeMessageReceiver extends MessageReceiver {

    private boolean shallRun = true;
	private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    public static final String CORRECT_MESSAGE = "{"
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

    public static final String CORRECT_MESSAGE_DIFFERENT_IP = "{"
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

    public static final String CORRECT_MESSAGE_DIFFERENT_SERVICES = "{"
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

    public static final String CORRECT_MESSAGE_DIFFERENT_DEVICE = "{"
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

    private static final String INVALID_JSON_MESSAGE = "{"
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

    private static final String MISSING_DEVICE_MESSAGE = "{"
            + "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
            + "\"apiVersion\":\"1.0\"," + "\"expiration\":15," + "\"netSettings\":{"
            + "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
            + "\"interface\":{\"description\":\"ethernet backplane side\","
            + "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
            + "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
            + "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}" + "}" + "}";

    private static final String MISSING_DEVICE_UUID_MESSAGE = "{"
            + "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
            + "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
            + "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
            + "\"name\":\"MX410 Matthias\",\"type\":\"MX410\"}," + "\"expiration\":15,"
            + "\"netSettings\":{" + "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"},"
            + "\"interface\":{\"description\":\"ethernet backplane side\","
            + "\"ipv4\":[{\"address\":\"172.19.192.57\",\"netmask\":\"255.255.0.0\"}],"
            + "\"ipv6\":[{\"address\":\"fe80::209:e5ff:fe00:123a\",\"prefix\":64}],"
            + "\"name\":\"eth0\",\"type\":\"ethernet\"}" + "}" + "}" + "}";

    private static final String MISSING_PARAMS_MESSAGE = "{"
            + "\"jsonrpc\":\"2.0\",\"method\":\"announce\"" + "}";

    private static final String NO_INTERFACE_NAME_MESSAGE = "{"
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

    private static final String NO_INTERFACE_MESSAGE = "{"
            + "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
            + "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
            + "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
            + "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
            + "\"expiration\":15," + "\"netSettings\":{"
            + "\"defaultGateway\":{\"ipv4Address\":\"172.19.169.254\"}" + "}" + "}" + "}";

    private static final String NO_NET_SETTINGS_MESSAGE = "{"
            + "\"jsonrpc\":\"2.0\",\"method\":\"announce\",\"params\":{"
            + "\"apiVersion\":\"1.0\",\"device\":{\"familyType\":\"QuantumX\","
            + "\"firmwareVersion\":\"4.1.1.18610.1\",\"hardwareId\":\"MX410_R0\","
            + "\"name\":\"MX410 Matthias\",\"type\":\"MX410\",\"uuid\":\"0009E500123A\"},"
            + "\"expiration\":15" + "}" + "}";

    private static final String MISSING_ROUTER_UUID_MESSAGE = "{"
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

    private static final String MISSING_FAMILY_TYPE_MESSAGE = "{"
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

    public static final String MISSING_TYPE_RESPONSE_MESSAGE = "{\"id\":\"TEST-UUID\",\"jsonrpc\":\"2.0\"}";
    public static final String NO_SUCCESS_ID_RESPONSE_MESSAGE = "{\"id\":\"TEST-UUID\",\"jsonrpc\":\"2.0\",\"result\":}";
    public static final String CORRECT_ERROR_RESPONSE_MESSAGE = "{\"error\":{\"code\":-3,\"message\":\"'abcd' is not a valid ip V4 address\"},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
    public static final String INVALID_ERROR_SUCCESS_RESPONSE_MESSAGE = "{\"result\":0,\"error\":{\"code\":-3,\"message\":\"'abcd' is not a valid ip V4 address\"},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
    public static final String MISSING_ERROR_OBJECT_RESPONSE_MESSAGE = "{\"error\":,\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
    public static final String MISSING_ERROR_CODE_RESPONSE_MESSAGE = "{\"error\":{\"message\":\"'abcd' is not a valid ip V4 address\"},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
    public static final String NO_ERROR_CODE_RESPONSE_MESSAGE = "{\"error\":{\"code\":,\"message\":\"'abcd' is not a valid ip V4 address\"},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
    public static final String MISSING_ERROR_MESSAGE_REPONSE_MESSAGE = "{\"error\":{\"code\":-3},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";
    public static final String NO_ERROR_MESSAGE_RESPONSE_MESSAGE = "{\"error\":{\"code\":-3,\"message\":},\"id\":\"9f22cf19-87f0-48e9-8c4d-43fe2eb80775\",\"jsonrpc\":\"2.0\"}";

    public void emitSingleCorrectMessage() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE);
    }

    public void emitSingleCorrentMessageDifferentIP() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_DIFFERENT_IP);
    }

    public void emitSingleCorrectMessageDifferentServices() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_DIFFERENT_SERVICES);
    }

    public void emitSingleCorrectMessageDifferentDevice() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_DIFFERENT_DEVICE);
    }

    public void emitInvalidJsonMessage() {
        setChanged();
        notifyObservers(INVALID_JSON_MESSAGE);
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
        notifyObservers(MISSING_DEVICE_MESSAGE);
    }

    public void emitMissingDeviceUuidMessage() {
        setChanged();
        notifyObservers(MISSING_DEVICE_UUID_MESSAGE);
    }

    public void emitMissingParamsMessage() {
        setChanged();
        notifyObservers(MISSING_PARAMS_MESSAGE);
    }

    public void emitNoInterfaceNameMessage() {
        setChanged();
        notifyObservers(NO_INTERFACE_NAME_MESSAGE);
    }

    public void emitNoInterfaceMessage() {
        setChanged();
        notifyObservers(NO_INTERFACE_MESSAGE);
    }

    public void emitNoNetSettingsMessage() {
        setChanged();
        notifyObservers(NO_NET_SETTINGS_MESSAGE);
    }

    public void emitMissingRouterUuidMessage() {
        setChanged();
        notifyObservers(MISSING_ROUTER_UUID_MESSAGE);
    }

    public void emitMissingFamilyTypeMessage() {
        setChanged();
        notifyObservers(MISSING_FAMILY_TYPE_MESSAGE);
    }

    public void emitSingleCorrectSuccessResponseMessage(String queryID) {
        String correctSuccessResponseMessage = "{\"id\":\"" + queryID
                + "\",\"jsonrpc\":\"2.0\",\"result\":0}";
        setChanged();
        notifyObservers(correctSuccessResponseMessage);
    }

    public void emitMissingTypeResponseMessage() {
        setChanged();
        notifyObservers(MISSING_TYPE_RESPONSE_MESSAGE);
    }

    public void emitNoSuccessIdResponseMessage() {
        setChanged();
        notifyObservers(NO_SUCCESS_ID_RESPONSE_MESSAGE);
    }

    public void emitSingleCorrectErrorResponseMessage() {
        setChanged();
        notifyObservers(CORRECT_ERROR_RESPONSE_MESSAGE);
    }

    public void emitInvalidErrorSuccessReponseMessage() {
        setChanged();
        notifyObservers(INVALID_ERROR_SUCCESS_RESPONSE_MESSAGE);
    }

    public void emitMissingErrorObjectResponseMessage() {
        setChanged();
        notifyObservers(MISSING_ERROR_OBJECT_RESPONSE_MESSAGE);
    }

    public void emitMissingErrorCodeResponseMessage() {
        setChanged();
        notifyObservers(MISSING_ERROR_CODE_RESPONSE_MESSAGE);
    }

    public void emitNoErrorCodeResponseMessage() {
        setChanged();
        notifyObservers(NO_ERROR_CODE_RESPONSE_MESSAGE);
    }

    public void emitMissingErrorMessageReponseMessage() {
        setChanged();
        notifyObservers(MISSING_ERROR_MESSAGE_REPONSE_MESSAGE);
    }

    public void emitNoErrorMessageResponseMessage() {
        setChanged();
        notifyObservers(NO_ERROR_MESSAGE_RESPONSE_MESSAGE);
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
            synchronized (this) {
                while (shallRun) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            }
        } catch (IOException e) {
			LOGGER.info(e.toString());
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                // Ignore
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
