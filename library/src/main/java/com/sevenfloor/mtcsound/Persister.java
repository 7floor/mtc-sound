package com.sevenfloor.mtcsound;

import android.content.ContentResolver;
import android.content.Context;

import com.sevenfloor.mtcsound.state.DeviceState;
import com.sevenfloor.mtcsound.state.Input;
import com.sevenfloor.mtcsound.state.SoundProfile;

import java.util.HashMap;
import java.util.Map;

public class Persister {
    private static final String settingsKey = "cfg_sound_settings";
    private static final String profileKey = "cfg_sound_profile_%s";
    private static final String phoneProfileName = "phone";

    private String lastSettings;
    private final Map<String, String> lastProfile = new HashMap<>();

    public void readState(Context context, DeviceState state) {
        ContentResolver resolver = context.getContentResolver();
        lastSettings = android.provider.Settings.System.getString(resolver, settingsKey);
        if (lastSettings == null) lastSettings = "";
        state.settings.setString(lastSettings);

        readProfile(resolver, state.phoneProfile, phoneProfileName);
        for (Map.Entry<Input, SoundProfile> entry : state.allProfiles.entrySet()) {
            readProfile(resolver, entry.getValue(), entry.getKey().toString());
        }
    }

    public void writeState(Context context, DeviceState state) {
        ContentResolver resolver = context.getContentResolver();

        String settings = state.settings.getString();
        if (!settings.equals(lastSettings)) {
            lastSettings = settings;
            android.provider.Settings.System.putString(resolver, settingsKey, settings);
        }

        writeProfile(resolver, state.phoneProfile, phoneProfileName);
        for (Map.Entry<Input, SoundProfile> entry : state.allProfiles.entrySet()) {
            writeProfile(resolver, entry.getValue(), entry.getKey().toString());

            // update legacy av_lud setting, otherwise loudness will be off-sync with MTCManager
            if (entry.getKey() == state.inputMode.input) {
                int actualLud = entry.getValue().loudnessOn ? 1 : 0;
                android.provider.Settings.System.putInt(resolver, "av_lud", actualLud);
            }
        }

        // update legacy settings, otherwise they will be off-sync with MTCManager
        android.provider.Settings.System.putString(resolver, "KeyBalance", String.format("%d,%d", state.balance.getValue() + 14, 14 - state.fader.getValue()));
        android.provider.Settings.System.putString(resolver, "KeyBalanceMode", "0");
    }

    private void readProfile(ContentResolver resolver, SoundProfile profile, String name) {
        String setting = android.provider.Settings.System.getString(resolver, String.format(profileKey, name));
        if (setting == null) setting = "";
        lastProfile.put(name, setting);
        profile.setString(setting);
    }

    private void writeProfile(ContentResolver resolver, SoundProfile profile, String name) {
        String setting = profile.getString();
        if (!setting.equals(lastProfile.get(name))) {
            lastProfile.put(name, setting);
            android.provider.Settings.System.putString(resolver, String.format(profileKey, name), setting);
        }
    }
}
