package com.sevenfloor.mtcsound;

import android.content.Context;
import android.content.Intent;

import com.sevenfloor.mtcsound.handlers.*;
import com.sevenfloor.mtcsound.state.DeviceState;

import java.util.HashMap;
import java.util.Map;

public class Device {
    private Context context;
    private final Object lock = new Object();
    private final Map<String, ParameterHandler> handlers = new HashMap<>();
    private final HwInterface hardware = new HwInterface();
    private final Persister persister = new Persister();
    private boolean stateLoaded = false;
    private boolean i2cMode;

    public final DeviceState state = new DeviceState();

    public Device(Context context) {
        this.context = context;

        handlers.put("av_control_mode", new ControlModeHandler(this));


        i2cMode = checkHardware();
        if (!i2cMode) return;
        // listen to power, to reapply state
        // on some devices, maybe 3066 it is known to lose system input after return from sleep
        handlers.put("rpt_power", new PowerHandler(this));

        // inputs
        handlers.put("av_channel_enter", new ChannelEnterHandler(this));
        handlers.put("av_channel_exit", new ChannelExitHandler(this));
        handlers.put("av_channel", new ChannelQueryHandler(this));
        handlers.put("av_phone", new PhoneHandler(this));

        // globals
        handlers.put("av_mute", new MuteHandler(this));
        handlers.put("av_volume", new VolumeHandler(this));
        handlers.put("av_phone_volume", new PhoneVolumeHandler(this));
        handlers.put("av_balance", new BalanceHandler(this));

        // profile-specific
        handlers.put("av_gain", new InputGainHandler(this));

        handlers.put("av_eq_on", new EqualizerOnHandler(this));
        handlers.put("av_eq_bass", new BassHandler(this));
        handlers.put("av_eq_middle", new MiddleHandler(this));
        handlers.put("av_eq_treble", new TrebleHandler(this));

        handlers.put("av_lud", new LoudnessOnHandler(this)); // because this is an existing name supported by MTCManager (for LOUD hardware button)
        handlers.put("av_loudness", new LoudnessHandler(this));

        // configuration
        handlers.put("cfg_maxvolume", new GetMaxVolumeHandler(this));
        handlers.put("cfg_volumerange", new VolumeRangeHandler(this));
        handlers.put("cfg_subwoofer", new SubwooferHandler(this));
        handlers.put("cfg_gps_altmix", new GpsAltMixHandler(this));

        // gps alt mix support
        handlers.put("av_gps_package", new GpsPackageHandler(this));

        // reject
        ParameterHandler nullHandler = new NullHandler(this);
        handlers.put("av_eq", nullHandler);
    }

    public String getParameters(String keyValue, String defaultValue) {
        String[] parts = Utils.splitKeyValue(keyValue);
        if (parts == null) return defaultValue;
        String key = parts[0];
        String value = null;

        ParameterHandler handler = handlers.get(key);
        if (handler != null) {
            synchronized (lock) {
                checkStateLoaded();
                value = handler.get();
            }
        }

        if (value == null) return defaultValue;

        return value;
    }

    public String setParameters(String keyValue) {
        String[] parts = Utils.splitKeyValue(keyValue);
        if (parts == null) return keyValue;

        String key = parts[0];
        String value = parts[1];

        ParameterHandler handler = handlers.get(key);
        if (handler != null) {
            synchronized (lock) {
                checkStateLoaded();
                value = handler.set(value);
            }
        }

        if (value == null) return null;

        return String.format("%s=%s", key, value);
    }

    public void applyState() {
        applyState(false);
    }

    public void applyState(boolean forced) {
        if (i2cMode)
            hardware.applyState(state, forced);
        persister.writeState(context, state);
    }

    public void notifyInputChange(){
        context.sendBroadcast(new Intent("com.microntek.inputchange"));
    }

    private void checkStateLoaded() {
        if (!stateLoaded)
        {
            persister.readState(context, state);
            if (i2cMode)
                hardware.applyState(state, true);
            stateLoaded = true;
        }
    }

    private boolean checkHardware() {
        state.HardwareStatus = hardware.CheckHardware();
        return state.HardwareStatus.startsWith("i2c");
    }
}