package com.kinstalk.her.voip.ui.fragment.delegate;

/**
 * Created by siqing on 17/5/19.
 * 通话中视频..
 */

public interface CallingVideoDelegate {

    /**
     * 静音
     */
    void onSilenceClick(boolean isSilience);

    /**
     * 摄像头
     */
    void onCameraClick(boolean isCameraOpen);

    /**
     * 挂断
     */
    void onHangUpClick();

    /**
     * 是否是通话中
     * @return
     */
    boolean isConnecting();
}
