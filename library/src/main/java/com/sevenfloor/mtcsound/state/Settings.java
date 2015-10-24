package com.sevenfloor.mtcsound.state;

public class Settings {
    public final SubwooferOptions subwoofer = new SubwooferOptions();
    public final VolumeRange volumeRange = new VolumeRange();
    public boolean gpsAltMix = false;
    public boolean gpsOnTopEnable = true;

    public void setString(String value){
        String[] parts = value.split("\\|");
        if (parts.length < 1) return;
        subwoofer.setString(parts[0]);
        if (parts.length < 2) return;
        volumeRange.setString(parts[1]);
        if (parts.length < 3) return;
        gpsAltMix = Boolean.valueOf(parts[2]);
    }

    public String getString(){
        return String.format("%s|%s|%b", subwoofer.getString(), volumeRange.getString(), gpsAltMix);
    }
}

