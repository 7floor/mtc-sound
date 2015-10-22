package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;

public class GetMaxVolumeHandler extends ParameterHandler {
    public GetMaxVolumeHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        return null;
    }

    @Override
    public String get() {
        return String.valueOf(30);
    }
}
