package com.sevenfloor.mtcsound.service;
/** {@hide} */
interface IMtcSoundService {
    String getParameters(String paramString);
    void setParameters(String paramString);
    void onMediaPlayerEvent(String callerPackage, int event);
    void onAudioTrackEvent(String callerPackage, int event);
    void onRecording(String callerPackage, boolean active);
    void showToast(String text);
}
