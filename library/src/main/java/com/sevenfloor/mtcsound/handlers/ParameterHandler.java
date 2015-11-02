package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public abstract class ParameterHandler{
    protected final Device device;

    ParameterHandler(Device device) {
        this.device = device;
    }

    public abstract String getName();

    // returned value will be sent further to the MCU; return null to prevent it being sent
    public String set(String value) { return value; }

    // value is the value from MCU that may be overridden if needed
    public String get(String value) { return value; }
}

