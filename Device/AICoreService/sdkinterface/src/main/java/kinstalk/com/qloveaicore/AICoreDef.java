package kinstalk.com.qloveaicore;

/**
 * Created by majorxia on 2017/6/28.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

//this class contains common sdk definitions. Below definitions could be used by sdk clients
public class AICoreDef {
    public static final String GET_DATA_CMD_STR = "cmd";
    public static final String GET_DATA_CMD_GET_OWNER = "getOwner";
    public static final String GET_DATA_CMD_ERASE_ALL_BINDERS = "eraseAllBinders";

    //params for calling getPicList
    public static final String GET_DATA_PIC_LIST = "getPicList";
    public static final String GET_PIC_LIST_PARAM_OFFSET = "offset";
    public static final String GET_PIC_LIST_PARAM_LIMIT = "limit";
    public static final String GET_PIC_LIST_PARAM_ASC = "asc";

    public static final String JSON_OWNERINFO_FIELD_URL = "headUrl";
    public static final String JSON_OWNERINFO_FIELD_REMARK = "remark";
    public static final String JSON_OWNERINFO_FIELD_TYPE = "type";
    public static final String JSON_OWNERINFO_FIELD_CONTACT_TYPE = "contactType";
    public static final String JSON_OWNERINFO_FIELD_SN = "sn";

    public static final String AI_JSON_FIELD_TYPE = "type";
    public static final String AI_JSON_FIELD_PACKAGE = "pkg";
    public static final String AI_JSON_FIELD_SERVICECLASS = "svcClass";
    public static final String AI_JSON_FIELD_DEFAULTCLIENT = "isDefault";
    public static final String AI_JSON_PLAYTEXT_TEXT = "text";
    public static final String AI_JSON_PLAYTEXT_SPEED = "speed";
    public static final String AI_JSON_PLAYTEXT_ROLE = "role";
    public static final String AI_FIELD_APP_NAME = "appName";
    public static final String AI_FIELD_HAS_CONTEXT = "hasContext";

    public static final String ACTION_TXSDK_TTS = "kinstalk.com.aicore.action.txsdk.tts";
    public static final String ACTION_TXSDK_EXTRA_TTS_STATE = "kinstalk.com.aicore.action.txsdk.tts_state";
    public static final String ACTION_TXSDK_EXTRA_TTS_START = "start";
    public static final String ACTION_TXSDK_EXTRA_TTS_STOP = "stop";

    public static final String ACTION_TXSDK_WEIXIN_PICTURE_COME = "kinstalk.com.aicore.action.weixin.picture";
    public static final String ACTION_TXSDK_EXTRA_PICTURE_DATA = "data";

    public static final String ACTION_AICORE_WINDOW_SHOWN = "kinstalk.com.aicore.action.window_shown";
    public static final String EXTRA_AICORE_WINDOW_SHOWN = "isShown";

    public static final String ACTION_PRIVACY_KEY_SHORT_PRESS = "kinstalk.com.aicore.action.privacy_short_press_1";
    public static final String ACTION_PRIVACY_KEY_START_DUMP_AUDIO = "kinstalk.com.aicore.action.start_dump";
    public static final String ACTION_PRIVACY_KEY_STOP_DUMP_AUDIO = "kinstalk.com.aicore.action.stop_dump";
    public static final String PROP_TENCENT_WAKEUP_STATE = "sys.ai.wakeup.state";
    // bind status broadcast begin
    public static final String ACTION_TXSDK = "kinstalk.com.aicore.action.txsdk";
    public static final String ACTION_TXSDK_EXTRA_PID = "kinstalk.com.aicore.action.txsdk.pid";
    public static final String ACTION_TXSDK_EXTRA_SN = "kinstalk.com.aicore.action.txsdk.sn";
    public static final String ACTION_TXSDK_UPLOAD_SUCCESS = "kinstalk.com.aicore.action.txsdk.upload_complete";
    //设备绑定状态变化广播, extra为 bind_status
    public static final String ACTION_TXSDK_BIND_CHANGE = "kinstalk.com.aicore.action.txsdk.bind_change";
    public static final String EXTRA_AICORE_BIND_STATE = "bind_status";
    // 小微长连接状态变化广播, extra为 long_connection_status
    public static final String ACTION_TXSDK_LONG_CONNECTION_CHANGE = "kinstalk.com.aicore.action.txsdk.long_connection_change";
    public static final String EXTRA_TXSDK_LONG_CONNECTION_STATUS = "long_connection_status";
    //广播qrcode url
    public static final String ACTION_TXSDK_QRCODE_URL = "kinstalk.com.aicore.action.txsdk.qrcode_url";
    public static final String ACTION_TXSDK_EXTRA_QRURL = "kinstalk.com.aicore.action.txsdk.qrcode";
    // bind status broadcast end


    //============================ 语音精灵============================
    public static final String WATER_ANIM_CLIENT_TYPE = "waterAnim";
    public static final String WATER_ANIM_JSON_CMD = "cmd";
    public static final String WATER_ANIM_JSON_TEXT = "text";
    public static final String WATER_ANIM_JSON_VOLUME = "volume";
    public static final String AI_FIELD_TEXT_QUESTION = "textQuestion";
    public static final String AI_FIELD_TEXT_ANSWER = "textAnswer";
    public static final String GET_DATA_EGG_CLICK = "eggClick";

    /**
     * WATER_ANIM_CMD_WAKEUP表示唤醒，这是应该弹出activity
     * WATER_ANIM_CMD_START_RECORD表示开始录音，这时应该播放'听'动画
     * WATER_ANIM_CMD_STOP_RECORD表示停止录音，这时应该停止'听'动画
     * WATER_ANIM_CMD_SPEAKING表示开始说，这时应该播放'说'动画
     * WATER_ANIM_CMD_SHOW_TEXT表示用户说的内容
     * WATER_ANIM_CMD_VOLUME表示音量变化
     * WATER_ANIM_CMD_ENDOFSPEECH表示一轮监听结束，这时activity应该退出
     */
    public static final int WATER_ANIM_CMD_ENDOFSPEECH = 0;
    public static final int WATER_ANIM_CMD_WAKEUP = 1;
    public static final int WATER_ANIM_CMD_START_RECORD = 2;
    public static final int WATER_ANIM_CMD_STOP_RECORD = 3;
    public static final int WATER_ANIM_CMD_SHOW_TEXT = 4;
    public static final int WATER_ANIM_CMD_VOLUME = 10;
    //============================ 语音精灵============================

    public static final String INTENT_AICORE_DEBUG_SETTING = "kinstalk.com.aicore.debug";
    //A boolean extra indicate whether use separate egg ap
    public static final String INTENT_AICORE_DEBUG_EXTRA_EGG_APP = "kinstalk.com.aicore.debug.extra.egg_app";

    public static final String ACTION_PRIVACY_MODE = "kingstalk.action.privacymode";//检测隐私方法：监测Sticky广播
    public static final String ACTION_SHOW_TX_DEBUG_INFO_WINDOW = "kinstalk.com.intent.action.START_AICORE_INFO_WINDOW";//打开显示腾讯调试窗口的广播

    public static final String ACTION_TXSDK_CALL = "kinstalk.com.aicore.action.txsdk.call";
    public static final String ACTION_TXSDK_EXTRA_CALL_STATE = "kinstalk.com.aicore.action.txsdk.call_state";
    public static final String ACTION_TXSDK_EXTRA_CALL_BEGIN = "begin";
    public static final String ACTION_TXSDK_EXTRA_CALL_END = "end";
    public static final String ACTION_FITTIME = "kinstalk.com.aicore.action.fittime";
    public static final String ACTION_FITTIME_STATE = "kinstalk.com.aicore.action.fittime.state";
    public static final String ACTION_FITTIME_STATE_CLOSE = "close";

    public interface REQUEST_DATA_CODE_DEF {
        int SUCCESS = 0;//成功
        int REQ_PARAMS_EMPTY_ERR = 20001; //请求数据为空
        int REQ_PARAMS_JSON_ERR = 20002; //请求数据json格式不对
        int REQ_UNSUPPORTED_SERVICE_TYPE_ERR = 20003; //请求的服务类型不支持
        int REQ_UNSUPPORTED_OPCODE_ERR = 20004; //请求的服务，操作类型不支持
        int VOICEID_EMPTY_ERR = 20005; //调用requestText,返回空的voiceid
    }

    public interface AppControlCmd {
        int CONTROL_CMD_BASE = 0;
        int CONTROL_CMD_RESUME = CONTROL_CMD_BASE + 1;
        int CONTROL_CMD_PAUSE = CONTROL_CMD_BASE + 2;
        int CONTROL_CMD_STOP = CONTROL_CMD_BASE + 3;
        int CONTROL_CMD_PREV = CONTROL_CMD_BASE + 4;
        int CONTROL_CMD_NEXT = CONTROL_CMD_BASE + 5;
        int CONTROL_CMD_RANDOM = CONTROL_CMD_BASE + 6;
        int CONTROL_CMD_ORDER = CONTROL_CMD_BASE + 7;
        int CONTROL_CMD_LOOP = CONTROL_CMD_BASE + 8;
        int CONTROL_CMD_SINGLE = CONTROL_CMD_BASE + 9;
        int CONTROL_CMD_REPEAT = CONTROL_CMD_BASE + 10;
        int CONTROL_CMD_SHARE = CONTROL_CMD_BASE + 11;

//        int CONTROL_CMD_SPEED = CONTROL_CMD_BASE + 100;
//        int CONTROL_CMD_REWIND = CONTROL_CMD_BASE + 101;
//        int CONTROL_CMD_DEFINITION = CONTROL_CMD_BASE + 102;
//        int CONTROL_CMD_POSITION = CONTROL_CMD_BASE + 103;
//        int CONTROL_CMD_DURATION = CONTROL_CMD_BASE + 104;
//
//        int CONTROL_CMD_ENSURE = CONTROL_CMD_BASE + 200;
//        int CONTROL_CMD_CANCEL = CONTROL_CMD_BASE + 201;
//        int CONTROL_CMD_BACK = CONTROL_CMD_BASE + 202;
//
//        int CONTROL_CMD_SELECT = CONTROL_CMD_BASE + 300;
//        int CONTROL_CMD_PREV_PAGE = CONTROL_CMD_BASE + 301;
//        int CONTROL_CMD_NEXT_PAGE = CONTROL_CMD_BASE + 302;
//
//        int CONTROL_CMD_SPEED_TO = CONTROL_CMD_BASE + 400;
//        int CONTROL_CMD_EXIT = CONTROL_CMD_BASE + 401;
//        int CONTROL_CMD_REWIND_TO = CONTROL_CMD_BASE + 402;
    }

    public static class AppState {
        public static final int APP_STATE_BASE = 0;
        public static final int APP_STATE_ONRESUME = APP_STATE_BASE + 1;
        public static final int APP_STATE_ONPAUSE = APP_STATE_BASE + 2;
        public static final int APP_STATE_ONCREATE = APP_STATE_BASE + 3;
        public static final int APP_STATE_ONDESTROY = APP_STATE_BASE + 4;
        public static final int PLAY_STATE_PLAY = APP_STATE_BASE + 5;
        public static final int PLAY_STATE_PAUSE = APP_STATE_BASE + 6;
    }

    // 服务类型名称
    public static class QLServiceType {
        public static final String TYPE_MUSIC = "music";
        public static final String TYPE_WIKI = "wiki";
        public static final String TYPE_FM = "fm";
        public static final String TYPE_WEATHER = "weather";
        public static final String TYPE_SCHEDULE = "schedule";
        public static final String TYPE_GENERIC = "generic";
        public static final String TYPE_NEWS = "news";
        public static final String TYPE_COUNTDOWN = "timer"; //倒计时
        public static final String TYPE_CAMERA = "camera"; //相机
        public static final String TYPE_GALLERY = "gallery"; //相册
        public static final String TYPE_FITTIME = "fittime"; //fittime
        public static final String TYPE_LAUNCHER = "launcher"; //launcher
        public static final String TYPE_SETTINGS = "system";//
        public static final String TYPE_SETTINGS_VOL = "system_vol";//
        public static final String TYPE_COLOURLIFE = "colourlife"; //colourlife
        public static final String TYPE_QINJIAN = "playQinjian";
        public static final String TYPE_VIDEO = "video"; //视频
        public static final String TYPE_HONGEN = "HongEn"; //洪恩绘本
        public static final String TYPE_HEALTH = "ikeeper"; //熙心健康
        public static final String TYPE_CALL = "call";//视频电话
        public static final String TYPE_WAKEUP_SLEEP_MODE = "sleepmode"; //起床睡觉
        public static final String TYPE_GIFT = "gift";

    }

    public static class CtrlCmdType {
        public static final int TYPE_NONE = 0;
        public static final int TYPE_COMMON = 1;
        public static final int TYPE_VOLUME = 2;
    }

    //skill ids begin
    public static final String C_DEF_TXCA_SKILL_ID_UNKNOWN = "8dab4796-fa37-4114-ffff-ffffffffffff";
    public static final String C_DEF_TXCA_SKILL_ID_UNKNOWN_IOT = "8dab4796-fa37-4114-ffff-000000000000";
    public static final String C_DEF_TXCA_SKILL_ID_MUSIC = "8dab4796-fa37-4114-0011-7637fa2b0001";     //skill name: 音乐
    public static final String C_DEF_TXCA_SKILL_ID_FM = "8dab4796-fa37-4114-0024-7637fa2b0001";        //skill name: FM-笑话/FM-电台/FM-小说/FM-相声/FM-评书/FM-故事/FM-杂烩
    public static final String C_DEF_TXCA_SKILL_ID_WEATHER = "8dab4796-fa37-4114-0012-7637fa2b0003";   //skill name: 天气服务
    public static final String C_DEF_TXCA_SKILL_ID_NEWS = "8dab4796-fa37-4114-0019-7637fa2b0001";      //skill name: 新闻
    public static final String C_DEF_TXCA_SKILL_ID_WIKI = "8dab4796-fa37-4114-0020-7637fa2b0001";      //skill name: 百科
    public static final String C_DEF_TXCA_SKILL_ID_HISTORY = "8dab4796-fa37-4114-0027-7637fa2b0001";   //skill name: 历史上的今天
    public static final String C_DEF_TXCA_SKILL_ID_DATETIME = "8dab4796-fa37-4114-0028-7637fa2b0001";  //skill name: 当前时间
    public static final String C_DEF_TXCA_SKILL_ID_CALC = "8dab4796-fa37-4114-0018-7637fa2b0001";      //skill name: 计算器
    public static final String C_DEF_TXCA_SKILL_ID_TRANSLATE = "8dab4796-fa37-4114-0030-7637fa2b0001"; //skill name: 翻译
    public static final String C_DEF_TXCA_SKILL_ID_CHAT = "8dab4796-fa37-4114-0029-7637fa2b0001";      //skill name: 闲聊
    public static final String C_DEF_TXCA_SKILL_ID_IOTCTRL = "8dab4796-fa37-4114-0016-7637fa2b0001"; //skill name: 物联-物联设备控制
    public static final String C_DEF_TXCA_SKILL_ID_ALARM = "8dab4796-fa37-4114-0012-7637fa2b0001";    //skill name: 提醒类
    public static final String C_DEF_TXCA_SKILL_ID_QQTEL = "8dab4796-fa37-4114-0001-7637fa2b0001";    //skill name: 通讯-QQ通话
    public static final String C_DEF_TXCA_SKILL_ID_QQMSG = "8dab4796-fa37-4114-0002-7637fa2b0001";    //skill name: 通讯-QQ消息
    public static final String C_DEF_TXCA_SKILL_ID_MSGBOX = "8dab4796-fa37-4114-0012-7637fa2b0002";   //skill name: 消息盒子
    public static final String C_DEF_TXCA_SKILL_ID_NAVIGATE = "8dab4796-fa37-4114-0015-7637fa2b0001"; //skill name: 导航
    public static final String C_DEF_TXCA_SKILL_ID_VOD = "8dab4796-fa37-4114-0026-7637fa2b0001";      //skill name: 视频
    public static final String C_DEF_TXCA_SKILL_ID_GLOBAL = "8dab4796-fa37-4114-0000-7637fa2b0000";   //skill name: 通用控制
    public static final String C_DEF_TXCA_SKILL_ID_INTELLIGENT_INTERACTION = "8dab4796-fa37-4114-0037-7637fa2b0001";  // 智能互动
    //skill ids end

    public static final String C_DEF_TXCA_SKILL_NAME_MUSIC = "音乐";     //skill name: 音乐
    public static final String C_DEF_TXCA_SKILL_NAME_FM = "8dab4796-fa37-4114-0024-7637fa2b0001";        //skill name: FM-笑话/FM-电台/FM-小说/FM-相声/FM-评书/FM-故事/FM-杂烩
    public static final String C_DEF_TXCA_SKILL_NAME_WEATHER = "天气服务";   //skill name: 天气服务
    public static final String C_DEF_TXCA_SKILL_NAME_NEWS = "新闻";      //skill name: 新闻
    public static final String C_DEF_TXCA_SKILL_NAME_WIKI = "百科";      //skill name: 百科
    public static final String C_DEF_TXCA_SKILL_NAME_HISTORY = "历史上的今天";   //skill name: 历史上的今天
    public static final String C_DEF_TXCA_SKILL_NAME_DATETIME = "当前时间";  //skill name: 当前时间
    public static final String C_DEF_TXCA_SKILL_NAME_CALC = "计算器";      //skill name: 计算器
    public static final String C_DEF_TXCA_SKILL_NAME_TRANSLATE = "翻译"; //skill name: 翻译
    public static final String C_DEF_TXCA_SKILL_NAME_CHAT = "闲聊";      //skill name: 闲聊
    public static final String C_DEF_TXCA_SKILL_NAME_IOTCTRL = "物联-物联设备控制"; //skill name: 物联-物联设备控制
    public static final String C_DEF_TXCA_SKILL_NAME_ALARM = "提醒类";    //skill name: 提醒类
    public static final String C_DEF_TXCA_SKILL_NAME_QQTEL = "通讯-QQ通话";    //skill name: 通讯-QQ通话
    public static final String C_DEF_TXCA_SKILL_NAME_QQMSG = "8dab4796-fa37-4114-0002-7637fa2b0001";    //skill name: 通讯-QQ消息
    public static final String C_DEF_TXCA_SKILL_NAME_MSGBOX = "8dab4796-fa37-4114-0012-7637fa2b0002";   //skill name: 消息盒子
    public static final String C_DEF_TXCA_SKILL_NAME_NAVIGATE = "8dab4796-fa37-4114-0015-7637fa2b0001"; //skill name: 导航
    public static final String C_DEF_TXCA_SKILL_NAME_VOD = "8dab4796-fa37-4114-0026-7637fa2b0001";      //skill name: 视频
    public static final String C_DEF_TXCA_SKILL_NAME_GLOBAL = "8dab4796-fa37-4114-0000-7637fa2b0000";   //skill name: 通用控制
    public static final String C_DEF_TXCA_SKILL_NAME_CAMERA = "skills/8dab4796-fa37-1441-5720-437ca2f3b001";   //skill name: 相机
    public static final String C_DEF_TXCA_SKILL_NAME_FITTIME = "skills/8dab4796-fa37-1441-5835-4b60b4a48001";   //skill name: FitTime
    public static final String C_DEF_TXCA_SKILL_NAME_HEALRH = "skills/8dab4796-fa37-1441-58c2-d41c6c623000";   //skill name: health
    public static final String C_DEF_TXCA_SKILL_NAME_LAUNCHER = "skills/8dab4796-fa37-1441-575d-0574d3b7e007";   //skill name: launcher
    public static final String C_DEF_TXCA_SKILL_NAME_SETTINGS = "skills/8dab4796-fa37-1441-57e7-a90886637000";   //skill name: 系统设置
    public static final String C_DEF_TXCA_SKILL_NAME_COLOURLIFE = "skills/8dab4796-fa37-1441-589a-9e45dfd12015";   //skill name: colourlife
    public static final String C_DEF_TXCA_SKILL_NAME_TIMEREMINDER = "时间提醒";   //skill name: 今天几号
    public static final String C_DEF_TXCA_SKILL_NAME_VIDEO = "视频"; //skill name: 视频
    public static final String C_DEF_TXCA_SKILL_NAME_HONGEN = "skills/8dab4796-fa37-1441-58e5-8e24aa690010";//打开洪恩绘本
    public static final String C_DEF_TXCA_SKILL_NAME_XiXinHealth = "skills/8dab4796-fa37-1441-58d7-16e52c1c6000";   //skill name: 熙心健康
    public static final String C_DEF_TXCA_SKILL_NAME_WAKEUP_OR_SLEEP = "skills/8dab4796-fa37-1441-5719-cf2f8e2e801a";   //skill name: 起床
}
