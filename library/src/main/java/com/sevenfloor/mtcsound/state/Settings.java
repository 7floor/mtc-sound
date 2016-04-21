package com.sevenfloor.mtcsound.state;

public class Settings {
    public final SubwooferOptions subwoofer = new SubwooferOptions();
    public final VolumeRange volumeRange = new VolumeRange();
    public boolean gpsAltMix = false;
    public boolean gsmAltInput = false;
    public boolean recMute = false;
    public PhoneOut phoneOut = new PhoneOut();

    public void setString(String value){
        String[] parts = value.split("\\|");
        if (parts.length < 1) return;
        subwoofer.setString(parts[0]);
        if (parts.length < 2) return;
        volumeRange.setString(parts[1]);
        if (parts.length < 3) return;
        gpsAltMix = Boolean.valueOf(parts[2]);
        if (parts.length < 4) return;
        phoneOut.setString(parts[3]);
        if (parts.length < 5) return;
        gsmAltInput = Boolean.valueOf(parts[4]);
        if (parts.length < 6) return;
        recMute = Boolean.valueOf(parts[5]);
    }

    public String getString(){
        return String.format("%s|%s|%b|%s|%b|%b", subwoofer.getString(), volumeRange.getString(), gpsAltMix, phoneOut.getString(), gsmAltInput, recMute);
    }
}

