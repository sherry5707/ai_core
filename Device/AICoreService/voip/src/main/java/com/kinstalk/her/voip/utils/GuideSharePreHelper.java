package com.kinstalk.her.voip.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kinstalk.her.voip.constant.VoipConstant;

/**
 * Created by siqing on 17/8/30.
 */

public class GuideSharePreHelper {

    /**
     * 是否显示过添加好友引导
     *
     * @param context
     * @return
     */
    public static boolean isGuideShowed(Context context) {
        SharedPreferences sharedPreferences = SharePreUtils.getSharePreferences(context, VoipConstant.GUIDE_FILE_NAME);
        return sharedPreferences.getBoolean(VoipConstant.GUIDE_KEY, false);
    }

    public static void setGuideShowed(Context context) {
        SharePreUtils.edit(context, VoipConstant.GUIDE_FILE_NAME, new SharePreUtils.EditorOpt() {
            @Override
            public void onEdit(SharedPreferences.Editor editor) {
                editor.putBoolean(VoipConstant.GUIDE_KEY, true);
            }
        });
    }
}
