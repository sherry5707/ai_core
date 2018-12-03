// ITTSCallback.aidl
package kinstalk.com.qloveaicore;

// Declare any non-default types here with import statements

interface ITTSCallback {
    void onTTSPlayBegin(String voiceId);
    void onTTSPlayEnd(String voiceId);
    void onTTSPlayProgress(String voiceId, int progress);
    void onTTSPlayError(String voiceId, int errCode, String errString);
}
