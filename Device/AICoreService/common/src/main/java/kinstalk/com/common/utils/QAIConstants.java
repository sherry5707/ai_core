package kinstalk.com.common.utils;

/**
 * Created by majorxia on 2018/4/18.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public class QAIConstants {

    public static final String JSON_FIELD_CONTINUOUS_LISTEN = "soundPickup";
    public static final String G_SHARED_PREFERENCE_DBG = "ai_debug_pref";
    public static final String G_SHARED_PREFERENCE = "ai_core_pref";
    public static final String SHARED_PREFERENCE_KEY_USE_TX = "useTX";
    public static final String SHARED_PREFERENCE_KEY_UPDATE_CACHE = "cache_deleted";
    public static final String SHARED_PREFERENCE_KEY_USE_SEPARATE_APK = "useEggApp";
    public static final String SHARED_PREFERENCE_KEY_CONTINUOUS_SPEAKING = "enableContinuousSpeaking";
    public static final String SHARED_PREFERENCE_KEY_BT_SCO_DEMO = "enableBtScoDemo";
    public static final String SHARED_PREFERENCE_KEY_SENSORY_SEARCH_IDX = "searchIndex";
    public static final String SHARED_PREFERENCE_KEY_SENSORY_WAKEUP_THRESHOLD = "snsry_wake_threshold";
    public static final String SHARED_PREFERENCE_KEY_SNSRY_WAKEUP_ONLY = "snsry_wakeup_only";
    public static final String SHARED_PREFERENCE_KEY_SNSRY_DUMP_WAKEUP = "snsry_wakeup_dump_audio";
    public static final String SHARED_PREFERENCE_KEY_SNSRY_ENABLE = "snsry_wakeup_enable";
    public static final String SHARED_PREFERENCE_KEY_ENABLE_TX_SDK_LOG = "enable_tx_sdk_log";
    public static final String SHARED_PREFERENCE_KEY_USE_TEST_SERVICE_TYPE= "use_test_service_type";
    public static final String SHARED_PREFERENCE_KEY_WAKEUPFAILURE_TEST_ENABLE = "wakeup_failure_test";
    public static final String SHARED_PREFERENCE_KEY_SAVE_TTS_FILE = "save_tts_file";


    public static final String SHARED_PREFERENCE_KEY_SPEECH_WAKEUP_THRESHOLD = "speech_wake_threshold";
    public static final String SHARED_PREFERENCE_KEY_SPEECH_WAKEUP_ONLY = "snsry_wakeup_only";
    public static final String SHARED_PREFERENCE_KEY_SPEECH_ENABLE = "speech_wakeup_enable";
    public static final String SP_KEY_DUMP_EVERY_VOICE = "enableDumpEveryVoice";
    public static final String SP_KEY_DUMP_VOICE = "enableDumpVoice";
    public static final String SP_KEY_DUMP_CALLBACK = "enableDumpCallback";
    public static final String SP_KEY_DUMP_TTS = "enableDumpTts";
    public static final String SP_KEY_AI_PROMPT = "enableAiPrompt";
    public static final String SP_KEY_BIND_STATE = "bind_state";
    public static final String SP_KEY_BIND_HEADURL = "headUrl";
    public static final String SP_KEY_BIND_REMARK = "remark";
    public static final String SP_KEY_BIND_TYPE = "type";
    public static final String SP_KEY_BIND_CONTACTTYPE = "contactType";

    public static final String APP_NAME_MUSIC = "音乐";
    public static final String APP_NAME_CONTROL_MUSIC = "通用控制/音乐";
    public static final String APP_NAME_FM = "FM";
    public static final String APP_NAME_JOKE = "互动-笑话";
    public static final String APP_NAME_STREAM_MEDIA = "流媒体";
    public static final String APP_NAME_GENERAL_CONTROL = "通用控制";
    public static final String VOICE_CMD_PLAY = "播放";
    public static final String VOICE_CMD_PAUSE = "暂停";
    public static final String VOICE_CMD_NEXT = "下一首";
    public static final String VOICE_CMD_PREV = "上一首";
    public static final String VOICE_CMD_CHANGE = "换一个";
    public static final String VOICE_CMD_KEEP = "收藏";
    public static final String VOICE_CMD_UN_KEEP = "取消收藏";
    public static final String VOICE_CMD_DEFAULT = "我想听音乐";
    public static final String APP_NAME_WEATHER = "天气服务";
    public static final String APP_NAME_ALARM = "提醒类";
    public static final String APP_NAME_NEWS = "微信-新闻";
    public static final String APP_NAME_CONTROL_NEWS = "通用控制/微信-新闻";
    public static final String APP_NAME_AILAB_BAIKE = "AILab-百科";
    public static final String APP_NAME_SOGOU_BAIKE = "搜狗-百科";
    public static final String COMMON_CONTROL_REGEX = "通用控制/+";

    public enum SERVICE_TYPE {
        SERVICE_ENTER,
        SERVICE_CMD,
        SERVICE_MULTIPLETALK_ENTRY,
        SERVICE_MULTIPLETALK_EXIT,
        SERVICE_MULTIPLETALK_CMD
    }

    public static final int AI_ERROR_IFLY_BASE = 1000000;
    public static final int AI_ERROR_IFLY_SCRIPT_ERROR = AI_ERROR_IFLY_BASE + 16005; // 脚本错误
    public static final int AI_ERROR_TENCENT_BASE = 1100000;
    public static final int AI_ERROR_SNOWBOY_BASE = 1200000;

    //external components can send below commands to core service
    public static final int AI_CORE_COMMAND_SHOW_TOAST = 1;
    public static final int AI_CORE_COMMAND_SHOW_ANIM = 2;
    public static final int AI_CORE_COMMAND_EGG_CLICK = 3;

    //network type
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_MOBILE = 0;
    public static final int NETWORK_NONE = -1;

    public static final int SPEECH_CMD_MANUAL_WAKEUP = 0;
    public static final String PROP_TENCENT_WAKEUP_STATE = "sys.ai.wakeup.state";


    public static final int CMD_START_DUMP = 1;
    public static final int CMD_STOP_DUMP = 2;

    // default AI search index
    public static final int AI_DFT_SEARCH_INDEX = 6;
    public static final int AI_DFT_SNSRY_WAKEUP_THRESHOLD = 0;

    //Default Speech parameter
    public static final float AI_DFT_SPEECH_WAKEUP_THRESHOLD = 0.145f;
    public static final String AI_DFT_SPEECH_WAKEUP_WORD = "ni hao xiao wei";
    public static final String AI_DFT_SPEECH_WAKEUP_WORD2 = "xiao wei xiao wei";

    public static final String ANIM_WINDOW_SHOW_REASON_TTS_START = "show for tts start";
    public static final String ANIM_WINDOW_SHOW_REASON_FM_START = "show for fm start";
    public static final String ANIM_WINDOW_SHOW_REASON_FM_RESUME = "show for fm resume";
    public static final String ANIM_WINDOW_SHOW_REASON_WAKEUP = "show for wakeup";
    public static final String ANIM_WINDOW_HIDE_REASON_TTS_STOP = "hide for tts stop";
    public static final String ANIM_WINDOW_HIDE_REASON_FM_STOP = "hide for fm stop";
    public static final String ANIM_WINDOW_HIDE_REASON_RECOGNIZE_RESULT = "hide for recognize result";
    public static final String ANIM_WINDOW_HIDE_REASON_EGG_CLICK = "hide for egg click";

    public static final int MEDIA_PLAYER_EVT_BASE = 0;
    public static final int MEDIA_PLAYER_INIT = 0;
    public static final int MEDIA_PLAYER_LOADING = 1;
    public static final int MEDIA_PLAYER_START = 2;
    public static final int MEDIA_PLAYER_PAUSE = 3;
    public static final int MEDIA_PLAYER_STOP = 4;
    public static final int MEDIA_PLAYER_RESUME = 5;
    public static final int MEDIA_PLAYER_COMPLETE = 6;
    public static final int MEDIA_PLAYER_ERROR = 7;

    public static final int TTS_PLAYER_EVT_BASE = 100;
    public static final int TTS_PLAYER_PLAY = 100;
    public static final int TTS_PLAYER_PAUSE = 101;
    public static final int TTS_PLAYER_ERROR = 102;

    public static final int ADD_ALARM = 0;
    public static final int DELETE_ALARM = 1;
    public static final int UPDATE_ALARM = 2;
    public static final int GET_ALARM = 3;

    public static final String INTENT_ADD_ALARM = "AddAlarm";
    public static final String INTENT_DELETE_ALARM = "DeleteAlarm";
    public static final String INTENT_UPDATE_ALARM = "UpdateAlarm";
    public static final String INTENT_GET_ALARM = "GetAlarm";

    public static final String CONFIG_ACTION_ALL = "all";
    public static final String CONFIG_ACTION_SWITCH_TO_DEV = "switch_to_dev";
    public static final String CONFIG_ACTION_SWITCH_TO_TEST = "switch_to_test";
    public static final String CONFIG_ACTION_SWITCH_TO_PROD = "switch_to_prod";
    //Engines ID
    public static final int ENGINE_SPEECH_LOCAL_WAKEUP = 0x80001;
    public static final int ENGINE_TX_CLOUD_WAKEUP = 0x80002;
    //80004 80008;
    //TTS Engine
    public static final int ENGINE_TX_CLOUD_TTS = 0x80010;
    public static final int ENGINE_SPEECH_CLOUD_TTS = 0x80020;
    public static final int ENGINE_SPEECH_LOCAL_TTS = 0x80040;
    //0x80080
    // ASR Engine
    public static final int ENGINE_SPEECH_LOCAL_ASR = 0x80100;
    public static final int ENGINE_SPEECH_CLOUD_ASR = 0x80200;
    public static final int ENGINE_TX_CLOUD_ASR = 0x800400;
    //800800
}
