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

        // 0 to 10 is dB (x2), 11 = total mute
        device.state.backViewState.cut = i > 10 ? 100 : i * 2;
        device.applyState();

        return get();
    }

    @Override
    public String get() {
        return String.valueOf(device.state.backViewState.cut);
    }
}
