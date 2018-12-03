package com.kinstalk.her.voip.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.kinstalk.her.voip.activity.MissCallActivity;

/**
 * Created by siqing on 17/11/1.
 */

public class MissCallReceiverStyle2 extends BroadcastReceiver {
    public static String ACTION_MISS_CALL = "com.kinstalk.her.voip.ACTION_MISS_CALL_STYLE_2";

    @Override
    public void onReceive(Context context, Intent intent) {
        MissCallActivity.actionStart(context);
    }

    /**
     * 注册receiver
     * @param context
     */
    public static void registerReceiver(Context context) {
        MissCallReceiverStyle2 missCallReceiverStyle2 = new MissCallReceiverStyle2();
        IntentFilter intentFilter = new IntentFilter(ACTION_MISS_CALL);
        context.registerReceiver(missCallReceiverStyle2, intentFilter);
    }
}
