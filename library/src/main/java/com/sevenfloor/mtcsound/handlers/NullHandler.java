package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class NullHandler extends ParameterHandler {
    public NullHandler(Device device) {
        super(device);
    }

    private String value;

    @Override
    public String set(String value) {
        this.value = value;
        return null;
    }

    @Override
    public String get() {
        return this.value;
    }
}

