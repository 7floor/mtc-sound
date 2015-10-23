package com.sevenfloor.mtcsound.state;

import java.util.HashMap;
import java.util.Map;

public class DeviceState {

    public String HardwareStatus = "";

    public final Settings settings = new Settings();

    public final InputMode inputMode = new InputMode();

    public boolean mute;
    public final Volume volume = new Volume(settings.volumeRange);
    public final Volume phoneVolume = new Volume(settings.volumeRange);
    public final Balance balance = new Balance();
    public final Balance fader = new Balance();

    public final SoundProfile phoneProfile = new SoundProfile();
    public final SoundProfile sysProfile = new SoundProfile();
    public final SoundProfile btProfile = new SoundProfile();
    public final SoundProfile dvdProfile = new SoundProfile();
    public final SoundProfile dtvProfile = new SoundProfile();
    public final SoundProfile lineProfile = new SoundProfile();
    public final SoundProfile fmProfile = new SoundProfile();
    public final SoundProfile ipodProfile = new SoundProfile();
    public final SoundProfile dvrProfile = new SoundProfile();

    public final Map<Input, SoundProfile> allProfiles = new HashMap<>();

    public DeviceState() {
        allProfiles.put(Input.sys, sysProfile);
        allProfiles.put(Input.gsm_bt, btProfile);
        allProfiles.put(Input.dvd, dvdProfile);
        allProfiles.put(Input.dtv, dtvProfile);
        allProfiles.put(Input.line, lineProfile);
        allProfiles.put(Input.fm, fmProfile);
        allProfiles.put(Input.ipod, ipodProfile);
        allProfiles.put(Input.dvr, dvrProfile);
    }

    public SoundProfile getCurrentProfile() {
        if (inputMode.phoneState == PhoneState.answer)
            return phoneProfile;

        return allProfiles.get(inputMode.input);
    }

    public Volume getCurrentVolume() {
        return inputMode.phoneState == PhoneState.answer
                ? phoneVolume
                : volume;
    }
}
