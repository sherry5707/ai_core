package com.kinstalk.her.voip.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.HashMap;

import ly.count.android.sdk.Countly;

/**
 * Created by siqing on 17/7/10.
 */

public class StatisticsUtils {
    public static String TAG = "StatisticsUtils";
    /**
     * 手动接听
     */
    private static String T_INCOMING_ACCEPTED = "t_incoming_accepted";

    /**
     * 语音接听
     */
    private static String V_INCOMING_ACCEPTED = "v_incoming_accepted";

    /**
     * 手动挂断
     */
    private static String T_INCOMING_HANGUP = "t_incoming_hangup";

    /**
     * 语音挂断
     */
    private static String V_INCOMING_HANGUP = "v_incoming_hangup";

    /**
     * 异常中断
     */
    private static String INCOMING_CONNECTED_ERROR = "incoming_connected_error";

    /**
     * 未接
     */
    private static String INCOMING_MISSED = "incoming_missed";

    /**
     * 手动拒接
     */
    private static String T_INCOMING_REJECTED = "t_incoming_rejected";

    /**
     * 语音拒接
     */
    private static String V_INCOMING_REJECTED = "v_incoming_rejected";

    /**
     * 点击呼出
     */
    private static String V_START_OUTGOING_METHOD = "v_start_outgoing_method";

    /**
     * 语音呼出
     */
    private static String T_START_OUTGOING_METHOD = "t_start_outgoing_method";

    /**
     * 未匹配到联系人
     */
    private static String V_OUTGOING_UNMATCHED = "v_outgoing_unmatched";

    /**
     * 对方未接听
     */
    private static String OUTGOING_MISSED = "outgoing_missed";

    /**
     * 视频通话状态
     */
    private static String TIMED_VIDEO_CALL_STATS = "timed_video_call_stats";

    /**
     * 相机关闭
     */
    private static String T_CLOSE_CAMERA = "t_close_camera";

    /**
     * 相机打开
     */
    private static String T_OPEN_CAMERA = "t_open_camera";

    /**
     * 视角切换
     */
    private static String T_VIEW_SWITCHING = "t_view_switching";

    /**
     * 声音打开
     */
    private static String T_SPEAKER = "t_speaker";

    /**
     * 静音
     */
    private static String T_MUTE = "t_mute";


    public static void touchIncomingAccept() {
        LogUtils.i(TAG, "touchIncomingAccept");
        Countly.sharedInstance().recordEvent(T_INCOMING_ACCEPTED);
    }

    public static void voiceIncomingAccept() {
        LogUtils.i(TAG, "voiceIncomingAccept");
        Countly.sharedInstance().recordEvent(V_INCOMING_ACCEPTED);
    }

    public static void touchIncomingHangup() {
        LogUtils.i(TAG, "touchIncomingHangup");
        Countly.sharedInstance().recordEvent(T_INCOMING_HANGUP);
    }

    public static void voiceIncomingHangup() {
        LogUtils.i(TAG, "voiceIncomingHangup");
        Countly.sharedInstance().recordEvent(V_INCOMING_HANGUP);
    }

    public static void connectedError() {
        LogUtils.i(TAG, "connectedError");
        Countly.sharedInstance().recordEvent(INCOMING_CONNECTED_ERROR);
    }

    public static void incomingMiss() {
        LogUtils.i(TAG, "incomingMiss");
        Countly.sharedInstance().recordEvent(INCOMING_MISSED);
    }

    public static void touchIncomingReject() {
        LogUtils.i(TAG, "touchIncomingReject");
        Countly.sharedInstance().recordEvent(T_INCOMING_REJECTED);
    }

    public static void voiceIncomingReject() {
        LogUtils.i(TAG, "voiceIncomingReject");
        Countly.sharedInstance().recordEvent(V_INCOMING_REJECTED);
    }


    public static void touchCallingOut() {
        LogUtils.i(TAG, "touchCallingOut");
        Countly.sharedInstance().recordEvent(T_START_OUTGOING_METHOD);
    }

    public static void cameraStatus(boolean isOpen) {
        LogUtils.i(TAG, "cameraStatus isOpen " + isOpen);
        Countly.sharedInstance().recordEvent(isOpen ? T_OPEN_CAMERA : T_CLOSE_CAMERA);
    }

    public static void voiceStatus(boolean isOpen) {
        LogUtils.i(TAG, "voiceStatus isOpen " + isOpen);
        Countly.sharedInstance().recordEvent(isOpen ? T_SPEAKER : T_MUTE);
    }

    public static void switchCanvas() {
        LogUtils.i(TAG, "switchCanvas");
        Countly.sharedInstance().recordEvent(T_VIEW_SWITCHING);
    }

    /**
     * 实现不了
     */
    public static void voiceCallingOut() {
        Countly.sharedInstance().recordEvent(V_START_OUTGOING_METHOD);
    }


    public static void unMatchContact() {
        LogUtils.i(TAG, "unMatchContact");
        Countly.sharedInstance().recordEvent(V_OUTGOING_UNMATCHED);
    }

    public static void outgoingMiss() {
        LogUtils.i(TAG, "outgoingMiss");
        Countly.sharedInstance().recordEvent(OUTGOING_MISSED);
    }

    public static void videoStartEvent() {
        LogUtils.i(TAG, "videoStartEvent");
        Countly.sharedInstance().startEvent(TIMED_VIDEO_CALL_STATS);
    }

    public static void videoEndEvent(Context context, boolean isReceiver, boolean isCameraOpen) {
        HashMap<String, String> segmentation = new HashMap<String, String>();
        segmentation.put("direction", isReceiver ? "incoming" : "outgoing");
        segmentation.put("camera", isCameraOpen ? "open" : "close");
        boolean isCharge = isCarge(context);
        LogUtils.i(TAG, "videoEndEvent isReceiver : " + isReceiver + ", isCameraOpen : " + isCameraOpen + ", isCharge : " + isCharge);
        segmentation.put("charge", isCharge ? "charge" : "nocharge");
        Countly.sharedInstance().endEvent(TIMED_VIDEO_CALL_STATS, segmentation, 1, 0);
    }

    public static boolean isCarge(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = context.registerReceiver(null, ifilter);
        //如果设备正在充电，可以提取当前的充电状态和充电方式（无论是通过 USB 还是交流充电器），如下所示：

        // Are we charging / charged?
        int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (isCharging) {
            if (usbCharge) {
                return true;
            } else if (acCharge) {
                return true;
            }
        }
        return false;
    }

}
