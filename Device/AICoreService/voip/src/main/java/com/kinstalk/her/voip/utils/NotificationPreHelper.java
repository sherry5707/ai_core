package com.kinstalk.her.voip.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kinstalk.her.voip.constant.VoipConstant;

/**
 * Created by siqing on 17/8/30.
 */

public class NotificationPreHelper {

    /**
     * 是否显示过添加好友引导
     *
     * @param context
     * @return
     */
    public static int getNotifyCount(Context context, String key) {
        SharedPreferences sharedPreferences = SharePreUtils.getSharePreferences(context, VoipConstant.NOTIFY_FILE_NAME);
        return sharedPreferences.getInt(key, 0);
    }

    public static void setNotifyCount(Context context, final String key, final int count) {
        SharePreUtils.edit(context, VoipConstant.NOTIFY_FILE_NAME, new SharePreUtils.EditorOpt() {
            @Override
            public void onEdit(SharedPreferences.Editor editor) {
                editor.putInt(key, count);
            }
        });
    }
}
