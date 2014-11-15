package com.hbm.devices.scan.messages;

public class Announce extends JsonRpc {

    private Announce() {
        super("announce");
    }

    public AnnounceParams getParams() {
        return params;
    }

    private AnnounceParams params;

    @Override
    public String toString() {
        return params.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Announce)) {
            return false;
        }
        Announce rhs = (Announce)o;
        return this.getJSONString().equals(rhs.getJSONString());
    }

    @Override
    public int hashCode() {
        return getJSONString().hashCode();
    }
}
