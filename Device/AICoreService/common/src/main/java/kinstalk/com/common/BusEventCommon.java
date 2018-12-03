/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.common;

import android.os.Bundle;

/**
 * Created by majorxia on 2016/6/24.
 */
public class BusEventCommon {
    public int eventType;

    public int intVar1;
    public int intVar2;

    public String strVar1;
    public String strVar2;

    public boolean bVar1;
    public boolean bVar2;

    public Object oVar1;
    public Object oVar2;

    public Bundle eventBundle;

    // <--------------- event define begin
    // for FM play, Media preparing, bVar1 indicates whether it's prepare or not
    private static final int EVENT_BASE = 1000;
    public static final int JPUSH_EVENT = EVENT_BASE + 1;
    public static final int MUSIC_STATE_EVENT = EVENT_BASE + 2;
    public static final int TTS_PLAY_STATE_EVENT = EVENT_BASE + 3;
    public static final int TTS_PLAY_START = EVENT_BASE + 4;
    public static final int TTS_PLAY_STOP = EVENT_BASE + 5;
    public static final int START_TTS_SPEACKING_ANIM = EVENT_BASE + 6;
    public static final int STOP_TTS_SPEACKING_ANIM = EVENT_BASE + 7;
    public static final int CONVERTOR_RESP_EVENT = EVENT_BASE + 8;
    public static final int BIND_STATE_CHANGE = EVENT_BASE + 9;
    public static final int START_TTS_NOT_UNDERSTOOD_ANIM = EVENT_BASE + 10;
    public static final int FM_PLAY_START = EVENT_BASE + 11;
    public static final int FM_PLAY_STOP = EVENT_BASE + 12;
    public static final int ON_CLICK_EGG_EVENT = EVENT_BASE + 13;
    public static final int FM_PLAY_PAUSE = EVENT_BASE + 14;
    public static final int FM_PLAY_RESUME = EVENT_BASE + 15;

    public static final int FM_STATE_EVENT = EVENT_BASE + 25;
    public static final int TTS_VOL_EVENT = EVENT_BASE + 26;
    public static final int NEWS_STATE_EVENT = EVENT_BASE + 27;
    public static final int BAIKE_STATE_EVENT = EVENT_BASE + 28;
    public static final int MEDIA_PLAYER_EVENT = EVENT_BASE + 29;
    public static final int ALARM_RESP_EVENT = EVENT_BASE + 30;
    public static final int BUS_EVT_SENSORY_WAKEUP = EVENT_BASE + 31;
    public static final int AI_AUDIO_STATE_CALL_BEGIN = EVENT_BASE + 32;
    public static final int AI_AUDIO_STATE_CALL_END = EVENT_BASE + 33;
    public static final int MUSIC_GET_CAT = EVENT_BASE + 34;


    // ---------------> event define end
}
