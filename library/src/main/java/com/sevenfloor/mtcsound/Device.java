package com.sevenfloor.mtcsound;

import android.content.Context;
import android.content.Intent;
import android.media.AudioTrack;
import android.os.Handler;
import android.widget.Toast;

import com.sevenfloor.mtcsound.handlers.*;
import com.sevenfloor.mtcsound.state.DeviceState;

import java.util.HashMap;
import java.util.Map;

public class Device {

    public static final int MEDIA_PLAYBACK_COMPLETE = 2;
    public static final int MEDIA_STARTED = 6;
    public static final int MEDIA_PAUSED = 7;
    public static final int MEDIA_STOPPED = 8;
    public static final int MEDIA_ERROR = 100;

    private Context context;
    private Handler handler = new Handler();
    private final Object lock = new Object();
    public final DeviceState state = new DeviceState();
    private final Map<String, ParameterHandler> handlers = new HashMap<>();
    private final Persister persister = new Persister();
    private HwInterface hardware = null;
    private boolean stateLoaded = false;

    public Device(Context context) {
        this.context = context;

        addHandler(new ControlModeHandler(this));

        addHandler(new PowerHandler(this));

        // inputs
        addHandler(new ChannelEnterHandler(this));
        addHandler(new ChannelExitHandler(this));
        addHandler(new ChannelQueryHandler(this));
        addHandler(new PhoneHandler(this));

        // globals: mute, volume, balance
        addHandler(new MuteHandler(this));
        addHandler(new VolumeHandler(this));
        addHandler(new PhoneVolumeHandler(this));
        addHandler(new BalanceHandler(this));

        // profile-specific: preamp, eq, loudness
        addHandler(new InputGainHandler(this));
        addHandler(new EqualizerOnHandler(this));
        addHandler(new BassHandler(this));
        addHandler(new MiddleHandler(this));
        addHandler(new TrebleHandler(this));
        addHandler(new LoudnessOnHandler(this));
        addHandler(new LoudnessHandler(this));

        // gps mix support
        addHandler(new GpsPackageHandler(this));
        addHandler(new GpsMonitorHandler(this));
        addHandler(new GpsSwitchHandler(this));
        addHandler(new GpsGainHandler(this));
        addHandler(new GpsOnTopHandler(this));

        // back view mix/mute support
        addHandler(new BackViewHandler(this));
        addHandler(new BackViewVolumeHandler(this));

        // configuration
        addHandler(new VolumeRangeHandler(this));
        addHandler(new SubwooferHandler(this));
        addHandler(new PhoneOutHandler(this));
        addHandler(new GpsAltMixHandler(this));
        addHandler(new GsmAltInputHandler(this));
        addHandler(new RecMuteHandler(this));

        init();
    }

    public String getParameters(String keyValue, String defaultValue) {
        String[] parts = Utils.splitKeyValue(keyValue);
        if (parts == null) return defaultValue;
        String key = parts[0];

        ParameterHandler handler = handlers.get(key);
        if (handler != null) {
            synchronized (lock) {
                checkStateLoaded();
                defaultValue = handler.get(defaultValue);
            }
        }

        return defaultValue;
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

    public void onMediaPlayerEvent(String callerPackage, int event) {
        if (!shouldCheckPackageSound(callerPackage))
            return;
        boolean aloud = state.gpsState.gpsIsAloud;
        switch (event)
        {
            case MEDIA_STARTED:
                aloud = true;
                break;
            case MEDIA_PAUSED:
            case MEDIA_STOPPED:
            case MEDIA_PLAYBACK_COMPLETE:
            case MEDIA_ERROR:
                aloud = false;
        }
        //showToast(String.format("%b -> %b", state.gpsState.gpsIsAloud, aloud));
        if (state.gpsState.gpsIsAloud == aloud)
            return;
        state.gpsState.gpsIsAloud = aloud;
        applyState();
    }

    public void onAudioTrackEvent(String callerPackage, int event) {
        if (!shouldCheckPackageSound(callerPackage))
            return;
        boolean aloud = state.gpsState.gpsIsAloud;
        switch (event)
        {
            case AudioTrack.PLAYSTATE_PLAYING:
                aloud = true;
                break;
            case AudioTrack.PLAYSTATE_PAUSED:
            case AudioTrack.PLAYSTATE_STOPPED:
                aloud = false;
        }
        //showToast(String.format("%b -> %b", state.gpsState.gpsIsAloud, aloud));
        if (state.gpsState.gpsIsAloud == aloud)
            return;
        state.gpsState.gpsIsAloud = aloud;
        applyState();
    }

    public void onRecording(boolean active) {
        state.recActive = active;
        applyState();
    }

    public void checkHardware() {
        if (hardware == null) return;
        hardware.check();
        setModeAndStatus(hardware.isOnline(), hardware.getStateDescription());
    }

    public void applyState() {
        applyState(false);
    }

    public void applyState(boolean forced) {
        persister.writeState(context, state);
        if (hardware == null) return;
        hardware.applyState(state, forced);
        setModeAndStatus(hardware.isOnline(), hardware.getStateDescription());
    }

    public void notifyInputChange(){
        context.sendBroadcast(new Intent("com.microntek.inputchange"));
    }

    private void init() {
        String message = SoftwareChecker.check();
        if (message != null) {
            setModeAndStatus(false, message);
            return;
        }

        hardware = new HwInterface(SoftwareChecker.getFileNames());
        setModeAndStatus(hardware.isOnline(), hardware.getStateDescription());
    }

    private void checkStateLoaded() {
        if (stateLoaded) return;
        persister.readState(context, state);
        stateLoaded = true;
        if (hardware == null) return;
        hardware.applyState(state, true);
        setModeAndStatus(hardware.isOnline(), hardware.getStateDescription());
    }

    private void setModeAndStatus(boolean i2cMode, String status)
    {
        state.ModeAndStatus =  String.format("%s, %s", i2cMode ? "i2c" : "mcu", status);
    }

    private void addHandler(ParameterHandler handler){
        addHandler(handler, handler.getName());
    }

    private void addHandler(ParameterHandler handler, String name){
        handlers.put(name, handler);
    }

    private boolean shouldCheckPackageSound(String callerPackage) {
        return state.gpsState.gpsMonitor && state.gpsState.gpsPackage.equals(callerPackage);
    }

    public void showToast(String text) {
        final String t = text;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, t, Toast.LENGTH_SHORT).show();
            }
        });
    }
}