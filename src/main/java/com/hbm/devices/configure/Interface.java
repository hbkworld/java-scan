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
	private Method configurationMethod;

	/**
	 * This constructor is used to instantiate an {@link Interface} object.
	 * <p>
	 * Note: The parameter {@code configMethod} must not be {@link Method#manual}. If you want to
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
		this.configurationMethod = configMethod;
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

	/**
	 * 
	 * @return returns the configuration {@link Method}
	 */
	public Method getConfigurationMethod() {
		return this.configurationMethod;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
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
	 * @throws NullPointerException
	 */
	public static void checkForErrors(Interface iface) throws MissingDataException,
			NullPointerException {
		if (iface == null)
			throw new NullPointerException("interface object must not be null");

		if (iface.name == null) {
			throw new NullPointerException("No name in Interface");
		} else if (iface.name.length() == 0) {
			throw new MissingDataException("No name in Interface");
		}

		if (iface.configurationMethod == null) {
			throw new MissingDataException("No configuration method in Interface");
		}

		if (iface.configurationMethod.equals(Method.manual) && iface.ipv4 == null) {
			throw new MissingDataException("No ipv4 in Interface");
		}
		if (iface.configurationMethod.equals(Method.manual)) {
			// only check if there has to be a ipv4
			IPv4EntryManual.checkForErrors(iface.ipv4);
		}

	}
}
