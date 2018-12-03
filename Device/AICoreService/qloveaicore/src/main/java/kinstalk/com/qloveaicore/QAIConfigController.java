/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.qloveaicore;

/**
 *  Created by majorxia on 2017/5/2.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.SystemTool;

/**
 * An object used to manage global configuration.
 */
public class QAIConfigController {
    private static final String TAG = "QAIConfigController";
    private Context mContext;

    public QAIConfigController(Context c) {
        this.mContext = c;
    }

    public static boolean readUseTX(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        return sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_USE_TX, false);
    }

    public static void writeUseTX(Context c, Boolean value) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_USE_TX, value);
        editor.apply();
    }

    public static void writeAicoreEnable(Context c, String key, boolean enable) {
        QAILog.d(TAG, "writeEnable key: " + key + " enable: " + enable);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, enable);
        editor.apply();
    }

    public static boolean readAicoreEnable(Context c, String key) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        boolean b;
        if (TextUtils.equals(QAIConstants.SP_KEY_DUMP_CALLBACK, key)) {
            b = sp.getBoolean(key,true);
        } else {
            b = sp.getBoolean(key, false);
        }
        QAILog.d(TAG, "readEnable key: " + key);
        return b;
    }

    public static boolean readBindState(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        return sp.getBoolean(QAIConstants.SP_KEY_BIND_STATE, false);
    }

    public static void writeBindState(Context c, boolean value) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SP_KEY_BIND_STATE, value);
        editor.apply();
    }

    public static void WriteBinderInfo(Context c, DeviceBindStateController.QBinderInfo info) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(QAIConstants.SP_KEY_BIND_HEADURL, info.headUrl);
        editor.putString(QAIConstants.SP_KEY_BIND_REMARK, info.remark);
        editor.putInt(QAIConstants.SP_KEY_BIND_TYPE, info.type);
        editor.putInt(QAIConstants.SP_KEY_BIND_CONTACTTYPE, info.contactType);
        editor.apply();
    }

    public static DeviceBindStateController.QBinderInfo readBinderInfo(Context c) {
        DeviceBindStateController.QBinderInfo info = new DeviceBindStateController.QBinderInfo();

        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        info.headUrl = sp.getString(QAIConstants.SP_KEY_BIND_HEADURL, "");
        info.remark = sp.getString(QAIConstants.SP_KEY_BIND_REMARK, "");
        info.type = sp.getInt(QAIConstants.SP_KEY_BIND_TYPE, 1);
        info.contactType = sp.getInt(QAIConstants.SP_KEY_BIND_CONTACTTYPE, 1);

        return info;
    }

    public static boolean readUseSeparateEggApp(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean b = sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_USE_SEPARATE_APK, true);
        QAILog.d(TAG, "readUseSeparateEggApp: " + b);
        return b;
    }

    public static void writeUseSeparateEggApp(Context c, boolean use) {
        QAILog.d(TAG, "writeUseSeparateEggApp: " + use);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_USE_SEPARATE_APK, use);
        editor.apply();
    }

    public static void writeEnableContinuousSpeaking(Context c, boolean enable) {
        QAILog.d(TAG, "writeEnableContinuousSpeaking: " + enable);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_CONTINUOUS_SPEAKING, enable);
        editor.apply();
    }

    public static boolean readEnableContinuousSpeaking(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean b = sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_CONTINUOUS_SPEAKING, false);
        QAILog.d(TAG, "readEnableContinuousSpeaking: " + b);
        return b;
    }

    public static void writeEnableBTScoDemo(Context c, boolean enable) {
        QAILog.d(TAG, "writeEnableBTScoDemo: " + enable);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_BT_SCO_DEMO, enable);
        editor.apply();
    }

    public static boolean readEnableBTScoDemo(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean b = sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_BT_SCO_DEMO, false);
        QAILog.d(TAG, "readEnableBTScoDemo: " + b);
        return b;
    }

    public static int readSearchIndex(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        int idx = sp.getInt(QAIConstants.SHARED_PREFERENCE_KEY_SENSORY_SEARCH_IDX, QAIConstants.AI_DFT_SEARCH_INDEX);
        QAILog.d(TAG, "search index: " + idx);
        return idx;
    }

    public static void writeSearchIndex(Context c, int index) {
        QAILog.d(TAG, "writeSearchIndex: " + index);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(QAIConstants.SHARED_PREFERENCE_KEY_SENSORY_SEARCH_IDX, index);
        editor.apply();
    }

    public static int readSnsryWakeupThreshold(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        int threshold = sp.getInt(QAIConstants.SHARED_PREFERENCE_KEY_SENSORY_WAKEUP_THRESHOLD, QAIConstants.AI_DFT_SNSRY_WAKEUP_THRESHOLD);
        QAILog.d(TAG, "snsry threshold: " + threshold);
        return threshold;
    }

    public static void writeSnsryWakeupThreshold(Context c, int threshold) {
        QAILog.d(TAG, "writeSnsryWakeupThreshold: " + threshold);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(QAIConstants.SHARED_PREFERENCE_KEY_SENSORY_WAKEUP_THRESHOLD, threshold);
        editor.apply();
    }

    public static boolean readSnsryWakeupOnlyDbg(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean wakeupOnly = sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_SNSRY_WAKEUP_ONLY, false);
        QAILog.d(TAG, "snsry wakeup only: " + wakeupOnly);
        return wakeupOnly;
    }

    public static void writeSnsryWakeupOnlyDbg(Context c, boolean wakeupOnly) {
        QAILog.d(TAG, "writeSnsryWakeupOnlyDbg: " + wakeupOnly);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_SNSRY_WAKEUP_ONLY, wakeupOnly);
        editor.apply();
    }

    //debug purpose, whether dump wakeup audio
    public static boolean readSnsryWakeupDumpAudio(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean dumap_audio = sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_SNSRY_DUMP_WAKEUP, false);
        QAILog.d(TAG, "snsry wakeup dump audio: " + dumap_audio);
        return dumap_audio;
    }

    public static void writeSnsryWakeupDumpAudio(Context c, boolean dump_audio) {
        QAILog.d(TAG, "writeSnsryWakeupDumpAudio: " + dump_audio);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_SNSRY_DUMP_WAKEUP, dump_audio);
        editor.apply();
    }

    public static boolean readSnsryEnable(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean enable = sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_SNSRY_ENABLE, false);
        QAILog.d(TAG, "snsry wakeup enable: " + enable);
        return enable;
    }

    public static void writeSnsryEnable(Context c, boolean enable) {
        QAILog.d(TAG, "writeSnsryEnable: " + enable);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_SNSRY_ENABLE, enable);
        editor.apply();
    }
    public static boolean readWakeupTestEnable(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean enable = sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_WAKEUPFAILURE_TEST_ENABLE, false);
        QAILog.d(TAG, "wakeup failure test enable: " + enable);
        return enable;
    }

    public static void writeWakeupTestEnable(Context c, boolean enable) {
        QAILog.d(TAG, "writeWakeupTestEnable: " + enable);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_WAKEUPFAILURE_TEST_ENABLE, enable);
        editor.apply();
    }
    public static void writeEnable(Context c, String key, boolean enable) {
        QAILog.d(TAG, "writeEnable key: " + key + " enable: " + enable);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, enable);
        editor.apply();
    }

    public static boolean readEnable(Context c, String key) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean b;
        if (TextUtils.equals(QAIConstants.SP_KEY_DUMP_CALLBACK, key)) {
            b = sp.getBoolean(key,true);
        } else {
            b = sp.getBoolean(key, false);
        }
        QAILog.d(TAG, "readEnable key: " + key);
        return b;
    }

    public static boolean readEnableWithDefault(Context c, String key, boolean def_value) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean enable = sp.getBoolean(key, def_value);
        QAILog.d(TAG, "readEnableWithDefault " + key + ":" + enable);

        return enable;
    }
