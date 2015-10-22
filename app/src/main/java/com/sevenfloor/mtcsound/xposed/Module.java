package com.sevenfloor.mtcsound.xposed;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

import com.android.server.am.ActivityManagerService;
import com.sevenfloor.mtcsound.service.IMtcSoundService;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.widget.Toast;

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
    private static final String PACKAGE_NAME = "com.sevenfloor.mtcsound";
    private static IMtcSoundService service;

    // use only for injection into system server
    private static MtcSoundService serviceInstance;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.hookAllMethods(ActivityManagerService.class, "main",
                new XC_MethodHook() {
                    @Override
                    protected final void afterHookedMethod(final XC_MethodHook.MethodHookParam param) {
                        try {
                            Context context = (Context) param.getResult();
                            XposedBridge.log(String.format("Creating service \"%s\"", MtcSoundService.SERVICE_NAME));
                            serviceInstance = new MtcSoundService(context);
                            XposedBridge.log(String.format("Installing service \"%s\" into ServiceManager", MtcSoundService.SERVICE_NAME));
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
                            XposedBridge.log(String.format("System ready. Initializing service \"%s\"", MtcSoundService.SERVICE_NAME));
                            serviceInstance.initialize();
                            XposedBridge.log(String.format("The Sound Control Status is: %s", getService().getParameters("av_control_mode=")));
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        // patch the AudioManager
        findAndHookMethod("android.media.AudioManager", loadPackageParam.classLoader, "getParameters", String.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        try {
                            return getService().getParameters((String) methodHookParam.args[0]);
                        } catch (RemoteException e) {
                            XposedBridge.log("Can't call getParameters() on MtcSoundService due to " + e);
                            return "";
                        }
                    }
                }
        );

        findAndHookMethod("android.media.AudioManager", loadPackageParam.classLoader, "setParameters", String.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        try {
                            getService().setParameters((String) methodHookParam.args[0]);
                        } catch (RemoteException e) {
                            XposedBridge.log("Can't call setParameters() on MtcSoundService due to " + e);
                        }
                        return null;
                    }
                }
        );
/*
        findAndHookMethod("android.media.AudioManager", loadPackageParam.classLoader, "requestAudioFocus",
                OnAudioFocusChangeListener.class, int.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Context context = (Context)XposedHelpers.getObjectField(param.thisObject, "mContext");
                            String caller = param.args[0] == null ? "null" : param.args[0].getClass().getName();
                            int stream = (int)param.args[1];
                            int hint = (int)param.args[2];
                            int result = (int)param.getResult();
                            String text = String.format("requestAudioFocus(%s,%s,%s) == %s", caller, audioFocusStreamName(stream), audioFocusDurationHintName(hint), audioFocusResultName(result));
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );

        findAndHookMethod("android.media.AudioManager", loadPackageParam.classLoader, "abandonAudioFocus",
                OnAudioFocusChangeListener.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                            String caller = param.args[0] == null ? "null" : param.args[0].getClass().getName();
                            int result = (int) param.getResult();
                            String text = String.format("abandonAudioFocus(%s) == %s", caller, audioFocusResultName(result));
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                    }
                }
        );
*/
        // patch the MTCManager to launch the new equalizer with hardware EQ button instead of switching eq presets that are not supported anymore
        if (loadPackageParam.packageName.equals("android.microntek.service")) {
            try {
                XposedBridge.log("Patching android.microntek.service.MicrontekServer");
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
        if (loadPackageParam.packageName.equals("com.android.settings")) {
            try {
                XposedBridge.log("Patching com.android.settings.MtcAmpSetup");
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
    }

    private XC_MethodHook mtcAmpSetupParameterReplacer() {
        return new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ("com.microntek.ampsetup".equals(param.args[0]))
                    param.args[0] = PACKAGE_NAME;
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
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
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
        return componentInfo.getPackageName().equals(PACKAGE_NAME);
    }

    //--------------------

    private String audioFocusDurationHintName(int value) {
        switch (value) {
            case AudioManager.AUDIOFOCUS_GAIN:
                return "AUDIOFOCUS_GAIN";
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                return "AUDIOFOCUS_GAIN_TRANSIENT";
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE:
                return "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE";
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                return "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
            case AudioManager.AUDIOFOCUS_LOSS:
                return "AUDIOFOCUS_LOSS";
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                return "AUDIOFOCUS_LOSS_TRANSIENT";
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                return "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
            case 0:
                return "AUDIOFOCUS_NONE";
            default:
                return "Unknown";
        }
    }

    private String audioFocusStreamName(int value) {
        switch (value) {
            case AudioManager.STREAM_ALARM:
                return "STREAM_ALARM";
            case 6:
                return "STREAM_BLUETOOTH_SCO";
            case AudioManager.STREAM_DTMF:
                return "STREAM_DTMF";
            case AudioManager.STREAM_MUSIC:
                return "STREAM_MUSIC";
            case AudioManager.STREAM_NOTIFICATION:
                return "STREAM_NOTIFICATION";
            case AudioManager.STREAM_RING:
                return "STREAM_RING";
            case AudioManager.STREAM_SYSTEM:
                return "STREAM_SYSTEM";
            case 7:
                return "STREAM_SYSTEM_ENFORCED";
            case 9:
                return "STREAM_TTS";
            case 0:
                return "STREAM_VOICE_CALL";
            default:
                return "Unknown";
        }
    }

    private String audioFocusResultName(int value) {
        switch (value) {
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                return "AUDIOFOCUS_REQUEST_FAILED";
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                return "AUDIOFOCUS_REQUEST_GRANTED";
            default:
                return "Unknown";
        }
    }

}


