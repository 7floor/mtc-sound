package com.sevenfloor.mtcsound.state;

import com.sevenfloor.mtcsound.Utils;

public class SoundProfile {
    private int inputGain = 0;

    public void setInputGain(int value) {
        inputGain = Utils.adjustInt(value, 0, 20);
    }

    public int getInputGain() {
        return inputGain;
    }

    public boolean equalizerOn = false;
    public boolean loudnessOn = false;
    public EqualizerBand bassBand = new EqualizerBand(-20, +20, 4, 4);
    public EqualizerBand middleBand = new EqualizerBand(-20, +20, 4, 4);
    public EqualizerBand trebleBand = new EqualizerBand(-20, +20, 4, 2);
    public Loudness loudness = new Loudness();

    public void setString(String value) {
        String[] parts = value.split("\\|");

        if (parts.length < 1) return;
        Integer v = Utils.stringToInt(parts[0]);
        if (v != null) setInputGain(v);

        if (parts.length < 2) return;
        equalizerOn = Boolean.valueOf(parts[1]);

        if (parts.length < 3) return;
        loudnessOn = Boolean.valueOf(parts[2]);

        if (parts.length < 4) return;
        bassBand.setString(parts[3]);

        if (parts.length < 5) return;
        middleBand.setString(parts[4]);

        if (parts.length < 6) return;
        trebleBand.setString(parts[5]);

        if (parts.length < 7) return;
        loudness.setString(parts[6]);
    }

    public String getString() {
        return String.format("%d|%b|%b|%s|%s|%s|%s", inputGain, equalizerOn, loudnessOn, bassBand.getString(), middleBand.getString(), trebleBand.getString(), loudness.getString());
    }
}
