/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Hottinger Baldwin Messtechnik GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hbm.devices.scan;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This class emits fake announce messages.
 * <p>
 *
 * @since 1.0
 */
public class FakeMessageReceiver extends AbstractMessageReceiver {

    private boolean shallRun = true;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    private static final String CORRECT_MESSAGE;
    private static final String CORRECT_MESSAGE_NO_IPV4;
    private static final String CORRECT_MESSAGE_NO_IPV6;
    private static final String MESSAGE_IPV6_IN_IPV4;
    private static final String MESSAGE_IPV4_IN_IPV6;
    private static final String MESSAGE_ILLEGAL_IPV4;
    private static final String MESSAGE_ILLEGAL_IPV6;
    private static final String CORRECT_MESSAGE_NO_EXPIRE;
    private static final String CORRECT_MESSAGE_NEGATIVE_EXPIRE;
    private static final String CORRECT_MESSAGE_SHORT_EXPIRE;
    private static final String CORRECT_MESSAGE_MANUAL;
    private static final String CORRECT_MESSAGE_ROUTER_SOLICIT;
    private static final String MISSING_VERSION_MESSAGE;
    private static final String VERSION_2;
    private static final String MISSING_CONFIG_MESSAGE;
    private static final String CORRECT_MESSAGE_DIFFERENT_IP;
    private static final String CORRECT_MESSAGE_DIFFERENT_SERVICES;
    private static final String CORRECT_MESSAGE_DIFFERENT_DEVICE;
    private static final String INVALID_JSON_MESSAGE;
    private static final String MISSING_DEVICE_MESSAGE;
    private static final String MISSING_DEVICE_UUID_MESSAGE;
    private static final String EMPTY_DEVICE_UUID_MESSAGE;
    private static final String MISSING_PARAMS_MESSAGE;
    private static final String NO_INTERFACE_NAME_MESSAGE;
    private static final String EMPTY_INTERFACE_NAME_MESSAGE;
    private static final String INTERFACE_NAME_IS_NUMBER_MESSAGE;
    private static final String INTERFACE_NAME_IS_OBJECT_MESSAGE;
    private static final String NO_INTERFACE_DESCRIPTION_MESSAGE;
    private static final String INTERFACE_DESCRIPTION_IS_NUMBER;
    private static final String NO_INTERFACE_TYPE_MESSAGE;
    private static final String INTERFACE_TYPE_IS_NUMBER;
    private static final String NO_INTERFACE_MESSAGE;
    private static final String NO_NET_SETTINGS_MESSAGE;
    private static final String MISSING_ROUTER_UUID_MESSAGE;
    private static final String EMPTY_ROUTER_UUID_MESSAGE;
    private static final String MISSING_FAMILY_TYPE_MESSAGE;
    private static final String MISSING_SERVICE_MESSAGE;
    private static final String EMPTY_SERVICE_MESSAGE;
    private static final String MISSING_HTTP_MESSAGE;
    private static final String NOT_ANNOUNCE_MESSAGE;
    private static final String MISSING_METHOD_MESSAGE;
    private static final String NO_SUCCESS_ID_RESPONSE_MESSAGE;
    private static final String EMPTY_SUCCESS_ID_RESPONSE_MESSAGE;
    private static final String CORRECT_ERROR_RESPONSE_MESSAGE;
    private static final String INVALID_ERROR_SUCCESS_RESPONSE_MESSAGE;
    private static final String MISSING_ERROR_MESSAGE_REPONSE_MESSAGE;
    private static final String NO_ERROR_MESSAGE_RESPONSE_MESSAGE;
    private static final String ERROR_AND_RESULT_RESPONSE_MESSAGE;
    private static final String NULL_MESSAGE;
    private static final String PARAMS_NULL_MESSAGE;
    private static final String DEVICE_NULL_MESSAGE;
    private static final String UUID_NULL_MESSAGE;
    private static final String NETSETTINGS_NULL_MESSAGE;
    private static final String INTERFACE_NULL_MESSAGE;
    private static final String INTERFACENAME_NULL_MESSAGE;
    private static final String SERVICE_NULL_MESSAGE;
    private static final String SINGLE_NULL_SERVICE_ENTRY;
    private static final String SERVICE_ENTRY_NULL_MESSAGE;
    private static final String SERVICE_STRING_MESSAGE;

