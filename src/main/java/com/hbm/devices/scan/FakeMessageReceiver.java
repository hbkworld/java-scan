/*
 * Java Scan, a library for scanning and configuring HBM devices.
 * Copyright (C) 2014 Stephan Gatzka
 *
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Academic Free License (AFL 3.0)
 * that is bundled with this package in the file LICENSE.
 * It is also available through the world-wide-web at this URL:
 * http://opensource.org/licenses/afl-3.0.php
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
public class FakeMessageReceiver extends MessageReceiver {

    private boolean shallRun = true;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    public static final String CORRECT_MESSAGE;
    private static final String CORRECT_MESSAGE_DIFFERENT_IP;
    public static final String CORRECT_MESSAGE_DIFFERENT_SERVICES;
    public static final String CORRECT_MESSAGE_DIFFERENT_DEVICE;
    private static final String INVALID_JSON_MESSAGE;
    private static final String MISSING_DEVICE_MESSAGE;
    private static final String MISSING_DEVICE_UUID_MESSAGE;
    private static final String MISSING_PARAMS_MESSAGE;
    private static final String NO_INTERFACE_NAME_MESSAGE;
    private static final String NO_INTERFACE_MESSAGE;
    private static final String NO_NET_SETTINGS_MESSAGE;
    private static final String MISSING_ROUTER_UUID_MESSAGE;
    private static final String MISSING_FAMILY_TYPE_MESSAGE;
    private static final String MISSING_TYPE_RESPONSE_MESSAGE;
    private static final String NO_SUCCESS_ID_RESPONSE_MESSAGE;
    private static final String CORRECT_ERROR_RESPONSE_MESSAGE;
    private static final String INVALID_ERROR_SUCCESS_RESPONSE_MESSAGE;
    private static final String MISSING_ERROR_OBJECT_RESPONSE_MESSAGE;
    private static final String MISSING_ERROR_CODE_RESPONSE_MESSAGE;
    private static final String NO_ERROR_CODE_RESPONSE_MESSAGE;
    private static final String MISSING_ERROR_MESSAGE_REPONSE_MESSAGE;
    private static final String NO_ERROR_MESSAGE_RESPONSE_MESSAGE;

    private static final String FAKE_DEVICE_1;
    private static final String FAKE_DEVICE_2;
    private static final String FAKE_DEVICE_3;
    private static final String FAKE_DEVICE_4;
    private static final String FAKE_DEVICE_5;

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

    public void stop() {
        synchronized (this) {
            shallRun = false;
            this.notifyAll();
        }
    }

    static {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("fakemessages.properties");
            Properties props = new Properties();
            props.load(is);
            FAKE_DEVICE_1 = props.getProperty("scan.announce.device1");
            FAKE_DEVICE_2 = props.getProperty("scan.announce.device2");
            FAKE_DEVICE_3 = props.getProperty("scan.announce.device3");
            FAKE_DEVICE_4 = props.getProperty("scan.announce.device4");
            FAKE_DEVICE_5 = props.getProperty("scan.announce.device5");
            CORRECT_MESSAGE = props.getProperty("scan.announce.correct_message");
            CORRECT_MESSAGE_DIFFERENT_IP = props.getProperty("scan.announce.correct_message_different_ip");
            CORRECT_MESSAGE_DIFFERENT_SERVICES = props.getProperty("scan.announce.correct_message_different_services");
            CORRECT_MESSAGE_DIFFERENT_DEVICE = props.getProperty("scan.announce.correct_message_different_device");
            INVALID_JSON_MESSAGE = props.getProperty("scan.announce.invalid_json_message");
            MISSING_DEVICE_MESSAGE = props.getProperty("scan.announce.scan.announce.missing_device_message");
            MISSING_DEVICE_UUID_MESSAGE = props.getProperty("scan.announce.missing_device_uuid_message");
            MISSING_PARAMS_MESSAGE = props.getProperty("scan.announce.missing_params_message");
            NO_INTERFACE_NAME_MESSAGE = props.getProperty("scan.announce.no_interface_name_message");
            NO_INTERFACE_MESSAGE = props.getProperty("scan.announce.no_interface_message");
            NO_NET_SETTINGS_MESSAGE = props.getProperty("scan.announce.no_net_settings_message");
            MISSING_ROUTER_UUID_MESSAGE = props.getProperty("scan.announce.missing_router_uuid_message");
            MISSING_FAMILY_TYPE_MESSAGE = props.getProperty("scan.announce.missing_family_type_message");
            MISSING_TYPE_RESPONSE_MESSAGE = props.getProperty("scan.configure.missing_type_response_message");
            NO_SUCCESS_ID_RESPONSE_MESSAGE = props.getProperty("scan.configure.no_success_id_response_message");
            CORRECT_ERROR_RESPONSE_MESSAGE = props.getProperty("scan.configure.correct_error_response_message");
            INVALID_ERROR_SUCCESS_RESPONSE_MESSAGE = props.getProperty("scan.configure.invalid_error_success_response_message");
            MISSING_ERROR_OBJECT_RESPONSE_MESSAGE = props.getProperty("scan.configure.missing_error_object_response_message");
            MISSING_ERROR_CODE_RESPONSE_MESSAGE = props.getProperty("scan.configure.missing_error_code_response_message");
            NO_ERROR_CODE_RESPONSE_MESSAGE = props.getProperty("scan.configure.no_error_code_response_message");
            MISSING_ERROR_MESSAGE_REPONSE_MESSAGE = props.getProperty("scan.configure.missing_error_message_response_message");
            NO_ERROR_MESSAGE_RESPONSE_MESSAGE = props.getProperty("scan.configure.no_error_message_response_message");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
