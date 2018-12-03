package com.kinstalk.her.voip.recevier;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.kinstalk.her.voip.activity.MissCallActivity;
import com.kinstalk.her.voip.activity.dispatch.ActivityDispatch;
import com.kinstalk.her.voip.ui.utils.LogUtils;
import com.kinstalk.her.voip.utils.NotificationPreHelper;

public class MissCallReceiver extends BroadcastReceiver {
    public static String ACTION_MISS_CALL = "com.kinstalk.her.voip.ACTION_MISS_CALL";

    @Override
    public void onReceive(Context context, Intent intent) {
        MissCallActivity.actionStart(context);
    }

    /**
     * 注册receiver
     * @param context
     */
    public static void registerReceiver(Context context) {
        MissCallReceiver missCallReceiver = new MissCallReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_MISS_CALL);
        context.registerReceiver(missCallReceiver, intentFilter);
    }
}