    private static final String FAKE_DEVICE_1;
    private static final String FAKE_DEVICE_2;
    private static final String FAKE_DEVICE_3;
    private static final String FAKE_DEVICE_4;
    private static final String FAKE_DEVICE_5;

    public void emitSingleCorrectMessage() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE);
    }

    public void emitSingleCorrectMessageNoIpv4() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_NO_IPV4);
    }

    public void emitSingleCorrectMessageNoIpv6() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_NO_IPV6);
    }

    public void emitSingleMessageIpv6InIpv4() {
        setChanged();
        notifyObservers(MESSAGE_IPV6_IN_IPV4);
    }

    public void emitSingleMessageIpv4InIpv6() {
        setChanged();
        notifyObservers(MESSAGE_IPV4_IN_IPV6);
    }

    public void emitIllegalIpv4() {
        setChanged();
        notifyObservers(MESSAGE_ILLEGAL_IPV4);
    }

    public void emitIllegalIpv6() {
        setChanged();
        notifyObservers(MESSAGE_ILLEGAL_IPV6);
    }

    public void emitMissingExpiration() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_NO_EXPIRE);
    }

    public void emitNegativeExpiration() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_NEGATIVE_EXPIRE);
    }

    public void emitSingleCorrectMessageShortExpire() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_SHORT_EXPIRE);
    }

    public void emitSingleCorrectMessageDevice2() {
        setChanged();
        notifyObservers(FAKE_DEVICE_2);
    }

    public void emitCorrectMessageManual() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_MANUAL);
    }

    public void emitCorrectMessageRouterSolicit() {
        setChanged();
        notifyObservers(CORRECT_MESSAGE_ROUTER_SOLICIT);
    }

    public void emitMissingVersion() {
        setChanged();
        notifyObservers(MISSING_VERSION_MESSAGE);
    }

    public void emitVersion2() {
        setChanged();
        notifyObservers(VERSION_2);
    }

    public void emitMissingConfigurationMethod() {
        setChanged();
        notifyObservers(MISSING_CONFIG_MESSAGE);
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
        notifyObservers(null);
    }

    public void emitMissingDeviceMessage() {
        setChanged();
        notifyObservers(MISSING_DEVICE_MESSAGE);
    }

    public void emitMissingDeviceUuidMessage() {
        setChanged();
        notifyObservers(MISSING_DEVICE_UUID_MESSAGE);
    }

    public void emitEmptyDeviceUuidMessage() {
        setChanged();
        notifyObservers(EMPTY_DEVICE_UUID_MESSAGE);
    }

    public void emitMissingParamsMessage() {
        setChanged();
        notifyObservers(MISSING_PARAMS_MESSAGE);
    }

    public void emitNoInterfaceNameMessage() {
        setChanged();
        notifyObservers(NO_INTERFACE_NAME_MESSAGE);
    }

    public void emitEmptyInterfaceNameMessage() {
        setChanged();
        notifyObservers(EMPTY_INTERFACE_NAME_MESSAGE);
    }

    public void emitInterfaceNameIsNumberMessage() {
        setChanged();
        notifyObservers(INTERFACE_NAME_IS_NUMBER_MESSAGE);
    }

    public void emitInterfaceNameIsObjectMessage() {
        setChanged();
        notifyObservers(INTERFACE_NAME_IS_OBJECT_MESSAGE);
    }

    public void emitNoInterfaceDescriptionMessage() {
        setChanged();
        notifyObservers(NO_INTERFACE_DESCRIPTION_MESSAGE);
    }

    public void emitInterfaceDescriptionIsNumber() {
        setChanged();
        notifyObservers(INTERFACE_DESCRIPTION_IS_NUMBER);
    }

    public void emitNoInterfaceTypeMessage() {
        setChanged();
        notifyObservers(NO_INTERFACE_TYPE_MESSAGE);
    }

    public void emitInterfaceTypeIsNumberMessage() {
        setChanged();
        notifyObservers(INTERFACE_TYPE_IS_NUMBER);
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

    public void emitEmptyRouterUuidMessage() {
        setChanged();
        notifyObservers(EMPTY_ROUTER_UUID_MESSAGE);
    }

    public void emitMissingFamilyTypeMessage() {
        setChanged();
        notifyObservers(MISSING_FAMILY_TYPE_MESSAGE);
    }

    public void emitSingleCorrectSuccessResponseMessage(String queryID) {
        final String correctSuccessResponseMessage = "{\"id\":\"" + queryID
                + "\",\"jsonrpc\":\"2.0\",\"result\":0}";
        setChanged();
        notifyObservers(correctSuccessResponseMessage);
    }

    public void emitMissingServiceMessage() {
        setChanged();
        notifyObservers(MISSING_SERVICE_MESSAGE);
    }

    public void emitEmptyServiceMessage() {
        setChanged();
        notifyObservers(EMPTY_SERVICE_MESSAGE);
    }

    public void emitMissingHttpMessage() {
        setChanged();
        notifyObservers(MISSING_HTTP_MESSAGE);
    }

    public void emitNotAnnounceMessage() {
        setChanged();
        notifyObservers(NOT_ANNOUNCE_MESSAGE);
    }

    public void emitMissingMethodMessage() {
        setChanged();
        notifyObservers(MISSING_METHOD_MESSAGE);
    }

    public void emitNoSuccessIdResponseMessage() {
        setChanged();
        notifyObservers(NO_SUCCESS_ID_RESPONSE_MESSAGE);
    }

    public void emitEmtpySuccessIdResponseMessage() {
        setChanged();
        notifyObservers(EMPTY_SUCCESS_ID_RESPONSE_MESSAGE);
    }

    public void emitSingleCorrectErrorResponseMessage() {
        setChanged();
        notifyObservers(CORRECT_ERROR_RESPONSE_MESSAGE);
    }

    public void emitInvalidErrorSuccessReponseMessage() {
        setChanged();
        notifyObservers(INVALID_ERROR_SUCCESS_RESPONSE_MESSAGE);
    }

    public void emitMissingErrorMessageReponseMessage() {
        setChanged();
        notifyObservers(MISSING_ERROR_MESSAGE_REPONSE_MESSAGE);
    }

    public void emitErrorAndResultResponseMessage() {
        setChanged();
        notifyObservers(ERROR_AND_RESULT_RESPONSE_MESSAGE);
    }

    public void emitString(String message) {
        setChanged();
        notifyObservers(message);
    }

    public void emitNullMessage() {
        setChanged();
        notifyObservers(NULL_MESSAGE);
    }

    public void emitParamsNullMessage() {
        setChanged();
        notifyObservers(PARAMS_NULL_MESSAGE);
    }

    public void emitDeviceNullMessage() {
        setChanged();
        notifyObservers(DEVICE_NULL_MESSAGE);
    }

    public void emitUuidNullMessage() {
        setChanged();
        notifyObservers(UUID_NULL_MESSAGE);
    }

    public void emitNetsettingNullMessage() {
        setChanged();
        notifyObservers(NETSETTINGS_NULL_MESSAGE);
    }

    public void emitInterfaceNullMessage() {
        setChanged();
        notifyObservers(INTERFACE_NULL_MESSAGE);
    }

    public void emitInterfaceNameNullMessage() {
        setChanged();
        notifyObservers(INTERFACENAME_NULL_MESSAGE);
    }

    public void emitServiceNullMessage() {
        setChanged();
        notifyObservers(SERVICE_NULL_MESSAGE);
    }

    public void emitSingleNullServiceEntryMessage() {
        setChanged();
        notifyObservers(SINGLE_NULL_SERVICE_ENTRY);
    }

    public void emitServiceEntryNullMessage() {
        setChanged();
        notifyObservers(SERVICE_ENTRY_NULL_MESSAGE);
    }

    public void emitServiceStringMessage() {
        setChanged();
        notifyObservers(SERVICE_STRING_MESSAGE);
    }

    @Override
    public void run() {
        setChanged();
        notifyObservers(FAKE_DEVICE_1);
        setChanged();
        notifyObservers(FAKE_DEVICE_2);
        setChanged();
        notifyObservers(FAKE_DEVICE_3);
        setChanged();
        notifyObservers(FAKE_DEVICE_4);
        setChanged();
        notifyObservers(FAKE_DEVICE_5);
        synchronized (this) {
            while (shallRun) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    LOGGER.info(e.toString());
                }
            }
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            shallRun = false;
            this.notifyAll();
        }
    }

    static {
        try (final InputStream is = FakeMessageReceiver.class.getResourceAsStream("/fakemessages.properties")) {
            final Properties props = new Properties();
            props.load(is);

            FAKE_DEVICE_1 = props.getProperty("scan.announce.device1");
            FAKE_DEVICE_2 = props.getProperty("scan.announce.device2");
            FAKE_DEVICE_3 = props.getProperty("scan.announce.device3");
            FAKE_DEVICE_4 = props.getProperty("scan.announce.device4");
            FAKE_DEVICE_5 = props.getProperty("scan.announce.device5");
            CORRECT_MESSAGE = props.getProperty("scan.announce.correctMessage");
            CORRECT_MESSAGE_NO_IPV4 = props.getProperty("scan.announce.correctMessageNoIPv4");
            CORRECT_MESSAGE_NO_IPV6 = props.getProperty("scan.announce.correctMessageNoIPv6");
            MESSAGE_IPV6_IN_IPV4 = props.getProperty("scan.announce.messageIPv6InIPv4");
            MESSAGE_IPV4_IN_IPV6 = props.getProperty("scan.announce.messageIPv4InIPv6");
            MESSAGE_ILLEGAL_IPV4 = props.getProperty("scan.announce.illegalIPv4");
            MESSAGE_ILLEGAL_IPV6 = props.getProperty("scan.announce.illegalIPv6");
            CORRECT_MESSAGE_NO_EXPIRE = props.getProperty("scan.announce.correctMessageMissingExpire");
            CORRECT_MESSAGE_NEGATIVE_EXPIRE = props.getProperty("scan.announce.correctMessageNegativeExpire");
            CORRECT_MESSAGE_SHORT_EXPIRE = props.getProperty("scan.announce.correctMessageShortExpire");
            CORRECT_MESSAGE_MANUAL = props.getProperty("scan.announce.correctMessageManual");
            CORRECT_MESSAGE_ROUTER_SOLICIT = props.getProperty("scan.announce.correctMessageRouterSolicitation");
            MISSING_VERSION_MESSAGE = props.getProperty("scan.announce.missingVersion");
            VERSION_2 = props.getProperty("scan.announce.version2");
            MISSING_CONFIG_MESSAGE = props.getProperty("scan.announce.missingConfigMethod");
            CORRECT_MESSAGE_DIFFERENT_IP = props.getProperty("scan.announce.correctMessageDifferentIP");
            CORRECT_MESSAGE_DIFFERENT_SERVICES = props.getProperty("scan.announce.correctMessageDifferentServices");
            CORRECT_MESSAGE_DIFFERENT_DEVICE = props.getProperty("scan.announce.correctMessageDifferentDevice");
            INVALID_JSON_MESSAGE = props.getProperty("scan.announce.invalidJsonMessage");
            MISSING_DEVICE_MESSAGE = props.getProperty("scan.announce.missingDeviceMessage");
            MISSING_DEVICE_UUID_MESSAGE = props.getProperty("scan.announce.missingDeviceUuidMessage");
            EMPTY_DEVICE_UUID_MESSAGE = props.getProperty("scan.announce.emptyDeviceUuidMessage");
            MISSING_PARAMS_MESSAGE = props.getProperty("scan.announce.missingParamsMessage");
            NO_INTERFACE_NAME_MESSAGE = props.getProperty("scan.announce.noInterfaceNameMessage");
            EMPTY_INTERFACE_NAME_MESSAGE = props.getProperty("scan.announce.emptyInterfaceNameMessage");
            INTERFACE_NAME_IS_NUMBER_MESSAGE = props.getProperty("scan.announce.interfaceNameIsNumber");
            INTERFACE_NAME_IS_OBJECT_MESSAGE = props.getProperty("scan.announce.interfaceNameIsObject");
            NO_INTERFACE_DESCRIPTION_MESSAGE = props.getProperty("scan.announce.correctMessageNoInterfaceDescription");
            INTERFACE_DESCRIPTION_IS_NUMBER = props.getProperty("scan.announce.interfaceDescriptionIsNumber");
            NO_INTERFACE_TYPE_MESSAGE = props.getProperty("scan.announce.correctMessageNoInterfaceType");
            INTERFACE_TYPE_IS_NUMBER = props.getProperty("scan.announce.interfaceTypeIsNumber");
            NO_INTERFACE_MESSAGE = props.getProperty("scan.announce.noInterfaceMessage");
            NO_NET_SETTINGS_MESSAGE = props.getProperty("scan.announce.noNetSettingsMessage");
            MISSING_ROUTER_UUID_MESSAGE = props.getProperty("scan.announce.missingRouterUuidMessage");
            EMPTY_ROUTER_UUID_MESSAGE = props.getProperty("scan.announce.emptyRouterUuidMessage");
            MISSING_FAMILY_TYPE_MESSAGE = props.getProperty("scan.announce.missingFamilyTypeMessage");
            MISSING_SERVICE_MESSAGE = props.getProperty("scan.announce.correctMessageMissingService");
            EMPTY_SERVICE_MESSAGE = props.getProperty("scan.announce.correctMessageMissingService");
            MISSING_HTTP_MESSAGE = props.getProperty("scan.announce.correctMessageNoHttp");
            NOT_ANNOUNCE_MESSAGE = props.getProperty("scan.announce.notAnnounceMessage");
            MISSING_METHOD_MESSAGE = props.getProperty("scan.configure.missingMethodMessage");
            NO_SUCCESS_ID_RESPONSE_MESSAGE = props.getProperty("scan.configure.noSuccessIdResponseMessage");
            EMPTY_SUCCESS_ID_RESPONSE_MESSAGE = props.getProperty("scan.configure.emptySuccessIdResponseMessage");
            CORRECT_ERROR_RESPONSE_MESSAGE = props.getProperty("scan.configure.correctErrorResponseMessage");
            INVALID_ERROR_SUCCESS_RESPONSE_MESSAGE = props.getProperty("scan.configure.invalidErrorSuccessResponseMessage");
            MISSING_ERROR_MESSAGE_REPONSE_MESSAGE = props.getProperty("scan.configure.missingErrorMessageResponseMessage");
            NO_ERROR_MESSAGE_RESPONSE_MESSAGE = props.getProperty("scan.configure.noErrorMessageResponseMessage");
            ERROR_AND_RESULT_RESPONSE_MESSAGE = props.getProperty("scan.configure.errorAndResultResponseMessage");
            NULL_MESSAGE = props.getProperty("scan.null");
            PARAMS_NULL_MESSAGE = props.getProperty("scan.params.null");
            DEVICE_NULL_MESSAGE = props.getProperty("scan.device.null");
            UUID_NULL_MESSAGE = props.getProperty("scan.uuid.null");
            NETSETTINGS_NULL_MESSAGE = props.getProperty("scan.netsettings.null");
            INTERFACE_NULL_MESSAGE = props.getProperty("scan.interface.null");
            INTERFACENAME_NULL_MESSAGE = props.getProperty("scan.interfacename.null");
            SERVICE_NULL_MESSAGE = props.getProperty("scan.services.null");
            SINGLE_NULL_SERVICE_ENTRY = props.getProperty("scan.singleServiceEntry.null");
            SERVICE_ENTRY_NULL_MESSAGE = props.getProperty("scan.serviceEntry.null");
            SERVICE_STRING_MESSAGE = props.getProperty("scan.service.string");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
