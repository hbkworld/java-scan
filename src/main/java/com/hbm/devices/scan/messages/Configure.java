package com.hbm.devices.scan.messages;

/**
 * Network configuration request datagrams are dedicated requests, so only the device addressed by 
 * request/configure/device/uuid must answer with a network configuration response datagram.
 */
public class Configure extends JsonRpc {

	private Configure() {
		super("configure");
	}

   /**
    * @param id     A value of any type, which is used to match the response with the request that it is replying to.
    */
	public Configure(String id) {
        this();
        this.id = id;
    }
	
	public ConfigureParams getParams() {
		return params;
	}

	private ConfigureParams params;

	@Override
	public String toString() {
		return params.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Configure)) {
			return false;
		}
		Configure rhs = (Configure)o;
		return this.getJSONString().equals(rhs.getJSONString());
	}
	
	public String getId() {
        return id;
    }

    private String id;
}
