package com.sevenfloor.mtcsound.service;

import android.content.Context;
import android.media.AudioSystem;
import android.os.Binder;
import android.os.RemoteException;

import com.sevenfloor.mtcsound.Device;

public class MtcSoundService extends IMtcSoundService.Stub {
    public static final String SERVICE_NAME = "mtcsound.service";

    private Context context;
    private Device device;

    public MtcSoundService(Context context) {
        this.context = context;
    }

    public void systemReady() {
        device = new Device(context);
    }

    @Override
    public String getParameters(String paramString) throws RemoteException {
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            String defaultValue = AudioSystem.getParameters(paramString);
            return device.getParameters(paramString, defaultValue);
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }

    @Override
    public void setParameters(String paramString) throws RemoteException {
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            paramString = device.setParameters(paramString);
            if (paramString != null) {
                AudioSystem.setParameters(paramString);
            }
        }
        finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }
}