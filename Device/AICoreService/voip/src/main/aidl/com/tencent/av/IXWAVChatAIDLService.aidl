// IXWAVChatAIDLService.aidl
package com.tencent.av;
import com.tencent.xiaowei.info.XWContactInfo;
import com.tencent.xiaowei.info.XWAudioFrameInfo;

interface IXWAVChatAIDLService
{
    long getSelfDin();
    boolean isContact(long uin);
    XWContactInfo getXWContactInfo(String uin);
    List getBinderList();
    byte[] getVideoChatSignature();
    void notifyVideoServiceStarted();
    void sendVideoCall(long peerUin, int uinType, in byte[] msg);
    void sendVideoCallM2M(long peerUin, int uinType, in byte[] msg);
    void sendVideoCMD(long peerUin, int uinType, in byte[] msg);
    void setVideoPID(int pid, String videoService);
    XWAudioFrameInfo readAudioData(int length);
    void startQQCallSkill(long uin);
    void cancelAIAudioRequest();
    int sendQQCallRequest(int uinType, long tinyId, in byte[] msg, int length);
    long getBindedQQUin();
    void statisticsPoint(String compassName, String event, String param, long time);
}