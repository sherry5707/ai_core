package com.kinstalk.her.voip.activity.delegate;

/**
 * Created by siqing on 17/5/23.
 * Voip的界面公用的接口
 */

public interface ITXVoipUIDelegate {

    /**
     * 呼入
     */
    void callingIn();

    /**
     * 通话中
     */
    void onCalling();

    /**
     * 呼出
     */
    void callingOut();

    /**
     * 退出页面
     */
    void exitPage();

    /**
     * 对方摄像头是否关闭
     *
     * @param isClose
     */
    void onPeerCameraChange(boolean isClose);

    /**
     * 对方麦克风是否关闭
     *
     * @param isClose
     */
    void onPeerMicChange(boolean isClose);

    /**
     * 视频连接成功
     */
    void onVoipVideoLoadSucc();

    /**
     * @param isAudio
     */
    void onVoipConnectedSucc(boolean isAudio);

    /**
     * 网络不好
     *
     * @param reason
     */
    void onNetBad(String reason);

    /**
     * 网络良好
     */
    void onNetGood();

    /**
     * 释放画面内存
     */
    void destoryVideoRender();

    /**
     * Voip 状态变化，语音、视频通话切换
     *
     * @param isAudio
     */
    void onVoipStateChange(boolean isAudio);

    /**
     * 设置
     * @param width
     * @param height
     */
    void onSetLocalCameraCap(int width, int height);
}
