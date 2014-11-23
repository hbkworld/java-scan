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

package com.hbm.devices.configure;

import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;
import com.hbm.devices.scan.messages.Configure;
import com.hbm.devices.scan.messages.MissingDataException;

/**
 * This class is used to parse {@link Configure} objects into a JSON-String.
 * <p>
 * This class reads {@link Configure} objects and parses them into a JSON-String. Invalid objects,
 * or {@link Configure} objects, that do not conform to the HBM network discovery and configuration
 * protocol are handled correctly: Users of this class will receive an Exception
 * 
 * @since 1.0
 */
public class ConfigParser extends Observable implements Observer, Noticeable {

    private Gson gson;

    private Noticeable noticeable;

    /**
     * The constructor which is used to instantiate a ConfigParser. You can also specify
     * {@link Noticeable} if you want to be notified, when an Exception.
     * 
     * @param noticeable
     *            The noticeable interface which is notified, when an Exception occur. (null is also
     *            possible)
     */
    public ConfigParser(Noticeable noticeable) {
        gson = new Gson();
        this.noticeable = noticeable;
    }

    /**
     * This method parses the configure object into a JSON string. It also checks the
     * {@link Configure} object for errors ({@link Configure#checkForErrors(Configure)}). If it does
     * not conform to the HBM network discovery and configuration protocol, an Exception is thrown
     * 
     * @param config
     *            the configuration, which should be parsed into a JSON string
     * @return the parsed JSON string
     * @throws MissingDataException
     *             is thrown, when the {@link Configure} object does not contain all the relevant
     *             information, which is required according to the specification
     * @throws NullPointerException
     *             is thrown, when the {@link Configure} object is null or a sub-object in the
     *             config parameter is null, which must not be null according to the specification
     */
    public String getJsonString(Configure config) throws MissingDataException {
        Configure.checkForErrors(config);
        return gson.toJson(config);
    }

    @Override
    public void update(Observable o, Object obj) {
        if (obj == null) {
            if (this.noticeable != null) {
                this.noticeable.onException(new NullPointerException(
                        "ConfigParser object must not be null"));
            }
            return;
        }
        if (obj instanceof Configure) {
            String parsedConfig;

            try {
                parsedConfig = getJsonString((Configure) obj);
                setChanged();
                notifyObservers(parsedConfig);
            } catch (Exception e) {
                if (this.noticeable != null) {
                    this.noticeable.onException(e);
                }
            }
        }
    }

    @Override
    public void onException(Exception e) {
        // forward the exception
        if (this.noticeable != null) {
            this.noticeable.onException(e);
        }
    }

}
