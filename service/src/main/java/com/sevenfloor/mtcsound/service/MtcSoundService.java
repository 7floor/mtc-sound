package com.sevenfloor.mtcsound.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioSystem;
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
    }

    public void initialize() {
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
        showToast(callerPackage + ": " + mediaCommandName(event));
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
    private static final int MEDIA_NOP = 0;
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_STARTED = 6;
    private static final int MEDIA_PAUSED = 7;
    private static final int MEDIA_STOPPED = 8;
    private static final int MEDIA_SKIPPED = 9;
    private static final int MEDIA_TIMED_TEXT = 99;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;
    private static final int MEDIA_SUBTITLE_DATA = 201;

    private String mediaCommandName(int value)
    {
        switch (value) {
            case MEDIA_NOP:
                return "MEDIA_NOP";
            case MEDIA_PREPARED:
                return "MEDIA_PREPARED";
            case MEDIA_PLAYBACK_COMPLETE:
                return "MEDIA_PLAYBACK_COMPLETE";
            case MEDIA_BUFFERING_UPDATE:
                return "MEDIA_BUFFERING_UPDATE";
            case MEDIA_SEEK_COMPLETE:
                return "MEDIA_SEEK_COMPLETE";
            case MEDIA_SET_VIDEO_SIZE:
                return "MEDIA_SET_VIDEO_SIZE";
            case MEDIA_STARTED:
                return "MEDIA_STARTED";
            case MEDIA_PAUSED:
                return "MEDIA_PAUSED";
            case MEDIA_STOPPED:
                return "MEDIA_STOPPED";
            case MEDIA_SKIPPED:
                return "MEDIA_SKIPPED";
            case MEDIA_TIMED_TEXT:
                return "MEDIA_TIMED_TEXT";
            case MEDIA_ERROR:
                return "MEDIA_ERROR";
            case MEDIA_INFO:
                return "MEDIA_INFO";
            case MEDIA_SUBTITLE_DATA:
                return "MEDIA_SUBTITLE_DATA";
            default:
                return "Unknown";
        }
    }
}