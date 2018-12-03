/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.common;

//import kinstalk.com.utils.SystemTool;

/**
 * Created by majorxia on 2017/4/18.
 */

public class QAIConfig {
    public static final String VERSION = "20170422_00_00_05";
//    public static boolean isDebugMode = !SystemTool.isUserType();
    public static boolean isCrashCookerEnable = false;
    public static boolean isAudioCookerEnable = true;
    public static boolean isAiPromptEnable = false;
    public static boolean isNetworkGood = true; //标志网络是否ok
    public static String host_url = "";
    public static String txlicense_url = "";
    public static String aiengine_url = "";
    public static String unani_url = "";
    public static String accesskey_url = "";
    public static String music_url = "";
    public static String voicecmd_url = "";
    public static String engine = "";
    public static int code = 0;
    public static int current_env = 0;
    public static boolean autoTestDevice = false;
    public static int qLoveProductVersionNum = -1;


    public static int logLevel = -1;
    public static int logType = -1;

    public static final int LOG_NETWORK_API = 1;
    public static final int LOG_STATE_MACHINE = 2;
    public static final int LOG_EGG_ANIMATION = 4;
    public static final int LOG_WAKEUP_RECOGNIZE = 8;
    public static final int LOG_CONVERTER = 16;
    public static final int LOG_DATA_DISPATCH = 32;
    public static final int LOG_PLAYER = 64;
    public static final int LOG_COOKER = 128;
    public static final int LOG_LOCATION = 256;
    public static final int LOG_TX_SDK = 512;

    public static final boolean GET_LICENCE_FROM_SRV = true;
    public static final boolean ENABLE_CONFIG_API = true;
    public static final boolean ENABLE_RECONNECT_ON_PRIVACY_KEY_PRESS = false;

    public static final int MODEL_ARMSTRONG = 0; // armstrong/discovery
    public static final int MODEL_COLUMBUS = 1; // 哥伦布
    public static final int MODEL_MAGELLAN_M10 = 3; // 麦哲伦 M10
    public static final int MODEL_MAGELLAN_M7 = 5; // 麦哲伦 M7
    public static final int MODEL_MAGELLAN_M4 = 104; // M4

    public static String dumpString() {
        return "dumpQAIConfig: \n" +
                " isCrashCookerEnable: " + isCrashCookerEnable +
                " isAudioCookerEnable: " + isAudioCookerEnable +
                " txlicense_url: " + txlicense_url +
                " aiengine_url: " + aiengine_url +
                " unani_url: " + unani_url +
                " accesskey_url: " + accesskey_url +
                " music_url: " + music_url +
                " voicecmd_url: " + voicecmd_url +
                " engine: " + engine +
                " code: " + code +
                " current_env: " + current_env +
                " autoTestDevice: " + autoTestDevice;
    }
}
