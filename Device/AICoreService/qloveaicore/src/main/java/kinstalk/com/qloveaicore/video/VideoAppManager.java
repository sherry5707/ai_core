package kinstalk.com.qloveaicore.video;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.util.JsonUtil;
import com.tencent.xiaowei.util.QLog;

import org.json.JSONObject;

import kinstalk.com.qloveaicore.AICoreDef;
import kinstalk.com.qloveaicore.ICmdCallback;
import kinstalk.com.qloveaicore.QAICoreService;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelVideoSkill;
import kinstalk.com.qloveaicore.statemachine.QAISpeechStatesMachine;

/**
 * Created by Zhigang Zhang on 2018/6/5.
 */

public class VideoAppManager {
    private static final String TAG = "VideoAppManager";
    private static final String INTENT_NAME_VIDEO = "点播视频";
    private static final String INTENT_NAME_OPEN_APP = "打开app";

    private static VideoAppManager sInstance;
    private Context mContext;

    private VideoAppManager() {

    }

    public synchronized static VideoAppManager getInstance() {
        if (sInstance == null) {
            sInstance = new VideoAppManager();
        }
        return sInstance;
    }

    private class CommandCallback implements ICmdCallback {

        @Override
        public String processCmd(String json) throws RemoteException {
            handleVideoCommand(json);
            return null;
        }

        @Override
        public void handleQLoveResponseInfo(String voiceId, QLoveResponseInfo rspData, byte[] extendData) throws RemoteException {
            //NOT USED
        }

        @Override
        public void handleWakeupEvent(int command, String data) throws RemoteException {
            //NOT USED
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    public void registerIfNeeded(QAICoreService service) {
        //register this local internal service into VoiceCmdHandler
        //if tencent agent is installed

        if(isTencentAgentInstalled(service)) {
            mContext = service;
            registerService(service);
        }
    }
    private void registerService(QAICoreService servic) {
        //internal service, no package, no svc class
        String param = buildJson(AICoreDef.QLServiceType.TYPE_VIDEO);
        QLog.d(TAG, "register internal service:" + param);
        servic.registerInternalService(param, new CommandCallback());

    }
    private String buildJson(String type) {
        JSONObject json = new JSONObject();
        try {
            json.put(AICoreDef.AI_JSON_FIELD_TYPE, type);
            json.put(AICoreDef.AI_JSON_FIELD_PACKAGE, "");
            json.put(AICoreDef.AI_JSON_FIELD_SERVICECLASS, "");
            json.put(AICoreDef.AI_JSON_FIELD_DEFAULTCLIENT, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private void handleVideoCommand(String json) {
        QLog.d(TAG, "handleVideoCommand: " + json);

        ModelVideoSkill videoSkill = JsonUtil.getObject(json, ModelVideoSkill.class);
        if (videoSkill == null) {
            return;
        }
        videoSkill = videoSkill.init();
        if (INTENT_NAME_VIDEO.equals(videoSkill.intentName)) {
            if (CartoonVideoHandler.handleCartoonVideo(videoSkill)){
                QLog.d(TAG, "open CartoonVideo");
            } else {
                QLog.d(TAG, "send trigger intent to startup Tencent Video, notify wakeup");
                QAISpeechStatesMachine.getInstance().queueWakeupNotification();
                doStartTencentVideoAPP(mContext);
            }
            //TODO, based on json, wakeup tencent or not
        } else if(INTENT_NAME_OPEN_APP.equals(videoSkill.intentName)) {
            QLog.d(TAG, "open app intent, just send trigger intent to startup Tencent Video");
            doStartTencentVideoAPP(mContext);
        }
    }

    private static final String TENCENT_VIDEO_AGENT_PACKAGE = "com.ktcp.aiagent";
    private static final String TENCENT_VOIDEO_AGENT_TRGGER_ACTION = "com.ktcp.aiagent.voice.trigger";
    private boolean isTencentAgentInstalled(Context context) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(TENCENT_VIDEO_AGENT_PACKAGE, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return false;
        } else {
            return true;
        }
    }

    private void doStartTencentVideoAPP(Context context) {
        Intent intent = new Intent(TENCENT_VOIDEO_AGENT_TRGGER_ACTION);
        intent.setPackage(TENCENT_VIDEO_AGENT_PACKAGE);
        context.sendBroadcast(intent);
    }
}