package com.sevenfloor.mtcsound.xposed;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.android.server.am.ActivityManagerService;
import com.sevenfloor.mtcsound.BuildConfig;
import com.sevenfloor.mtcsound.service.IMtcSoundService;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaSyncEvent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.sevenfloor.mtcsound.service.MtcSoundService;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Module implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private static IMtcSoundService service;
    private static String packageName = "";

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

        XposedBridge.hookAllMethods(ActivityManagerService.class, "main",
                new XC_MethodHook() {
                    @Override
                    protected final void afterHookedMethod(final XC_MethodHook.MethodHookParam param) {
                        try {
                            Context context = (Context) param.getResult();
                            MtcSoundService serviceInstance = new MtcSoundService(context);
                            ServiceManager.addService(MtcSoundService.SERVICE_NAME, serviceInstance);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        XposedBridge.hookAllMethods(ActivityManagerService.class, "systemReady",
                new XC_MethodHook() {
                    @Override
                    protected final void afterHookedMethod(final MethodHookParam param) {
                        try {
                            XposedBridge.log(String.format("MTC Sound version: %s", BuildConfig.VERSION_NAME));
                            logControlMode(getService().getParameters("av_control_mode="));
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        patchAudioManager();
        patchMediaPlayer();
        patchAudioTrack();
        patchMediaRecorder();
        patchAudioRecord();
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        packageName = loadPackageParam.packageName;
        patchMTCManager(loadPackageParam);
        patchMTCAmpSetup(loadPackageParam);
        patchMTCBackView(loadPackageParam);
    }

    // patch AudioManager to replace getParameters/setParameters
    private void patchAudioManager() {

        findAndHookMethod(AudioManager.class, "getParameters", String.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        try {
                            String param = (String) methodHookParam.args[0];
                            String result = getService().getParameters(param);
                            if (param.startsWith("av_control_mode")) {
                                logControlMode(result);
                            }
                            return result;
                        } catch (RemoteException e) {
                            XposedBridge.log("Can't call getParameters() on MtcSoundService due to " + e);
                            return "";
                        }
                    }
                }
        );

        findAndHookMethod(AudioManager.class, "setParameters", String.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        try {
                            String param = (String) methodHookParam.args[0];
                            getService().setParameters(param);
                        } catch (RemoteException e) {
                            XposedBridge.log("Can't call setParameters() on MtcSoundService due to " + e);
                        }
                        return null;
                    }
                }
        );

    }

    // patch MediaPlayer to let the Service know the start/stop/pause etc. events
    private void patchMediaPlayer() {

        findAndHookMethod(MediaPlayer.class, "start",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onMediaPlayerEvent(packageName, MEDIA_STARTED);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod(MediaPlayer.class, "stop",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onMediaPlayerEvent(packageName, MEDIA_STOPPED);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod(MediaPlayer.class, "pause",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onMediaPlayerEvent(packageName, MEDIA_PAUSED);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod(MediaPlayer.class, "postEventFromNative",
                Object.class, int.class, int.class, int.class, Object.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            int event = (int) param.args[1];
                            if (event != MEDIA_PLAYBACK_COMPLETE && event != MEDIA_ERROR)
                                return;
                            getService().onMediaPlayerEvent(packageName, event);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );
    }

    private void patchAudioTrack() {
        findAndHookMethod(AudioTrack.class, "play",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onAudioTrackEvent(packageName, AudioTrack.PLAYSTATE_PLAYING);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod(AudioTrack.class, "stop",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onAudioTrackEvent(packageName, AudioTrack.PLAYSTATE_STOPPED);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod(AudioTrack.class, "pause",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onAudioTrackEvent(packageName, AudioTrack.PLAYSTATE_PAUSED);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );
    }

    private void patchMediaRecorder() {

        findAndHookMethod(MediaRecorder.class, "start",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onRecording(packageName, true);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod(MediaRecorder.class, "stop",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onRecording(packageName, false);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );
    }

    private void patchAudioRecord() {

        findAndHookMethod(AudioRecord.class, "startRecording",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onRecording(packageName, true);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod(AudioRecord.class, "startRecording",
                MediaSyncEvent.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onRecording(packageName, true);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod(AudioRecord.class, "stop",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            getService().onRecording(packageName, false);
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );
    }

    // patch the MTCManager to launch the new equalizer with hardware EQ button
    // instead of switching eq presets that are not supported anymore
    private void patchMTCManager(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

        if (!"android.microntek.service".equals(loadPackageParam.packageName))
            return;

        try {
            findAndHookMethod("android.microntek.service.MicrontekServer", loadPackageParam.classLoader, "EQSwitch",
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            Context context = (Context) methodHookParam.thisObject;
                            if (isEqualizerOnTop(context)) {
                                stopEqualizer(context);
                            } else {
                                startEqualizer(context);
                            }
                            return null;
                        }
                    });
        } catch (XposedHelpers.ClassNotFoundError e) {
            XposedBridge.log("Wrong Device: class android.microntek.service.MicrontekServer not found.");
        }
    }

    // Patch MtcAmpSetup to launch our package
    private void patchMTCAmpSetup(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

        if (!"com.android.settings".equals(loadPackageParam.packageName))
            return;

        try {
            findAndHookMethod("com.android.settings.MtcAmpSetup", loadPackageParam.classLoader, "isPackageInstalled",
                    String.class,
                    PackageManager.class,
                    mtcAmpSetupParameterReplacer());

            findAndHookMethod("com.android.settings.MtcAmpSetup", loadPackageParam.classLoader, "RunApp",
                    String.class,
                    mtcAmpSetupParameterReplacer());
        } catch (XposedHelpers.ClassNotFoundError e) {
            XposedBridge.log("Wrong Device: class com.android.settings.MtcAmpSetup not found.");
        }
    }

    private void patchMTCBackView(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!"com.microntek.backview".equals(loadPackageParam.packageName))
            return;

        findAndHookMethod("com.microntek.backview.BackViewActivity", loadPackageParam.classLoader, "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            getService().setParameters("ctl_backview_active=true");
                    }
                }
        );

        findAndHookMethod("com.microntek.backview.BackViewActivity", loadPackageParam.classLoader, "onDestroy",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            getService().setParameters("ctl_backview_active=false");
                    }
                }
        );
    }

    private XC_MethodHook mtcAmpSetupParameterReplacer() {
        return new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ("com.microntek.ampsetup".equals(param.args[0]))
                    param.args[0] = BuildConfig.APPLICATION_ID;
            }
        };
    }

    private static IMtcSoundService getService() {
        if (service != null) {
            return service;
        }
        service = IMtcSoundService.Stub.asInterface(ServiceManager.getService(MtcSoundService.SERVICE_NAME));
        return service;
    }

    private static void startEqualizer(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(intent);
        }
    }

    private static void stopEqualizer(Context context){
        context.sendBroadcast(new Intent("com.microntek.ampclose"));
    }

    private static boolean isEqualizerOnTop(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(BuildConfig.APPLICATION_ID);
    }

    private static void logControlMode(String mode) {
        XposedBridge.log(String.format("MTC Sound status: %s", mode));
    }

    // constants copied from the MedaiPlayer (only those used here)
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_STARTED = 6;
    private static final int MEDIA_PAUSED = 7;
    private static final int MEDIA_STOPPED = 8;
    private static final int MEDIA_ERROR = 100;
}


