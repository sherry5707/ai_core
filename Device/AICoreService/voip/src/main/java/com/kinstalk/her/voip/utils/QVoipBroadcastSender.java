package com.kinstalk.her.voip.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.kinstalk.her.voip.ui.utils.LogUtils;

/**
 * Created by siqing on 17/9/11.
 */

public class QVoipBroadcastSender {

    /**
     * Voip广播的Action
     */
    public static final String ACTION_KINSTALK_VOIP = "com.kinstalk.her.voip.ACTION_VOIP";

    /**
     * 状态：1.STATUS_INCOMING 2.STATUS_OUTGOING
     */
    public static final String INTENT_STATUS = "com.kinstalk.her.voip.status";

    /**
     * Voip动作类型：1.TYPE_CALLING 2.TYPE_END
     */
    public static final String INTENT_TYPE = "com.kinstalk.her.voip.type";

    /**
     * Voip开始
     */
    public static final String TYPE_CALLING = "calling";

    /**
     * Voip结束
     */
    public static final String TYPE_END = "end";

    /**
     * 呼入状态
     */
    public static final String STATUS_INCOMING = "incoming";

    /**
     * 呼出状态
     */
    public static final String STATUS_OUTGOING = "outgoing";


    /**
     * 呼入
     *
     * @param context
     */
    public static void sendIncomingBroadcast(Context context) {
        LogUtils.i("sendIncomingBroadcast");
        sendStatusIntent(context, TYPE_CALLING, STATUS_INCOMING);
    }

    /**
     * 呼出
     *
     * @param context
     */
    public static void sendOutGoingBroadcast(Context context) {
        LogUtils.i("sendOutGoingBroadcast");
        sendStatusIntent(context, TYPE_CALLING, STATUS_OUTGOING);
    }

    /**
     * 结束
     *
     * @param context
     */
    public static void sendEndBroadcast(Context context) {
        LogUtils.i("sendEndBroadcast");
        sendStatusIntent(context, TYPE_END, null);
    }

    private static void sendStatusIntent(Context context, String type, String status) {
        Intent intent = new Intent(ACTION_KINSTALK_VOIP);
        intent.putExtra(INTENT_TYPE, type);
        if (!TextUtils.isEmpty(status)) {
            intent.putExtra(INTENT_STATUS, status);
        }
        context.sendStickyBroadcast(intent);
    }
}
