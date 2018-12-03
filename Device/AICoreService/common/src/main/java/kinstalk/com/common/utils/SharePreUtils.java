/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wangzhipeng on 2017/8/8.
 */

public class SharePreUtils {

    private static final String USER_PREFERENCE_NAME = "aiengine.user";
    private final static String USER_TOKEN_KEY = "user_token";
    private final static String USER_TOKEN_TIMESTAMP_KEY = "user_token_timestamp";

    private static Context mContext;
    private static SharedPreferences sUserPrf;

    public static void init(Context ct) {
        mContext = ct;
        sUserPrf = mContext.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static String getUserToken() {
        return sUserPrf.getString(USER_TOKEN_KEY, "");
    }

    public static boolean setUserToken(String value) {
        SharedPreferences.Editor editor = sUserPrf.edit();
        editor.putString(USER_TOKEN_KEY, value);
        return editor.commit();
    }

    public static long getUserTokenTimeStamp() {
        return sUserPrf.getLong(USER_TOKEN_TIMESTAMP_KEY, 0);
    }

    public static boolean setUserTokenTimeStamp(long value) {
        SharedPreferences.Editor editor = sUserPrf.edit();
        editor.putLong(USER_TOKEN_TIMESTAMP_KEY, value);
        return editor.commit();
    }

    public static void clearUserInfos() {
        sUserPrf.edit().clear().apply();
    }

    public static void clearTokens() {
        setUserToken("");
    }

}
