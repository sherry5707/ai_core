package kinstalk.com.qloveaicore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.kinstalk.her.voip.manager.AVChatManager;
import com.tencent.xiaowei.def.XWCommonDef;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;
import com.tencent.xiaowei.info.XWResponseInfo;
import com.tencent.xiaowei.sdk.XWDeviceBaseManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kinstalk.com.common.utils.CountlyEvents;
import kinstalk.com.common.utils.QAICommandUtils;
import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.QAIUtils;
import kinstalk.com.common.utils.Tuple3;
import kinstalk.com.qloveaicore.AICoreDef.CtrlCmdType;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelCameraSkill;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelColourlifeSkill;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelFittimeSkill;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelGiftSkill;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelQinjianSkill;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelSettingsSkill;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelTimerSkill;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelXiXinHealthSkill;
import kinstalk.com.qloveaicore.qlovenlp.utils.ReflectionUtils;
import kinstalk.com.qloveaicore.wakeup.tencent.RecordDataManager;

/**
 * Created by majorxia on 2017/4/10.
 * main controller to handle text command
 */

public class VoiceCmdHandler {
    private static final String TAG = "AI-VoiceCmdHandler";

    private static final String INTENT_NAME_GALLERY = "viewPhoto";

    private final AIClients mClients = AIClients.getInstance();
    private static final HandlerThread sHT = new HandlerThread("VoiceCmdHandler thread");
    private static PinganVoiceId pinganVoiceId = new PinganVoiceId();
    private static Handler sH = null;
    private final Map<String, String> mTypeMap = new HashMap<>();
    private final Map<String, String> mSkillMap = new HashMap<>();
    private final Context mContext;
    private CommonSkillClient skillClient;

    public VoiceCmdHandler(Context c) {
        sHT.start();
        mContext = c;
        setupServiceMap();
        setupSkillMap();
        skillClient = new CommonSkillClient(mContext);
    }

