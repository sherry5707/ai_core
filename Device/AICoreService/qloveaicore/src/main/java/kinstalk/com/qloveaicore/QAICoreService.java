package kinstalk.com.qloveaicore;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.tencent.xiaowei.control.XWMediaType;
import com.tencent.xiaowei.control.XWeiControl;
import com.tencent.xiaowei.control.info.XWeiMediaInfo;
import com.tencent.xiaowei.def.XWCommonDef;
import com.tencent.xiaowei.info.*;
import com.tencent.xiaowei.sdk.XWSDK;
import kinstalk.com.common.utils.CountlyEvents;
import kinstalk.com.common.utils.QAICommandUtils;
import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.qloveaicore.genericskill.GenericSkillService;
import kinstalk.com.qloveaicore.statemachine.QAISpeechStatesMachine;
import kinstalk.com.qloveaicore.statemachine.SpeechController;
import kinstalk.com.qloveaicore.video.VideoAppManager;
import org.json.JSONObject;


/**
 * Created by majorxia on 2018/3/29.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public class QAICoreService extends Service implements ICoreController {
    private static final String TAG = "AI-QAICoreService";
    private static final int MSG_CORE_BASE = 0x110;
    public static final int MSG_PLAY_TEXT = MSG_CORE_BASE + 1;
    public static final int MSG_PLAY_WRITE_BEAT = MSG_CORE_BASE + 2;
    public static final int MSG_EXTERNAL_COMMAND = MSG_CORE_BASE + 3;
    public static final int MSG_EVENTBUS_EVT = MSG_CORE_BASE + 4;
    public static final int MSG_SKILL_SWITCH = MSG_CORE_BASE + 5;

    public CoreControl mControl;
    private VoiceCmdHandler mCommandHandler;
    private SpeechController mSpeechController;
    private QAISpeechStatesMachine mSpeechSM=null;
    private ControlCommandController mCtrlCmd;
    private Handler mHandler;
    private String mLastVoiceId;
    private long mLastTtsTime;
    private static QAICoreService sInst = null;
    private Object mTTSLock = new Object();

    public static QAICoreService getInstance() {
        return sInst;
    }

    //-------------Service methods begin
    @Override
    public IBinder onBind(Intent intent) {
        QAILog.d(TAG, "onBind: Enter, i:" + intent);
        return mControl;
    }

    @Override
    public void onCreate() {
        QAILog.d(TAG, "onCreate: Enter");
        super.onCreate();
        mHandler = new MainHandler();
        QAIAudioFocusMgr.getInst().init(getApplicationContext()); // init on first get

        mControl = new CoreControl();
        mCommandHandler = new VoiceCmdHandler(getApplicationContext());
        mSpeechSM =  QAISpeechStatesMachine.getInstance();
        mSpeechSM.start(getApplicationContext(),this); //this part should be first start.

        mCtrlCmd = ControlCommandController.getInstance();
        sInst = this;
        startGenericSkillService(this);

        registerPrivacyReceiver();

        VideoAppManager.getInstance().registerIfNeeded(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterPrivacyReceiver();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
    //-------------Service methods end

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_EXTERNAL_COMMAND:
                    handleCmdInternal(msg.arg1, msg.arg2, msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleCmdInternal(int type, int command, Object param) {
        QAILog.d(TAG, "handleCmdInternal: cmd," + command);
        switch (type) {
            case QAIConstants.AI_CORE_COMMAND_SHOW_TOAST:
                String s = (String) param;
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                break;
            case QAIConstants.AI_CORE_COMMAND_SHOW_ANIM:
                mCommandHandler.dispatchAnimation(type, command, (String) param);
                break;
        }
    }

    public void registerInternalService(final String jsonParam, final ICmdCallback cb) {
        Log.i(TAG, "registerInternalService: Enter");
        if (TextUtils.isEmpty(jsonParam) || cb == null) {
            QAILog.i(TAG, "registerInternalService: wrong parameters, return");
            return;
        }
        mCommandHandler.dispatchClientRegister(jsonParam, cb);
    }

    //-------------sdk methods begin
    public class CoreControl extends IAICoreInterface.Stub {
        @Override
        public void unRegisterService(final String jsonParam) throws RemoteException {
            QAILog.i(TAG, "unRegisterService: Enter");
            if (TextUtils.isEmpty(jsonParam)) {
                QAILog.d(TAG, "registerService: wrong parameters, return");
                return;
            }

            mCommandHandler.dispatchClientUnRegister(jsonParam);
        }

        @Override
        public void registerService(final String jsonParam, final ICmdCallback cb) throws RemoteException {
            Log.i(TAG, "registerService: Enter");
            if (TextUtils.isEmpty(jsonParam) || cb == null) {
                QAILog.i(TAG, "registerService: wrong parameters, return");
                return;
            }

            mCommandHandler.dispatchClientRegister(jsonParam, cb);
        }

        @Override
        public void playText(final String jsonText) throws RemoteException {
            Log.i(TAG, "playText: Enter " + jsonText);
            String voiceid = "";

            try {
                if (!TextUtils.isEmpty(jsonText)) {
                    JSONObject json = new JSONObject(jsonText);
                    String t = json.optString(AICoreDef.AI_JSON_PLAYTEXT_TEXT);

                    playTextWithStr(t, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i(TAG, "playText: voiceId: " + voiceid);
        }

        @Override
        public void requestData(final String jsonParam) throws RemoteException {
            Log.i(TAG, "requestData: Enter");

        }

        @Override
        public RequestDataResult requestDataWithCb(String jsonParam, final ICmdCallback cb) throws RemoteException {
            Log.i(TAG, "requestDataWithCb: Enter");
            String strVoiceId = XWSDK.getInstance().requestWithCallBack(XWCommonDef.RequestType.TEXT, jsonParam.getBytes(), new XWContextInfo(), new XWSDK.RequestListener() {
                @Override
                public boolean onRequest(int event, final XWResponseInfo rspData, byte[] extendData) {
                    if(cb != null){
                        QLoveResponseInfo info = new QLoveResponseInfo(rspData, "");
                        try {
                            cb.handleQLoveResponseInfo(rspData.voiceID, info, extendData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });
            QAILog.i(TAG, "textRequest: voiceId:" + strVoiceId);
            return null;
        }

        @Override
        public void getData(final String jsonParam, final ICmdCallback cb) {
            Log.i(TAG, "getData: Enter ");
            mCommandHandler.handleGetData(jsonParam, cb);
        }

        private void reportError(String voiceId, ITTSCallback cb) {
            try {
                cb.onTTSPlayError(voiceId, 1, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**
         * 通过voice id播放TTS， 同时用回调函数通知播放状态
         *
         * @param voiceId 要播放的 voice id
         * @param cb      tts播放状态回调
         */
        @Override
        public void playTextWithId(String voiceId, ITTSCallback cb) {
            Log.d(TAG, "playTextWithId() called with: voiceId = [" + voiceId + "], cb = [" + cb + "]" + " pid:" + Binder.getCallingPid());
            Throwable t = new Throwable(TAG);
            t.printStackTrace();
            long time = System.currentTimeMillis();

            if (mLastVoiceId != null && voiceId != null) {
                if (mLastVoiceId.equals(voiceId)) {
                    reportError(voiceId, cb);
                    return;
                }
            }
            mLastVoiceId = voiceId;

            //workaround for tts leak
            if ((time - mLastTtsTime) < 500) {
                mLastTtsTime = time;
                reportError(voiceId, cb);
                return;
            }
            mLastTtsTime = time;
            Log.i(TAG, "playTextWithId() called with: voiceId = [" + voiceId + "], cb = [" + cb + "]");

            if (voiceId == null) {
                Log.i(TAG, "playTextWithId: null voiceId");
                CountlyEvents.clientRequestTTSTextEmpty();
                return;
            }

            XWeiMediaInfo mediaInfo = new XWeiMediaInfo();
            mediaInfo.resId = voiceId;
            mediaInfo.mediaType = XWMediaType.TYPE_TTS_OPUS;
            boolean isSuccess = XWeiControl.getInstance().playMedia(mediaInfo, true, cb);

            CountlyEvents.clientRequestTTS(isSuccess, voiceId);
        }

        /**
         * 通过纯文本播放TTS， 同时用回调函数通知播放状态
         *
         * @param text 要播放的文本字符串，不是JSON格式
         * @param cb   tts播放状态回调
         */
        @Override
        public void playTextWithStr(String text, final ITTSCallback cb) {
            Log.d(TAG, "playTextWithStr() called with: text = [" + text + "], cb = [" + cb + "]" + " pid:" + Binder.getCallingPid());
            String voiceid = "";

            if (TextUtils.isEmpty(text)){
                CountlyEvents.clientRequestTTSTextEmpty();
                return;
            }

            try {
                voiceid = XWSDK.getInstance().requestTTS(text.getBytes(), new XWContextInfo(), new XWSDK.RequestListener() {
                    @Override
                    public boolean onRequest(int event, final XWResponseInfo rspData, byte[] extendData) {
                        Log.d(TAG, "onRequest playText callback: event = [" + event
                                + "], rspData = [" + rspData + "], extendData = [" + extendData + "]");
                        playTextWithId(rspData.voiceID, cb);
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                CountlyEvents.clientRequestTTSTextEmpty();
            }

            Log.i(TAG, "playText: voiceId: " + voiceid);
        }

        @Override
        public String textRequest(String text) throws RemoteException {
            QAILog.i(TAG, "textRequest: " + text);
            if (TextUtils.isEmpty(text)) {
                CountlyEvents.clientRequestDataEmpty();
                CountlyEvents.textRecognitionFailNoFeedback();
            }
            String voiceId;
            voiceId = XWSDK.getInstance().request(XWCommonDef.RequestType.TEXT, text.getBytes(), new XWContextInfo());
            QAILog.i(TAG, "textRequest: voiceId:" + voiceId);
            if (TextUtils.isEmpty(voiceId)) {
                CountlyEvents.clientRequestDataError();
                CountlyEvents.textRecognitionFailNoFeedback();
            } else {
                CountlyEvents.textRecognitionSucceed(text);
            }
            return voiceId;
        }

        @Override
        public String setFavorite(String app, String playID, boolean favorite) {
            QAILog.i(TAG, "setFavorite() called with: app = [" + app + "], playID = ["
                    + playID + "], favorite = [" + favorite + "]");
            XWAppInfo appInfo = new XWAppInfo();
            if (TextUtils.equals(AICoreDef.C_DEF_TXCA_SKILL_NAME_MUSIC, app)) {
                appInfo.ID = AICoreDef.C_DEF_TXCA_SKILL_ID_MUSIC;
                appInfo.name = AICoreDef.C_DEF_TXCA_SKILL_NAME_MUSIC;
            }

            String ret = XWSDK.getInstance().setFavorite(appInfo, playID, favorite);
            QAILog.i(TAG, "setFavorite: ret " + ret);
            return ret;
        }

        @Override
        public void getMusicVipInfo(final ICmdCallback callback) {
            QAILog.d(TAG, "getMusicVipInfo: Enter, " + callback);
            if (callback == null) {
                return;
            }

            XWSDK.getInstance().getMusicVipInfo(new XWSDK.RequestListener() {
                @Override
                public boolean onRequest(int event, XWResponseInfo rspData, byte[] extendData) {
                    QAILog.i(TAG, "getMusicVipInfo onRequest");
                    QLoveResponseInfo info = new QLoveResponseInfo(rspData, "");
                    try {
                        callback.handleQLoveResponseInfo(rspData.voiceID, info, extendData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }

        @Override
        public String getMorePlaylist(XWAppInfo appInfo, String playID, int maxListSize, boolean isUp, final ICmdCallback callback) {
            QAILog.d(TAG, "getMorePlaylist: Enter, " + callback);
            if (callback == null) {
                return null;
            }

            return XWSDK.getInstance().getMorePlaylist(appInfo, playID, maxListSize, isUp, new XWSDK.RequestListener() {
                @Override
                public boolean onRequest(int event, XWResponseInfo rspData, byte[] extendData) {
                    QAILog.i(TAG, "getMorePlaylist onRequest");
                    QLoveResponseInfo info = new QLoveResponseInfo(rspData, "");
                    try {
                        callback.handleQLoveResponseInfo(rspData.voiceID, info, extendData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }

        @Override
        public String getPlayDetailInfo(XWAppInfo appInfo, String[] listPlayID, final ICmdCallback callback) throws RemoteException {
            QAILog.d(TAG, "getPlayDetailInfo: Enter, " + callback);
            if (callback == null) {
                return null;
            }

            return XWSDK.getInstance().getPlayDetailInfo(appInfo, listPlayID, new XWSDK.RequestListener() {
                @Override
                public boolean onRequest(int event, XWResponseInfo rspData, byte[] extendData) {
                    QAILog.i(TAG, "getPlayDetailInfo onRequest");
                    QLoveResponseInfo info = new QLoveResponseInfo(rspData, "");
                    try {
                        callback.handleQLoveResponseInfo(rspData.voiceID, info, extendData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }

        @Override
        public String refreshPlayList(XWAppInfo appInfo, String[] listPlayID, final ICmdCallback callback) {
            QAILog.d(TAG, "refreshPlayList: Enter, " + callback);
            if (callback == null) {
                return null;
            }

            return XWSDK.getInstance().refreshPlayList(appInfo, listPlayID, new XWSDK.RequestListener() {
                @Override
                public boolean onRequest(int event, XWResponseInfo rspData, byte[] extendData) {
                    QAILog.i(TAG, "refreshPlayList onRequest");
                    QLoveResponseInfo info = new QLoveResponseInfo(rspData, "");
                    try {
                        callback.handleQLoveResponseInfo(rspData.voiceID, info, extendData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }

        @Override
        public int reportPlayState(XWAppInfo appInfo, int state, String playID, String playContent, long playOffset, int playMode) {
            QAILog.d(TAG, "reportPlayState: Enter");

            XWPlayStateInfo stateInfo = new XWPlayStateInfo();
            stateInfo.appInfo = appInfo;
            stateInfo.state = state;
            stateInfo.playID = playID;
            stateInfo.playContent = playContent;
            stateInfo.playOffset = playOffset;
            stateInfo.playMode = playMode;

            return XWSDK.getInstance().reportPlayState(stateInfo);
        }

        @Override
        public int getDeviceAlarmList(final IOnGetAlarmList listener) {
            QAILog.i(TAG, "getDeviceAlarmList: " + listener);
            if (listener == null) {
                return -100;
            }

            int ret = XWSDK.getInstance().getDeviceAlarmList(new XWSDK.GetAlarmListRspListener() {
                @Override
                public void onGetAlarmList(int errCode, String strVoiceID, String[] arrayAlarmList) {
                    QAILog.d(TAG, "onGetAlarmList() called with: errCode = [" + errCode
                            + "], strVoiceID = [" + strVoiceID + "], arrayAlarmList = ["
                            + arrayAlarmList + "]");

                    try {
                        listener.onGetAlarmList(errCode, strVoiceID, arrayAlarmList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            QAILog.d(TAG, "getDeviceAlarmList: ret " + ret);
            return ret;
        }

        @Override
        public int setDeviceAlarmInfo(int opType, String strAlarmJson, final IOnSetAlarmList listener) {
            QAILog.i(TAG, "setDeviceAlarmInfo: " + listener);
            if (listener == null) {
                return -100;
            }

            int ret = XWSDK.getInstance().setDeviceAlarmInfo(opType, strAlarmJson, new XWSDK.SetAlarmRspListener() {
                @Override
                public void onSetAlarmList(int errCode, String strVoiceID, int alarmId) {
                    QAILog.i(TAG, "onSetAlarmList() called with: errCode = ["
                            + errCode + "], strVoiceID = [" + strVoiceID + "], alarmId = [" + alarmId + "]");
                    try {
                        listener.onSetAlarmList(errCode, strVoiceID, alarmId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            QAILog.i(TAG, "setDeviceAlarmInfo: ret " + ret);
            return ret;
        }

        /**
         * @param service app注册时的服务类型，定义在AICoreDef.QLServiceType
         * @param state   app需要上报的状态值，定义在AICoreDef.AppState
         * @return
         */
        @Override
        public int updateAppState(String service, int state) {
            mCtrlCmd.updateAppState(service, state);
            return 0;
        }

        @Override
        public void getLoginStatus(XWAppInfo appInfo, final ICmdCallback callback) {
            QAILog.d(TAG, "getLoginStatus: Enter");
            if (appInfo == null || callback == null) {
                return;
            }

            XWSDK.getInstance().getLoginStatus(appInfo.ID, new XWSDK.RequestListener() {
                @Override
                public boolean onRequest(int event, XWResponseInfo rspData, byte[] extendData) {
                    QAILog.i(TAG, "getLoginStatus onRequest");
                    QLoveResponseInfo info = new QLoveResponseInfo(rspData, "");
                    try {
                        callback.handleQLoveResponseInfo(rspData.voiceID, info, extendData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }

    //-------------sdk methods end

    //-------------core interface begin
    @Override
    public void showMainWindowDelayed(int millis, String reason) {

    }

    @Override
    public void hideMainWindowDelayed(int millis, String reason) {

    }

    @Override
    public void showSpeechText(String text) {

    }

    @Override
    public void Speech2Core_onStartRecord() {

    }

    @Override
    public void Speech2Core_onStopRecord() {

    }

    @Override
    public void Speech2Core_onRecordVolumeChange(int volume) {

        String strText = QAICommandUtils.getVoiceResponsePacket(volume);
        mCommandHandler.dispatchAnimation(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_VOLUME, strText);
    }

    @Override
    public void Speech2Core_endOfSpeech(Object speechResult) {

    }

    @Override
    public void clients2Core_playText(String text) {

    }

    @Override
    public void window2Core_onWindowShown(boolean isShown) {

    }

    @Override
    public void skillSwitch(String oldSkill, String newSkill) {

    }

    @Override
    public void handleCmd(int command, Object param) {
        mHandler.obtainMessage(MSG_EXTERNAL_COMMAND, command, 0, param).sendToTarget();
    }


    @Override
    public void handleEventMethod(int type, int command, Object param) {
        mHandler.obtainMessage(MSG_EXTERNAL_COMMAND, type, command, param).sendToTarget();
    }

    @Override
    public void handleXWResponseData(String voiceId, XWResponseInfo rspData, byte[] extendData) {
        mCommandHandler.dispatchHandleXWResponseData(voiceId, rspData, extendData);
    }
    @Override
    public void handleGeneralCmd(String cmd) {
        mCommandHandler.dispatchHandleResponseData(cmd);
    }

    //-------------core interface end

    private void startGenericSkillService(Context c) {
        QAILog.d(TAG, "startGenericSkillService: Enter");
        String SvcPkg = "kinstalk.com.qloveaicore.genericskill";
        String SvcCls = "kinstalk.com.qloveaicore.genericskill.GenericSkillService";

        Intent i2 = new Intent(c, GenericSkillService.class);
        c.startService(i2);
    }

    private final BroadcastReceiver mPrivacyKeyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            QAILog.d(TAG, "onReceive: " + intent);
            String action = intent.getAction();

            if (TextUtils.equals(action, AICoreDef.ACTION_PRIVACY_KEY_SHORT_PRESS)) {
                QAILog.d(TAG, "onReceive: privacy pressed");
                mSpeechSM.manualWakeup();
            }
        }
    };

    private void registerPrivacyReceiver() {
        QAILog.d(TAG, "registerPrivacyReceiver: enter");
        IntentFilter intentFilter = new IntentFilter(AICoreDef.ACTION_PRIVACY_KEY_SHORT_PRESS);
        registerReceiver(mPrivacyKeyReceiver, intentFilter);
    }

    private void unRegisterPrivacyReceiver() {
        QAILog.d(TAG, "unRegisterPrivacyReceiver: enter");
        unregisterReceiver(mPrivacyKeyReceiver);
    }

}
