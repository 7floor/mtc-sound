package com.sevenfloor.mtcsound.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;
import android.widget.Toast;

import com.sevenfloor.mtcsound.Device;

public class MtcSoundService extends IMtcSoundService.Stub {
    public static final String SERVICE_NAME = "mtcsound.service";

    private Context context;
    private Device device;
    private Handler handler;

    public MtcSoundService(Context context) {
        this.context = context;
        this.handler = new Handler();
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

    @Override
    public void onMediaPlayerEvent(String callerPackage, int event) throws RemoteException {
        device.onMediaPlayerEvent(callerPackage, event);
        //showToast(callerPackage + ": MediaPlayer " + mediaCommandName(event));
    }

    @Override
    public void onAudioTrackEvent(String callerPackage, int event) throws RemoteException {
        device.onAudioTrackEvent(callerPackage, event);
        //showToast(callerPackage + ": AudioTrack " + audioTrackStateName(event));
    }

    @Override
    public void onRecording(String callerPackage, boolean active) throws RemoteException {
        device.onRecording(active);
        //showToast(callerPackage + " is " + (active ? "" : "not") + " recording");
    }

    @Override
    public void showToast(String text) throws RemoteException {
        final String t = text;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // debug code ---------------------------------------------------------------------------------

    private String mediaCommandName(int value)
    {
        switch (value) {
            case Device.MEDIA_PLAYBACK_COMPLETE:
                return "MEDIA_PLAYBACK_COMPLETE";
            case Device.MEDIA_STARTED:
                return "MEDIA_STARTED";
            case Device.MEDIA_PAUSED:
                return "MEDIA_PAUSED";
            case Device.MEDIA_STOPPED:
                return "MEDIA_STOPPED";
            case Device.MEDIA_ERROR:
                return "MEDIA_ERROR";
            default:
                return "Unknown";
        }
    }

    private String audioTrackStateName(int value)
    {
        switch (value) {
            case AudioTrack.PLAYSTATE_PLAYING:
                return "PLAYSTATE_PLAYING";
            case AudioTrack.PLAYSTATE_PAUSED:
                return "PLAYSTATE_PAUSED";
            case AudioTrack.PLAYSTATE_STOPPED:
                return "PLAYSTATE_STOPPED";
            default:
                return "Unknown";
        }
    }
}