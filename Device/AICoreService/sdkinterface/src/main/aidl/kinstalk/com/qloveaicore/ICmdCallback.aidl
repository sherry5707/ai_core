// ICmdCallback.aidl
package kinstalk.com.qloveaicore;
//import XWResponseInfo;
import com.tencent.xiaowei.info.XWResponseInfo;
import com.tencent.xiaowei.info.QLoveResponseInfo;

interface ICmdCallback {
        // Depracated
        String processCmd(String json);
        void handleQLoveResponseInfo(String voiceId, in QLoveResponseInfo rspData, in byte[] extendData);

    /**
     * 唤醒后语音精灵动画的控制接口
     * @param command 指令
     * @param data 具体处理数据
     * @return void
     */
     void handleWakeupEvent(int command, String data);

}
