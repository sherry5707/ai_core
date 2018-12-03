package com.kinstalk.her.voip.ui.utils;

import android.os.Build;
import android.text.TextUtils;

import android.util.Log;

/**
 * Created by siqing on 17/5/19.
 */
public class LogUtils {
    public static final String TAG = "QQVoip";
    public static final boolean isLog = !Build.TYPE.equals("user");;

    public static void e(String msg) {
        if (isLog && !TextUtils.isEmpty(msg)) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (isLog && !TextUtils.isEmpty(msg)) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isLog && !TextUtils.isEmpty(msg)) {
            Log.i(tag, msg);
        }
    }

    public static void d(String msg) {
        if (isLog && !TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (isLog && !TextUtils.isEmpty(msg)) {
            Log.v(TAG, msg);
        }
    }
}
