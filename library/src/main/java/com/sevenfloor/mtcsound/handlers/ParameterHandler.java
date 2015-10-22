package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class ParameterHandler{
    protected final Device device;

    ParameterHandler(Device device) {
        this.device = device;
    }

    public String set(String value) { return value; }
    public String get() { return null; }
}

