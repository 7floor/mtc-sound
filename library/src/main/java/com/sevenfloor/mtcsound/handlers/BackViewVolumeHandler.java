package com.sevenfloor.mtcsound.handlers;

import com.sevenfloor.mtcsound.Device;
import com.sevenfloor.mtcsound.Utils;

public class BackViewVolumeHandler extends ParameterHandler {
    public BackViewVolumeHandler(Device device) {
        super(device);
    }

    @Override
    public String getName() {
        return "ctl_backview_vol"; // stock
    }

    @Override
    public String set(String value) {
        Integer i = Utils.stringToInt(value);
        if (i == null) return value;
        // 0 to 10 is dB (x2), 11 = total mute
        device.state.backViewState.cut = i > 10 ? 100 : i * 2;
        device.applyState();
        return value;
    }

    @Override
    public String get(String value) {
        return String.valueOf(device.state.backViewState.cut);
    }
}
