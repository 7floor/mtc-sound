package com.sevenfloor.mtcsound.state;

public class GpsState {
    public String gpsPackage = "";
    public boolean gpsMonitor = false;
    public boolean gpsSwitch = false;
    public boolean gpsOnTop = false;
    public int gpsGain = 0;
    public boolean gpsIsAloud = false;

    public int getActualCut() {
        if (gpsMonitor) {
            if (gpsIsAloud)
                return gpsSwitch ? 100 : 10;
        } else {
            return gpsOnTop ? 100 : 0;
        }

        return 0;
    }
}

