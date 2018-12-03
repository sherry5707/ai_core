// IXWAudioChatService.aidl
package com.tencent.av;

// Declare any non-default types here with import statements

interface IXWAudioChatService
{
    void accept(boolean isAudioOp);
    void reject(boolean isAudioOp);
    void close(boolean isAudioOp);
    void resumePlayRing();
    void pausePlayRing();
    void ifThirdManageCamera(boolean isThirdManageCamera);
}