    private void setupServiceMap() {
        String testSuffix = "";
        boolean useTestService = QAIConfigController.readEnableWithDefault(mContext,
                QAIConstants.SHARED_PREFERENCE_KEY_USE_TEST_SERVICE_TYPE, false);

        if (useTestService) testSuffix = "22";

        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_MUSIC, AICoreDef.QLServiceType.TYPE_MUSIC + testSuffix);
        mTypeMap.put("微信-新闻", AICoreDef.QLServiceType.TYPE_NEWS + testSuffix);
        mTypeMap.put("搜狗-百科", AICoreDef.QLServiceType.TYPE_WIKI + testSuffix);
        mTypeMap.put("AILab-百科", AICoreDef.QLServiceType.TYPE_WIKI + testSuffix);
        mTypeMap.put("FM-笑话", AICoreDef.QLServiceType.TYPE_FM + testSuffix);
        mTypeMap.put("FM-电台", AICoreDef.QLServiceType.TYPE_FM + testSuffix);
        mTypeMap.put("FM-小说", AICoreDef.QLServiceType.TYPE_FM + testSuffix);
        mTypeMap.put("FM-相声", AICoreDef.QLServiceType.TYPE_FM + testSuffix);
        mTypeMap.put("FM-评书", AICoreDef.QLServiceType.TYPE_FM + testSuffix);
        mTypeMap.put("FM-故事", AICoreDef.QLServiceType.TYPE_FM + testSuffix);
        mTypeMap.put("FM-杂烩", AICoreDef.QLServiceType.TYPE_FM + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_WEATHER, AICoreDef.QLServiceType.TYPE_WEATHER + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_ALARM, AICoreDef.QLServiceType.TYPE_SCHEDULE + testSuffix);
        mTypeMap.put("微信-闲聊", AICoreDef.QLServiceType.TYPE_GENERIC); //genericSkill no suffix
        mTypeMap.put("搜狗-计算器", AICoreDef.QLServiceType.TYPE_GENERIC);
        mTypeMap.put("AILab-翻译", AICoreDef.QLServiceType.TYPE_GENERIC);
        mTypeMap.put("搜狗-当前时间", AICoreDef.QLServiceType.TYPE_GENERIC);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_TIMEREMINDER, AICoreDef.QLServiceType.TYPE_GENERIC);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_CAMERA, AICoreDef.QLServiceType.TYPE_CAMERA + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_FITTIME, AICoreDef.QLServiceType.TYPE_FITTIME + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_LAUNCHER, AICoreDef.QLServiceType.TYPE_LAUNCHER + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_SETTINGS, AICoreDef.QLServiceType.TYPE_SETTINGS + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_COLOURLIFE, AICoreDef.QLServiceType.TYPE_COLOURLIFE + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_HEALRH, AICoreDef.QLServiceType.TYPE_LAUNCHER + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_VIDEO, AICoreDef.QLServiceType.TYPE_VIDEO + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_HONGEN,AICoreDef.QLServiceType.TYPE_HONGEN + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_XiXinHealth,AICoreDef.QLServiceType.TYPE_HEALTH + testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_QQTEL,AICoreDef.QLServiceType.TYPE_CALL +testSuffix);
        mTypeMap.put(AICoreDef.C_DEF_TXCA_SKILL_NAME_WAKEUP_OR_SLEEP, AICoreDef.QLServiceType.TYPE_WAKEUP_SLEEP_MODE + testSuffix);
    }

    private void setupSkillMap() {
        mSkillMap.put(AICoreDef.C_DEF_TXCA_SKILL_ID_UNKNOWN, AICoreDef.QLServiceType.TYPE_GENERIC);
        mSkillMap.put(AICoreDef.C_DEF_TXCA_SKILL_ID_UNKNOWN_IOT, AICoreDef.QLServiceType.TYPE_GENERIC);
    }

    public void dispatchClientRegister(String jsonParam, ICmdCallback cb) {
        QAILog.d(TAG, "dispatchClientRegister: enter," + jsonParam);

        Tuple3<String, ComponentName, Boolean> r = AIClients.getInfoFromJson(jsonParam);

        CountlyEvents.clientRegister(r);

        if (r != null) {
            mClients.addNewClient(r.first, r.second, null, cb.asBinder(), cb, r.third);
        }
    }

    public void dispatchClientUnRegister(String jsonParam) {
        QAILog.d(TAG, "dispatchClientUnRegister: enter");
        Pair<String, ComponentName> p = AIClients.getComponentInfoFromJson(jsonParam);

        CountlyEvents.clientUnregister(p);

        if (p != null) {
            mClients.removeClientByType(p.first);
        }
    }

    public void handleGetData(final String jsonParam, final ICmdCallback cb) {
        QAILog.d(TAG, "dispatchGetData: " + jsonParam);
        if (TextUtils.isEmpty(jsonParam)) {
            CountlyEvents.clientRequestDataEmpty();
        }

        String cmd = null;
        JSONObject json = null;

        try {
            json = new JSONObject(jsonParam);
            cmd = json.optString(AICoreDef.GET_DATA_CMD_STR);
        } catch (JSONException e) {
            e.printStackTrace();
            CountlyEvents.clientRequestDataError();
        }

        if (TextUtils.equals(cmd, AICoreDef.GET_DATA_CMD_GET_OWNER)) {
            /*try {
                cb.processCmd(DeviceBindStateController.getInst(mContext).getOwnerInfo());
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
        } else if (TextUtils.equals(cmd, AICoreDef.GET_DATA_CMD_ERASE_ALL_BINDERS)) {
            XWDeviceBaseManager.unBind(new XWDeviceBaseManager.OnUnBindListener() {
                @Override
                public void onResult(int error) {
                    QAILog.i(TAG, "unBind onResult:" + error);
                }
            });
        } else if (TextUtils.equals(cmd, AICoreDef.GET_DATA_EGG_CLICK)) {
            //add by wangyong wateranimal clicked
            String strText = QAICommandUtils.getAnimationResponsePacket(AICoreDef.WATER_ANIM_CMD_ENDOFSPEECH, "");
            dispatchAnimation(2, AICoreDef.WATER_ANIM_CMD_ENDOFSPEECH, strText);
            RecordDataManager.getInstance().onSleep();
        }
    }

    public void dispatchVoiceCommand(String jsonCommand) {
        QAILog.d(TAG, "dispatchVoiceCommand: enter, " + jsonCommand);
        /*
        try {
            JSONObject json = new JSONObject(jsonCommand);
            int code = json.optInt("code");
            if (code == -1) {
                return;
            }

            String type = json.optString("service");
            JSONObject answerjson = json.optJSONObject("answer");
            if (answerjson != null) {
                String speechtext = answerjson.optString("text");
                int playtts = json.optInt("playtts");
                if (playtts == 1) {
                    TXAIAudioSDK.getInstance().requestTTS(speechtext);
                }
            }

            // Handle requestDataWithCallBack first
            String voiceID = "";
            if (json.has("voiceID")) {
                voiceID = json.optString("voiceID");
                if (!TextUtils.isEmpty(voiceID) && RequestDataClients.getInstance().handleCbWithVoiceID(voiceID, jsonCommand)) {
                    return ;
                }
            }

            trySendToCallback(jsonCommand, type, voiceID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    public void dispatchAnimation(int type, int cmd, String object) {
        QAILog.i(TAG, "dispatchAnimation: " + cmd);
        trySendToCallback(cmd, object, AICoreDef.WATER_ANIM_CLIENT_TYPE, VOICEID_APP_TYPE_ANIMATION);
    }

    public void dispatchHandleXWResponseData(String voiceId, XWResponseInfo rspData, byte[] extendData) {
//        if(skillClient.handleLocalSkillData(rspData)){
//            return;
//        }
        if (rspData != null && rspData.appInfo != null) {
            String type = "";
            QLoveResponseInfo qRspInfo = new QLoveResponseInfo(rspData, type);

            int controlType = handleCtrlCmd(voiceId, qRspInfo, extendData);
            QAILog.i(TAG, "dispatchHandleXWResponseData controlType:" + controlType);

            if (controlType == CtrlCmdType.TYPE_COMMON) {
                type = ControlCommandController.getInstance().getRouteService();
            } else if (controlType == CtrlCmdType.TYPE_VOLUME) {
                type = mTypeMap.get(AICoreDef.C_DEF_TXCA_SKILL_NAME_SETTINGS);
            } else {
                type = matchServiceType(rspData);
            }

            QAILog.i(TAG, "dispatchHandleXWResponseData type:" + type);
            if (TextUtils.isEmpty(type)) {
                return;
            }
            //XWResGroupInfo{resources=[XWResourceInfo{format=5, ID='11020', content='144115210831033930', extendInfo='', offset=0, playCount=0}]}
            //XWResGroupInfo{resources=[XWResourceInfo{format=2, ID='KSPKJRKUCSERZNXGQPZUYIYYQIACYWFJ', content='正在为你拨打妹妹的电话', extendInfo='', offset=0, playCount=1}]}
            if (type.equals(AICoreDef.QLServiceType.TYPE_CALL)) {
                final int command = AVChatManager.checkCommandId(rspData.resources);
                final String value = AVChatManager.getCommandValue(rspData.resources);
                if(command == AVChatManager.QQCALL_REQUEST||command == AVChatManager.QQCALL_REQUEST_NEW){

                    QAICoreService.getInstance().mControl.playTextWithId(voiceId, new ITTSCallback() {
                        @Override
                        public void onTTSPlayBegin(String voiceId) throws RemoteException {
                        }

                        @Override
                        public void onTTSPlayEnd(String voiceId) throws RemoteException {
                            Intent intent = new Intent();
                            intent.setAction(LauncherCmdHandler.ACTION_MAKE_VIDOE_CALL);
                            intent.putExtra("command",command);//语音指令
                            intent.putExtra("value",value);//peerId
                            mContext.sendBroadcast(intent);
                        }

                        @Override
                        public void onTTSPlayProgress(String voiceId, int progress) throws RemoteException {

                        }

                        @Override
                        public void onTTSPlayError(String voiceId, int errCode, String errString) throws RemoteException {

                        }

                        @Override
                        public IBinder asBinder() {
                            return null;
                        }
                    });
                }else {
                    QAICoreService.getInstance().mControl.playTextWithId(voiceId,null);
                    //接听或挂断
                    Intent intent = new Intent();
                    intent.setAction(LauncherCmdHandler.ACTION_MAKE_VIDOE_CALL);
                    intent.putExtra("command",command);//语音指令
                    intent.putExtra("value",value);//peerId
                    mContext.sendBroadcast(intent);
                }

                return;
            }
            qRspInfo.qServiceType = type;
            AIClients.ClientInfo ci = AIClients.getInstance().getClientInfoByType(type);
            if (ci != null) {
                try {
                    Log.d(TAG, "dispatchHandleXWResponseData: type:" + type + " \nrspData:" + rspData);
                    if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_COUNTDOWN)) {
                        ci.callback.processCmd(ModelTimerSkill.getSkillJsonData(rspData.voiceID, rspData.requestText, rspData.responseData));
                    } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_CAMERA)) {
                        ci.callback.processCmd(ReflectionUtils.convObjectToJSON(ModelCameraSkill.optSkillData(rspData.responseData)).toString());
                    } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_GALLERY)) {
                        ci.callback.processCmd(ReflectionUtils.convObjectToJSON(ModelCameraSkill.optSkillData(rspData.responseData)).toString());
                    } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_FITTIME)) {
                        String intentName = new JSONObject(rspData.responseData).optString("intentName");
                        if (TextUtils.equals(intentName, "OpenFittime")){
                            LauncherCmdHandler.handleCmd(rspData);
                        }else {
                            ci.callback.processCmd(ModelFittimeSkill.getSkillDataJson(rspData.responseData, rspData.requestText
                                    , rspData.voiceID));
                        }
                    } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_SETTINGS)) {
                        if (controlType == CtrlCmdType.TYPE_VOLUME) {
                            ci.callback.processCmd(ReflectionUtils.convObjectToJSON(ModelSettingsSkill
                                    .optVolumeSkillData(rspData)).toString());
                        } else {
                            ci.callback.processCmd(ReflectionUtils.convObjectToJSON(ModelSettingsSkill
                                    .optSkillData(rspData.responseData, rspData.requestText, rspData.voiceID)).toString());
                        }
                    } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_COLOURLIFE)) {
                        ci.callback.processCmd(ModelColourlifeSkill.getSkillDataJson(rspData.responseData, rspData.requestText
                                , rspData.voiceID));
                    } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_QINJIAN)) {
                        ci.callback.processCmd(ModelQinjianSkill.getSkillJsonData(rspData.responseData, rspData.requestText, rspData.voiceID));
                    } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_VIDEO)) {
                        ci.callback.processCmd(rspData.responseData);
                    } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_HEALTH)) {
                        if ( BuildConfig.cfgVoicePrint == true ) {
                            pinganVoiceId.sendAudioRecongnition(mContext, rspData.responseData);
                        }
                        ci.callback.processCmd(ModelXiXinHealthSkill.getSkillDataJson(rspData.responseData, rspData.requestText, rspData.voiceID));
                    }else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_WAKEUP_SLEEP_MODE)) {
                        ci.callback.processCmd(rspData.responseData);
                    }else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_GIFT)) {
                        ci.callback.processCmd(ModelGiftSkill.getSkillJsonData(rspData.responseData, rspData.requestText, rspData.voiceID));
                    }else {
                        ci.callback.handleQLoveResponseInfo(voiceId, qRspInfo, extendData);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                QAILog.d(TAG, "dispatchHandleXWResponseData type: " + "launcher skill " + rspData);
                LauncherCmdHandler.handleCmd(rspData);
            }
        }
    }

    public void dispatchHandleResponseData(String cmd){
        QAILog.d(TAG, "dispatchHandleResponseData cmd");
        String type = matchLocalType(cmd);
        AIClients.ClientInfo ci;
        if(TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_SETTINGS_VOL)){
            ci = AIClients.getInstance().getClientInfoByType(AICoreDef.QLServiceType.TYPE_SETTINGS);
        }else{
            ci = AIClients.getInstance().getClientInfoByType(type);
        }
        if (ci != null) {
            try {
                Log.d(TAG, "dispatchHandleXWResponseData: type:" + type + " \ncmd:" + cmd);
                if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_COUNTDOWN)) {
                    //ci.callback.processCmd(ModelTimerSkill.getSkillJsonData(rspData.voiceID, rspData.requestText, rspData.responseData));
                } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_CAMERA)) {
                    ci.callback.processCmd(ReflectionUtils.convObjectToJSON(ModelCameraSkill.optSkillData(cmd)).toString());
                } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_GALLERY)) {
                    ci.callback.processCmd(ReflectionUtils.convObjectToJSON(ModelCameraSkill.optSkillData(cmd)).toString());
                } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_FITTIME)) {
                    //ci.callback.processCmd(ModelFittimeSkill.getSkillDataJson(rspData.responseData, rspData.requestText, rspData.voiceID));
                }else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_SETTINGS_VOL)) {
                    ci.callback.processCmd(ReflectionUtils.convObjectToJSON(ModelSettingsSkill
                            .optVolumeSkillData(cmd)).toString());

                } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_SETTINGS)) {
                        ci.callback.processCmd(ReflectionUtils.convObjectToJSON(ModelSettingsSkill
                                .optSkillData(cmd, null, null)).toString());

                } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_COLOURLIFE)) {
                    //ci.callback.processCmd(ModelColourlifeSkill.getSkillDataJson(rspData.responseData, rspData.requestText
                    //, rspData.voiceID));
                } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_QINJIAN)) {
                    //ci.callback.processCmd(ModelQinjianSkill.getSkillJsonData(rspData.responseData, rspData.requestText, rspData.voiceID));
                } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_VIDEO)) {
                    ci.callback.processCmd(cmd);
                } else if (TextUtils.equals(type, AICoreDef.QLServiceType.TYPE_HEALTH)) {
                    if ( BuildConfig.cfgVoicePrint == true ) {
                        pinganVoiceId.sendAudioRecongnition(mContext, cmd);
                    }
                    ci.callback.processCmd(ModelXiXinHealthSkill.getSkillDataJson(cmd, "", null));
                }else {
                    //ci.callback.handleQLoveResponseInfo(voiceId, qRspInfo, extendData);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            LauncherCmdHandler.handleCmd(cmd);
        }
    }

    private String matchLocalType(String cmd){
        String type = null;

        try {
            JSONObject responseObject = new JSONObject(cmd);
            String intentName = responseObject.optString("intentName");

            if (intentName == null) {
                return type;
            }

            switch (intentName) {
                case INTENT_NAME_GALLERY:
                    type = AICoreDef.QLServiceType.TYPE_GALLERY;
                    break;
                case LauncherCmdHandler.INTENT_NAME_OPEN_CAMERA:
                case LauncherCmdHandler.INTENT_NAME_TAKE_PHOTO:
                    type = AICoreDef.QLServiceType.TYPE_CAMERA;
                    break;
                case LauncherCmdHandler.INTENT_NAME_OFF_SCREEN:
                case LauncherCmdHandler.INTENT_NAME_HOME_BACK:
                    type = AICoreDef.QLServiceType.TYPE_SETTINGS;
                    break;
                case LauncherCmdHandler.INTENT_NAME_VOL_UP:
                case LauncherCmdHandler.INTENT_NAME_VOL_DOWN:
                    type = AICoreDef.QLServiceType.TYPE_SETTINGS_VOL;
                    break;
                case LauncherCmdHandler.INTENT_NAME_TIMER:
                    type = AICoreDef.QLServiceType.TYPE_COUNTDOWN;
                    break;
                case LauncherCmdHandler.INTENT_NAME_QINJIAN:
                    type = AICoreDef.QLServiceType.TYPE_QINJIAN;
                    break;
                case LauncherCmdHandler.INTENT_NAME_SCHEDULE:
                    type = AICoreDef.QLServiceType.TYPE_SCHEDULE;
                    break;
                case LauncherCmdHandler.INTENT_NAME_ADVICE:
                case LauncherCmdHandler.INTENT_NAME_CONTACTS:
                case LauncherCmdHandler.INTENT_NAME_CALL_LOG:
                case LauncherCmdHandler.INTENT_NAME_KIDS_VIDEO:
                    type = AICoreDef.QLServiceType.TYPE_LAUNCHER;
                    break;
                case LauncherCmdHandler.INTENT_CheckBloodGlucose:
                case LauncherCmdHandler.INTENT_CheckBloodGlucoseReport:
                case LauncherCmdHandler.INTENT_CheckBloodPressure:
                case LauncherCmdHandler.INTENT_CheckBloodPressureReport:
                case LauncherCmdHandler.INTENT_CheckHealthFile:
                case LauncherCmdHandler.INTENT_ContactXixinHealth:
                case LauncherCmdHandler.INTENT_MeasureBloodGlucose:
                case LauncherCmdHandler.INTENT_MeasureBloodPressure:
                case LauncherCmdHandler.INTENT_OpenXinxinHealth:
                case LauncherCmdHandler.INTENT_RecordBloodGlucose:
                case LauncherCmdHandler.INTENT_RecordBloodPressure:
                    type = AICoreDef.QLServiceType.TYPE_HEALTH;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;

    }

    private String matchServiceType(XWResponseInfo rspData) {
        if (rspData.appInfo == null) {
            return null;
        }

        String type;
        if (mSkillMap.containsKey(rspData.appInfo.ID))
            type = mSkillMap.get(rspData.appInfo.ID);
        else
            type = mTypeMap.get(rspData.appInfo.name);

        if (!AICoreDef.QLServiceType.TYPE_LAUNCHER.equals(type)
                && !AICoreDef.QLServiceType.TYPE_CAMERA.equals(type)
                && !AICoreDef.QLServiceType.TYPE_WAKEUP_SLEEP_MODE.equals(type)) {
            return type;
        }
        JSONObject responseObject;
        try {
            responseObject = new JSONObject(rspData.responseData);
            String intentName = responseObject.optString("intentName");
            if (intentName == null) {
                return type;
            }

            switch (intentName) {
                case INTENT_NAME_GALLERY:
                    type = AICoreDef.QLServiceType.TYPE_GALLERY;
                    break;
                case LauncherCmdHandler.INTENT_NAME_TIMER:
                    type = AICoreDef.QLServiceType.TYPE_COUNTDOWN;
                    break;
                case LauncherCmdHandler.INTENT_NAME_QINJIAN:
                    type = AICoreDef.QLServiceType.TYPE_QINJIAN;
                    break;
                case LauncherCmdHandler.INTENT_NAME_SCHEDULE:
                    type = AICoreDef.QLServiceType.TYPE_SCHEDULE;
                    break;
                case LauncherCmdHandler.INTENT_NAME_ADVICE:
                case LauncherCmdHandler.INTENT_NAME_CONTACTS:
                case LauncherCmdHandler.INTENT_NAME_CALL_LOG:
                case LauncherCmdHandler.INTENT_NAME_KIDS_VIDEO:
                    type = AICoreDef.QLServiceType.TYPE_LAUNCHER;
                    break;
                case LauncherCmdHandler.INTENT_CheckBloodGlucose:
                case LauncherCmdHandler.INTENT_CheckBloodGlucoseReport:
                case LauncherCmdHandler.INTENT_CheckBloodPressure:
                case LauncherCmdHandler.INTENT_CheckBloodPressureReport:
                case LauncherCmdHandler.INTENT_CheckHealthFile:
                case LauncherCmdHandler.INTENT_ContactXixinHealth:
                case LauncherCmdHandler.INTENT_MeasureBloodGlucose:
                case LauncherCmdHandler.INTENT_MeasureBloodPressure:
                case LauncherCmdHandler.INTENT_OpenXinxinHealth:
                case LauncherCmdHandler.INTENT_RecordBloodGlucose:
                case LauncherCmdHandler.INTENT_RecordBloodPressure:
                    type = AICoreDef.QLServiceType.TYPE_HEALTH;
                    break;
                case LauncherCmdHandler.INTENT_wakeup_sendMessage:
                case LauncherCmdHandler.INTENT_wakeup_viewMessage:
                case LauncherCmdHandler.INTENT_wakeup_openReminder:
                case LauncherCmdHandler.INTENT_wakeup_openGift:
                    type = AICoreDef.QLServiceType.TYPE_GIFT;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;
    }

    private int handleCtrlCmd(String voiceId, QLoveResponseInfo qRspData, byte[] extendData) {
        int controlType = CtrlCmdType.TYPE_NONE;
        XWResponseInfo xwResponseInfo = qRspData.xwResponseInfo;
        if (xwResponseInfo.appInfo != null && !TextUtils.isEmpty(xwResponseInfo.appInfo.name)
                && xwResponseInfo.appInfo.name.contains("通用控制")) {

            boolean isVolume = false;
            XWResGroupInfo[] resources = xwResponseInfo.resources;
            if (resources != null && resources.length == 1) {
                XWResourceInfo[] resources1 = resources[0].resources;
                if (resources1 != null && resources1.length == 1) {
                    XWResourceInfo resourceInfo = resources1[0];
                    if (resourceInfo.format == XWCommonDef.ResourceFormat.COMMAND &&
                            (TextUtils.equals("700101", resourceInfo.ID)
                                    || TextUtils.equals("700150", resourceInfo.ID)
                                    || TextUtils.equals("700151", resourceInfo.ID)
                                    || TextUtils.equals("700001", resourceInfo.ID)
                                    || TextUtils.equals("700002", resourceInfo.ID)
                                    || TextUtils.equals("700128", resourceInfo.ID))) {
                        isVolume = true;
                    }
                }
            }


            QAILog.d(TAG, "handleCtrlCmd isVolume:" + isVolume);
            if (!isVolume) {
                qRspData.isControlCmd = true;
                int propId = QAIUtils.getPropertyIdFromXWRspInfo(xwResponseInfo);
                qRspData.ctrlCommandInfo.command = QAIUtils.getQCmdFromProp(propId);
                qRspData.ctrlCommandInfo.requestText = xwResponseInfo.requestText;
                qRspData.ctrlCommandInfo.skillId = xwResponseInfo.appInfo.ID;
                qRspData.ctrlCommandInfo.skillName = xwResponseInfo.appInfo.name;
                controlType = CtrlCmdType.TYPE_COMMON;
            } else {
                controlType = CtrlCmdType.TYPE_VOLUME;
            }
        }
        return controlType;
    }

    public static void sendToCallback(int command, String result, String type) {
        trySendToCallback(command, result, type, VOICEID_TYPE_LOCAL);
    }

    private static final String VOICEID_TYPE_LOCAL = "HandledLocally";
    private static final String VOICEID_APP_TYPE_ANIMATION = "DispatchAnimation";
    private static final int SEND_TO_CALLBACK_RETRY_TIMES = 3;

    /**
     * Add retry protect since service in app maybe killed by LMK
     * linkToDeath restarting service in app needs about 300ms
     */
    private static void trySendToCallback(final int command, final String result, final String type, final String voiceID) {
        if (sH == null) {
            sH = new Handler(sHT.getLooper());
        }
        sH.post(new Runnable() {
            @Override
            public void run() {
                int tryCount = 1;
                boolean isSuccess = sendToCallback(command, result, type, voiceID);
                while (!isSuccess && tryCount <= SEND_TO_CALLBACK_RETRY_TIMES) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tryCount++;
                    QAILog.i(TAG, "retrySendToCallback: tryCount ： " + tryCount);
                    // retry sendToCallback
                    isSuccess = sendToCallback(command, result, type, voiceID);
                }
            }
        });
    }

    private static boolean sendToCallback(int command, String result, String type, String voiceID) {

        QAILog.v(TAG, "send command to client. Command= "+command+" result="+result+" type="+type);
        AIClients.ClientInfo ci = AIClients.getInstance().getClientInfoByType(type);
        if (ci != null) {
            boolean isSendSuccess = false;
            ICmdCallback cb = ci.callback;
            final ComponentName comp = ci.component;
            try {
                cb.handleWakeupEvent(command, result);
                if (!TextUtils.equals(VOICEID_APP_TYPE_ANIMATION, voiceID)) {
                    QAILog.kpi(TAG, "ntp:advtech:sendToCallback:processCmd, voiceID= " + voiceID);
                    QAILog.d(TAG, "sendToCallback:processCmd, voiceID= " + voiceID);
                }
                isSendSuccess = true;
            } catch (DeadObjectException e) {
                // the remote client was disconnected
                QAILog.w(TAG, "sendToCallback: DeadObjectException, remote disconnected");
            } catch (RemoteException e) {
                QAILog.w(TAG, "sendToCallback: RemoteException, remote disconnected");
            }

            if (!isSendSuccess) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(comp);
//                    BaseApplication.getInstance().getApplicationContext().startService(intent);
                } catch (Exception e1) {
                    QAILog.w(TAG, "sendToCallback: startService Exception, ComponentName： " + comp);
                }
            }
            return isSendSuccess;
        } else {
            return true;
        }
    }

}
