/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Stephan Gatzka
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

package com.hbm.devices.configure;

import com.hbm.devices.scan.messages.IPv4EntryManual;
import com.hbm.devices.scan.messages.Interface.Method;
import com.hbm.devices.scan.messages.MissingDataException;

/**
 * The interface describes the properties and settings of an network interface which are configured.
 * 
 * @since 1.0
 *
 */
public class Interface {

    private String name;
    private IPv4EntryManual ipv4;
    private String configurationMethod;

    /**
     * This constructor is used to instantiate an {@link Interface} object.
     * <p>
     * Note: The parameter {@code configMethod} must not be {@link Method#MANUAL}. If you want to
     * set a manual ipv4 use the constructor
     * {@link #Interface(String, com.hbm.devices.scan.messages.Interface.Method, com.hbm.devices.scan.messages.IPv4EntryManual)}.
     * <p>
     * 
     * @param interfaceName
     *            this parameter specifies the interface
     * @param configMethod
     *            this parameter specifies the ip configuration method.
     * 
     */
    public Interface(String interfaceName, Method configMethod) {
        this(interfaceName, configMethod, null);
    }

    /**
     * This constructor is used to instantiate an {@link Interface} object.
     * 
     * @param interfaceName
     *            this parameter specifies the interface
     * @param configMethod
     *            this parameter specifies the ip configuration method.
     * @param ipv4
     *            this parameter specifies the ip address which is set to this interface
     */
    public Interface(String interfaceName, Method configMethod, IPv4EntryManual ipv4) {
        this.name = interfaceName;
        this.configurationMethod = configMethod.toString();
        this.ipv4 = ipv4;
    }

    /**
     * 
     * @return returns the name of the network interface
     */
    public String getInterfaceName() {
        return this.name;
    }

    /**
     * 
     * @return returns the {@link IPv4EntryManual}
     */
    public IPv4EntryManual getIPv4() {
        return this.ipv4;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(40);
        result.append("\t  name: ").append(name).append('\n');
        result.append("\t  method: ").append(configurationMethod).append('\n');
        result.append("\t  ip: ").append(ipv4).append('\n');
        return result.toString();
    }

    /**
     * This method checks the {@link Interface} object for errors and if it conforms to the HBM
     * network discovery and configuration protocol.
     * 
     * @param iface
     *            the {@link Interface} object, which should be checked for errors
     * @throws MissingDataException
     *          if some information required by the specification is not
     *          included  in {@code iface.}
     * @throws IllegalArgumentException
     *          if {@code iface} is null.
     */
    public static void checkForErrors(Interface iface) throws MissingDataException {
        if (iface == null) {
            throw new IllegalArgumentException("interface object must not be null");
        }

        if ((iface.name == null) || (iface.name.length() == 0)) {
            throw new MissingDataException("No name in Interface");
        }

        if (iface.configurationMethod == null) {
            throw new MissingDataException("No configuration method in Interface");
        }

        if (iface.configurationMethod.equals(Method.MANUAL.toString()) && iface.ipv4 == null) {
            throw new MissingDataException("No ipv4 in Interface");
        }
        if (iface.configurationMethod.equals(Method.MANUAL.toString())) {
            // only check if there has to be a ipv4
            IPv4EntryManual.checkForErrors(iface.ipv4);
        }
    }
}
