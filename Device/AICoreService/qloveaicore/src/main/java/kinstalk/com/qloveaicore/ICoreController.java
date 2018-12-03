
package kinstalk.com.qloveaicore;

import com.tencent.xiaowei.info.XWResponseInfo;

/**
 * Created by majorxia on 2017/3/21.
 *
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

public interface ICoreController {
    void showMainWindowDelayed(int millis, String reason);

    void hideMainWindowDelayed(int millis, String reason);

    void showSpeechText(String text);

    void Speech2Core_onStartRecord();

    void Speech2Core_onStopRecord();

    void Speech2Core_onRecordVolumeChange(int volume);

    //notify of end of speech
    void Speech2Core_endOfSpeech(Object speechResult);

    void clients2Core_playText(String text);

    void window2Core_onWindowShown(boolean isShown);

    void skillSwitch(String oldSkill, String newSkill);

    void handleCmd(int command, Object param);


    /**
     * 通用控制接口
     * @param type 应用类型
     * @param command 指令
     * @param param 具体处理数据
     * @return void
     */
    void handleEventMethod(int type, int command, Object param);
    void handleXWResponseData(String voiceId, XWResponseInfo rspData, byte[] extendData);
    void handleGeneralCmd(String cmd);
}
