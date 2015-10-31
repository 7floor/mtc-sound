package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class BackViewVolumeHandler extends ParameterHandler {
    public BackViewVolumeHandler(Device device) {
        super(device);
    }

    @Override
    public String set(String value) {
        Integer i = Utils.stringToInt(value);
        if (i == null) i = 0;

        device.state.backViewState.cut = i;
        device.applyState();

        return get();
    }

    @Override
    public String get() {
        return String.valueOf(device.state.backViewState.cut);
    }
}
