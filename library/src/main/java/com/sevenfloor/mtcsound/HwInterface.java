package com.sevenfloor.mtcsound;

import com.sevenfloor.mtcsound.state.DeviceState;
import com.sevenfloor.mtcsound.state.EqualizerBand;
import com.sevenfloor.mtcsound.state.Input;
import com.sevenfloor.mtcsound.state.InputMode;
import com.sevenfloor.mtcsound.state.PhoneState;
import com.sevenfloor.mtcsound.state.SoundProfile;

import java.io.File;
import java.util.ArrayList;

public class HwInterface {

    private final Register R01 = new Register(0x01, 0b10100100);
    private final Register R02 = new Register(0x02, 0b00000000);
    private final Register R03 = new Register(0x03, 0b00000001);
    private final Register R05 = new Register(0x05, 0b10000000);
    private final Register R06 = new Register(0x06, 0b00000000);
    private final Register R20 = new Register(0x20, 0b11111111);
    private final Register R28 = new Register(0x28, 0b11111111);
    private final Register R29 = new Register(0x29, 0b11111111);
    private final Register R2A = new Register(0x2A, 0b11111111);
    private final Register R2B = new Register(0x2B, 0b11111111);
    private final Register R2C = new Register(0x2C, 0b11111111);
    private final Register R30 = new Register(0x30, 0b11111111);
    private final Register R41 = new Register(0x41, 0b00000000);
    private final Register R44 = new Register(0x44, 0b00000000);
    private final Register R47 = new Register(0x47, 0b00000000);
    private final Register R51 = new Register(0x51, 0b10000000);
    private final Register R54 = new Register(0x54, 0b10000000);
    private final Register R57 = new Register(0x57, 0b10000000);
    private final Register R75 = new Register(0x75, 0b00000000);

    private final Register AdvancedSwitch = R01;
    private final Register SubwooferSetup = R02;
    private final Register LoudnessFrequency = R03;
    private final Register InputSelector = R05;
    private final Register InputGain = R06;
    private final Register VolumeGain = R20;
    private final Register FaderFrontRight = R28;
    private final Register FaderFrontLeft = R29;
    private final Register FaderRearRight = R2A;
    private final Register FaderRearLeft = R2B;
    private final Register FaderSubwoofer = R2C;
    private final Register MixingGain = R30;
    private final Register EqBassSetup = R41;
    private final Register EqMiddleSetup = R44;
    private final Register EqTrebleSetup = R47;
    private final Register EqBassGain = R51;
    private final Register EqMiddleGain = R54;
    private final Register EqTrebleGain = R57;
    private final Register LoudnessGainHiCut = R75;

    private final Register[] AllRegisters = new Register[] { R01, R02, R03, R05, R06, R20, R28, R29, R2A, R2B, R2C, R30, R41, R44, R47, R51, R54, R57, R75 };

    public String CheckHardware() {
        if (!checkDevI2cFile(0)) {
            tryLoadDriver();
        }
        if (!checkDevI2cFile(0)) {
            return "mcu,No device driver i2c-dev";
        }

        String lastError = "";
        for(int i = 0; i < 10; i++)
        {
            if (!checkDevI2cFile(i)) continue; // don't check files that don't exist

            if (I2cBus.getDevFileAccess(i) != 0) {
                lastError = "No access to /dev/i2c-?; " + I2cBus.lastError;
                continue;
            }

            int errno = I2cBus.write(0x40,new byte[][]{{0x01}});
            if (errno == 0) return "i2c,Channel " + i;
            lastError = I2cBus.lastError;
            if ((errno == 11 || errno == 110) && lastError.contains("write")) // EAGAIN (3188) || ETIMEOUT (3066)
                lastError = lastError + " (no response from i2c slave)";
        }
        return "mcu," + lastError;
    }

    public void applyState(DeviceState state, boolean forced) {
        applySettings(state);

        applyMixingGain(state);
        applyVolume(state);
        applyBalanceAndFader(state);

        SoundProfile profile = state.getCurrentProfile();
        applyInputGain(profile);
        applyEqualizerBand(profile.equalizerOn, profile.bassBand, EqBassSetup, EqBassGain);
        applyEqualizerBand(profile.equalizerOn, profile.middleBand, EqMiddleSetup, EqMiddleGain);
        applyEqualizerBand(profile.equalizerOn, profile.trebleBand, EqTrebleSetup, EqTrebleGain);
        applyLoudness(profile.loudnessOn, profile);

        applyInput(state);
        applyMute(state);

        writeRegistersToI2C(forced);
    }

    private void applySettings(DeviceState state) {
        AdvancedSwitch.value = 0b10110111; // hard coded to maximum switch times
        int subCut = state.settings.subwoofer.getCutFrequency();
        int subOut = state.settings.subwoofer.getOutput();
        int subPhase= state.settings.subwoofer.getPhase();
        SubwooferSetup.value = (subPhase << 7) | (subOut << 4) | (subCut);
        int db = state.settings.subwoofer.getGain();
        FaderSubwoofer.value = 128 - db;
    }

    private void applyMixingGain(DeviceState state) {
        // don't mix with the phone
        if (state.isPhone()) {
            MixingGain.value = 0xFF;
            return;
        }
        // don't mix with itself or gsm_bt (it's the same hw input)
        if (state.inputMode.input == Input.sys || state.inputMode.input == Input.gsm_bt) {
            MixingGain.value = 0xFF;
            return;
        }
        int vol = state.volume.getValueInDb();
        int sysGain = state.sysProfile.getInputGain();
        int cut = state.backViewState.getActualCut();
        int result = vol + sysGain - cut;
        if (result > 7) result = 7;
        MixingGain.value = result < -79 ? 0xFF : 128 - result;
    }

