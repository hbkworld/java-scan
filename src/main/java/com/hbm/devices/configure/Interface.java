package com.hbm.devices.configure;

import com.hbm.devices.scan.MissingDataException;
import com.hbm.devices.scan.messages.IPv4EntryManual;
import com.hbm.devices.scan.messages.Interface.Method;

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
        StringBuilder result = new StringBuilder();
        result.append("\t  name: " + name + "\n");
        result.append("\t  method: " + configurationMethod + "\n");
        result.append("\t  ip: " + ipv4 + "\n");
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
     * @throws NullPointerException
     *          if {@code iface} is null.
     */
    public static void checkForErrors(Interface iface) throws MissingDataException {
        if (iface == null) {
            throw new NullPointerException("interface object must not be null");
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