//Speech Local wakeup solution.
    public static boolean readSpeechEnable(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        boolean enable = sp.getBoolean(QAIConstants.SHARED_PREFERENCE_KEY_SPEECH_ENABLE, true);
        //if(SystemTool.isSignVersion(c) == true) //is sign version
        {
            if((SystemTool.getQLoveProductVersion(c) != 3) && (SystemTool.getQLoveProductVersion(c) != 104))//not m10 m4
            {
                QAILog.d(TAG, "Speech wakeup disable for wrong product: " + SystemTool.getQLoveProductVersion(c));
                enable = false;
            }

        }
        QAILog.d(TAG, "Speech wakeup enable: " + enable);
        return enable;
    }

    public static void writeSpeechEnable(Context c, boolean enable) {
        QAILog.d(TAG, "writeSpeechEnable: " + enable);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(QAIConstants.SHARED_PREFERENCE_KEY_SPEECH_ENABLE, enable);
        editor.apply();
    }
    public static float readSpeechWakeupThreshold(Context c) {
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        float threshold = sp.getFloat(QAIConstants.SHARED_PREFERENCE_KEY_SPEECH_WAKEUP_THRESHOLD, QAIConstants.AI_DFT_SPEECH_WAKEUP_THRESHOLD);
        QAILog.d(TAG, "speech threshold: " + threshold);
        return threshold;
    }

    public static void writeSpeechWakeupThreshold(Context c, float threshold) {
        QAILog.d(TAG, "writeSpeechWakeupThreshold: " + threshold);
        SharedPreferences sp = c.getSharedPreferences(QAIConstants.G_SHARED_PREFERENCE_DBG, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(QAIConstants.SHARED_PREFERENCE_KEY_SPEECH_WAKEUP_THRESHOLD, threshold);
        editor.apply();
    }
}