    private void applyInput(DeviceState state) {
        if (state.isPhone()) {
            if (state.inputMode.phoneState == PhoneState.in) // incoming ring using system input
                InputSelector.value = 0x81;
            else // call in progress or outgoing connection tone using bluetooth input
                InputSelector.value = 0x80;
            // only frontal speakers should be active
            FaderRearLeft.value = 0xFF;
            FaderRearRight.value = 0xFF;
            FaderSubwoofer.value = 0xFF;
            MixingGain.value = 0xFF;
            return;
        }

        switch (state.inputMode.input)
        {
            case sys:
            case gsm_bt:
                InputSelector.value = 0x81;
                break;
            case dtv:
            case dvd:
                InputSelector.value = 0x82;
                break;
            case dvr: // ? need to check with the logic analyzer
            case line:
                InputSelector.value = 0x83;
                break;
            case fm:
                InputSelector.value = 0x8A;
                break;
            case ipod:
                InputSelector.value = 0x8B;
                break;
        }
    }

    private void applyInputGain(SoundProfile profile){
        InputGain.value = profile.getInputGain();
    }

    private void applyVolume(DeviceState state) {
        int db = state.getCurrentVolume().getValueInDb();

        int cut = 0;

        // cut only if no phone operations in progress
        if (!state.isPhone()) {
            cut = state.backViewState.getActualCut();
            // cut for gps only when the inputs are not sys nor gsm_bt since they're already cut by stock native code
            if (state.inputMode.input != Input.sys && state.inputMode.input != Input.gsm_bt)
                cut = cut + state.gpsState.getActualCut();
        }

        int result = db - cut;
        if (result > 15) result = 15;
        VolumeGain.value = result < -79 ? 0xFF : 128 - result;
    }

    private void applyBalanceAndFader(DeviceState state){
        int dbfl = 0, dbfr = 0, dbrl = 0, dbrr = 0, value;

        value = state.balance.getValue();
        if (value != 0) {
            int db = state.balance.getAttenuationInDB();
            if (value > 0) {
                dbfl -= db;
                dbrl -= db;
            } else {
                dbfr -= db;
                dbrr -= db;
            }
        }

        value = state.fader.getValue();
        if (value != 0) {
            int db = state.fader.getAttenuationInDB();
            if (value > 0) {
                dbrl -= db;
                dbrr -= db;
            } else {
                dbfl -= db;
                dbfr -= db;
            }
        }

        FaderFrontLeft.value = 128 - dbfl;
        FaderFrontRight.value = 128 - dbfr;
        FaderRearLeft.value = 128 - dbrl;
        FaderRearRight.value = 128 - dbrr;
    }

    private void applyEqualizerBand(boolean on, EqualizerBand band, Register rSetup, Register rGain) {
        int g = 0, f = 0, q = 0;
        if (on) {
            g = band.getGain();
            f = band.getFrequency();
            q = band.getQuality();
        }
        rSetup.value &= 0b11001100;
        rSetup.value |= (q) | (f << 4);
        if (g < 0) g = (-g) | 0b10000000;
        rGain.value = g;
    }

    private void applyLoudness(boolean on, SoundProfile profile) {
        int g = 0, f = 0, c = 0;
        if (on) {
            g = profile.loudness.getGain();
            f = profile.loudness.getFrequency();
            c = profile.loudness.getHicut();
        }
        LoudnessFrequency.value &= 0b11100111;
        LoudnessFrequency.value |= (f << 3);
        LoudnessGainHiCut.value &= 0b10000000;
        LoudnessGainHiCut.value |= g | (c << 5);
    }

    private void applyMute(DeviceState state) {
        if (state.mute || state.getCurrentVolume().getValue() == 0) {
            InputGain.value = InputGain.value | (1 << 7);
            VolumeGain.value = 0xFF;
            FaderFrontRight.value = 0xFF;
            FaderFrontLeft.value = 0xFF;
            FaderRearRight.value = 0xFF;
            FaderRearLeft.value = 0xFF;
            FaderSubwoofer.value = 0xFF;
            MixingGain.value = 0xFF;
        }
    }

    private void writeRegistersToI2C(boolean forced) {
        ArrayList<byte[]> buffer = new ArrayList<>(AllRegisters.length);
        for (Register r: AllRegisters) {
            if (forced || r.isChanged()) {
                buffer.add(new byte[]{(byte) r.index, (byte) r.value});
                r.flush();
            }
        }
        I2cBus.write(0x40, buffer.toArray(new byte[][]{}));
    }

    private static boolean checkDevI2cFile(int channel) {
        File f = new File("/dev/i2c-" + channel);
        return f.exists();
    }

    private static void tryLoadDriver() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "insmod /system/lib/modules/i2c-dev.ko"});
            process.waitFor();
        } catch (Throwable ignored) {}
    }}

class Register {
    int index;
    int lastValue;
    int value;

    public Register(int index, int defaultValue){
        this.index = index;
        this.value = defaultValue;
        this.lastValue = -1; // so that first time it will be written forcibly
    }

    public boolean isChanged() {
        return lastValue != value;
    }

    public void flush(){
        lastValue = value;
    }

}
