package com.sevenfloor.mtcsound.service;
/** {@hide} */
interface IMtcSoundService {
    String getParameters(String paramString);
    void setParameters(String paramString);
    void onMediaPlayerEvent(String callerPackage, int event);
    void showToast(String text);
}
