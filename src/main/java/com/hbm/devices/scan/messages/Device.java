package com.hbm.devices.scan.messages;

public class Device {

	private Device() {
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getFamilyType() {
		return familyType;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public boolean isRouter() {
		return isRouter;
	}

	@Override
	public String toString() {
		return "Device:\n" + 
		"\tUUID: " + uuid + "\n" +
		"\tname: " + name + "\n" +
		"\tfamily: " + familyType + "\n" +
		"\ttype: " + type + "\n" +
		"\tfirmware version: " + firmwareVersion + "\n" +
		"\tisRouter: " + isRouter + "\n";
	}

	private String uuid;
	private String name;
	private String type;
	private String familyType;
	private String firmwareVersion;
	private boolean isRouter = false;
}

