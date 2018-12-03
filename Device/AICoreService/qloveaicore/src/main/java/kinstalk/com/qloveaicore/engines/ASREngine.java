package kinstalk.com.qloveaicore.engines;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AICloudASREngine;
import com.aispeech.export.engines.AILocalASREngine;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalGrammarListener;
import com.aispeech.speech.AppKey;
import com.aispeech.util.GrammarHelper;
import com.aispeech.util.SampleConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.SystemTool;
import kinstalk.com.qloveaicore.BuildConfig;
import kinstalk.com.qloveaicore.LauncherCmdHandler;
import kinstalk.com.qloveaicore.statemachine.QAISpeechStatesMachine;
import kinstalk.com.qloveaicore.wakeup.tencent.RecordDataManager;

/**
 * Created by shaoyi on 2018/5/2.
 */

public class ASREngine extends Engine {
    private static ASREngine sInst = null;
    protected static final String TAG = "AI-ASREngine";
    private int mCurrEngine = QAIConstants.ENGINE_TX_CLOUD_ASR;//|QAIConstants.ENGINE_SPEECH_LOCAL_ASR;
    AICloudASREngine mAICloudASREngine = null;
    AILocalASREngine mAsrLocalEngine = null;
    AILocalGrammarEngine mGrammarEngine = null;
    private boolean mAIAuth= false;

    //-------------------TX Local ASR Engine---------------------
    RecordDataManager mTXCloudASREngine = null;
    Context mContext = null;
    private boolean mAICloudASREngineInitialized = false;
    private boolean mAILocalASREngineInitialized = false;

    private ASRListener mListener = null;
    public static ASREngine getInstance(Context c) {
        if (sInst == null) {
            QAILog.v(TAG, "Create ASR Engine.");
            sInst = new ASREngine(c);
        }
        return sInst;
    }

    private ASREngine(Context c) {
        //super(c);
        mContext = c;
        init();
    }



    @Override
    public boolean init(){
        QAILog.v(TAG, "Init ASR Engine. Just XW");

        if((mCurrEngine & QAIConstants.ENGINE_TX_CLOUD_ASR) == QAIConstants.ENGINE_TX_CLOUD_ASR){
            QAILog.v(TAG, "Init Tencent ASR Engine.");
            mTXCloudASREngine = RecordDataManager.getInstance();
        }
        /*if(initSpeechCloudASR()){
            QAILog.v(TAG, "Init Speech Cloud ASR Engine success.");
        }*/
        return true;
    }
    public void initLocal(){
        if((mCurrEngine & QAIConstants.ENGINE_SPEECH_LOCAL_ASR) == QAIConstants.ENGINE_SPEECH_LOCAL_ASR)  {
        if (initSpeechLocalASR()) {
            QAILog.v(TAG, "Init Speech Cloud ASR Engine success.");
        }
    }}

    private boolean initSpeechLocalASR(){
        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
        }
        QAILog.i(TAG, "grammar create");
        mGrammarEngine = AILocalGrammarEngine.createInstance();
//        mGrammarEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下
        mGrammarEngine.setResFileName(SampleConstants.RES_EBNFC);
        mGrammarEngine
                .init(mContext, new AILocalGrammarListenerImpl(), BuildConfig.APPKEY, BuildConfig.SECRETKEY);
        mGrammarEngine.setDeviceId(Util.getIMEI(mContext));
        return true;
    }

    private void startResGen() {
        // 生成ebnf语法
        GrammarHelper gh = new GrammarHelper(mContext);
        String contactString = gh.getConatcts();
        contactString = "";
        String appString = gh.getApps();
        // 如果手机通讯录没有联系人
        if (TextUtils.isEmpty(contactString)) {
            contactString = "无联系人";
        }
        String ebnf = gh.importAssets("", "", "grammar.xbnf");
        Log.i(TAG, ebnf);
        // 设置ebnf语法
        mGrammarEngine.setEbnf(ebnf);
        // 启动语法编译引擎，更新资源
        mGrammarEngine.update();
    }

    public  class AILocalGrammarListenerImpl implements AILocalGrammarListener {

        @Override
        public void onError(AIError error) {
            QAILog.e("AILocalGrammar Error.");
        }

        @Override
        public void onUpdateCompleted(String recordId, String path) {
            QAILog.i(TAG, "Resource generate/update\npath=" + path + "\nReload ASR ...");
            initLocalAsr();
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                QAILog.i(TAG, "init engine success.");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startResGen();
                        //initLocalAsr();
                    }
                },"AILocalGrammerThread").start();
            } else {
                QAILog.i(TAG, "init engine failed.");
            }
        }
    }

    private void initLocalAsr() {
        mAsrLocalEngine = AILocalASREngine.createInstance();
//        mEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下

        mAsrLocalEngine.setResBin(SampleConstants.RES_EBNFR);
        mAsrLocalEngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME,true);//AILocalGrammarEngine.OUTPUT_NAME
        mAsrLocalEngine.setUseConf(true);
        mAsrLocalEngine.setUseXbnfRec(true);
        mAsrLocalEngine.setVadEnable(true);
        mAsrLocalEngine.setVadResource(SampleConstants.RES_VAD);
        mAsrLocalEngine.setUserId("anonymous");
        // mEngine.setPauseTime(500);
        mAsrLocalEngine.setDeviceId(Util.getIMEI(mContext));
        mAsrLocalEngine.init(mContext, new AIASRListenerImpl(), BuildConfig.APPKEY, BuildConfig.SECRETKEY);
//        mEngine.setNBest(3);
        mAsrLocalEngine.setUseConf(true);
        mAsrLocalEngine.setUseCustomFeed(true);
    }
    public class AIASRListenerImpl implements AIASRListener {

        @Override
        public void onBeginningOfSpeech() {
            QAILog.i(TAG, "Local engine speech detected.");

        }

        @Override
        public void onEndOfSpeech() {
            QAILog.i(TAG, "Speech stop detected. Start recognize...");
        }

        @Override
        public void onReadyForSpeech() {
            QAILog.i(TAG,"Ready for speech. ");
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onError(AIError error) {
            QAILog.e(TAG, "Local engine error detected:"+ error.getErrId());
        }
        private String preparseRes(JSONObject in){
            int size = in.length();
            JSONObject res = new JSONObject();
            if(size > 0){
                try {
                    String action = in.getString(LauncherCmdHandler.STRING_ACTION);
                    if(TextUtils.equals(action,LauncherCmdHandler.STRING_OPEN)){
                        String appName = in.getString(LauncherCmdHandler.STRING_APP);
                        if(TextUtils.equals(appName,LauncherCmdHandler.STRING_APP_HONGEN)){
                            res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_NAME_OPEN_HONGEN);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE,"COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS,"NONE");
                            return res.toString();
                        }
                        else if(TextUtils.equals(appName,LauncherCmdHandler.STRING_APP_CONTACTS)){
                            res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_NAME_CONTACTS);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE,"COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS,"NONE");
                            return res.toString();
                        }
                        else if(TextUtils.equals(appName,LauncherCmdHandler.STRING_APP_CALLLOG)){
                            res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_NAME_CALL_LOG);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE,"COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS,"NONE");
                            return res.toString();
                        }
                        else if(TextUtils.equals(appName,LauncherCmdHandler.STRING_APP_CAMERA)){
                            res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_NAME_OPEN_CAMERA);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE,"COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS,"NONE");
                            return res.toString();
                        }
                        else if(TextUtils.equals(appName,LauncherCmdHandler.STRING_APP_XIXIN)){
                            res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_OpenXinxinHealth);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE,"COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS,"NONE");
                            return res.toString();
                        }
                    }
                    else if(TextUtils.equals(action,LauncherCmdHandler.STRING_CLOSE)){
                        //String appName = in.getString(LauncherCmdHandler.STRING_APP);
                        //if(TextUtils.equals(appName,LauncherCmdHandler.STRING_APP_HONGEN)){
                            res.put(LauncherCmdHandler.STRING_INTENTNAME,LauncherCmdHandler.INTENT_NAME_HOME_BACK);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE,"COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS,"NONE");
                            return res.toString();
                        //}
                    }
                    else if(TextUtils.equals(action,LauncherCmdHandler.STRING_TAKE_PHOTO)){
                            res.put(LauncherCmdHandler.STRING_INTENTNAME,LauncherCmdHandler.INTENT_NAME_TAKE_PHOTO);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE,"COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS,"NONE");
                            return res.toString();
                    }
                    else if(TextUtils.equals(action,LauncherCmdHandler.STRING_SCREEN_OFF)){
                        res.put(LauncherCmdHandler.STRING_INTENTNAME,LauncherCmdHandler.INTENT_NAME_OFF_SCREEN);
                        res.put(LauncherCmdHandler.STRING_DIALOGSTATE,"COMPLETED");
                        res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS,"NONE");
                        return res.toString();
                    }
                    else if(TextUtils.equals(action,LauncherCmdHandler.STRING_RECORD)){
                        String itemName = in.getString(LauncherCmdHandler.STRING_ITEM);
                        if(TextUtils.equals(itemName,LauncherCmdHandler.STRING_ITEM_BLOODPRESSURE)) {
                            res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_RecordBloodPressure);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE, "COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS, "NONE");
                            JSONArray JsonArray = new JSONArray();
                            JSONObject json = new JSONObject().put("time","");
                            JsonArray.put(json);
                            json = new JSONObject().put("diastolicPressure","");
                            JsonArray.put(json);
                            json = new JSONObject().put("systolicPressure","");
                            JsonArray.put(json);
                            json = new JSONObject().put("heartRate","");
                            JsonArray.put(json);
                            json = new JSONObject().put("pulseRate","");
                            JsonArray.put(json);
                            res.put(LauncherCmdHandler.STRING_SLOTS, JsonArray);
                            return res.toString();
                        }
                        else if(TextUtils.equals(itemName,LauncherCmdHandler.STRING_ITEM_GLUCOSE)) {
                            res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_RecordBloodGlucose);
                            res.put(LauncherCmdHandler.STRING_DIALOGSTATE, "COMPLETED");
                            res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS, "NONE");
                            JSONArray JsonArray = new JSONArray();
                            JSONObject json = new JSONObject().put("time","");
                            JsonArray.put(json);
                            json = new JSONObject().put("timePoint","");
                            JsonArray.put(json);
                            json = new JSONObject().put("bloodGlucose","");
                            JsonArray.put(json);
                            res.put(LauncherCmdHandler.STRING_SLOTS, JsonArray);
                            return res.toString();
                        }
                    }
                    else if(TextUtils.equals(action,LauncherCmdHandler.STRING_CHECK)){
                        String timeName = in.getString(LauncherCmdHandler.STRING_TIME);
                        res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_CheckBloodPressureReport);
                        res.put(LauncherCmdHandler.STRING_DIALOGSTATE, "COMPLETED");
                        if(TextUtils.equals(timeName,"上个月")){

                            JSONArray JsonArray = new JSONArray();
                            JSONObject json = new JSONObject().put("time",timeName);
                            JsonArray.put(json);
                            res.put(LauncherCmdHandler.STRING_SLOTS, JsonArray);
                            return res.toString();
                        }

                    }else if(TextUtils.equals(action,LauncherCmdHandler.STRING_UP)){
                        res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_NAME_VOL_UP);
                        res.put(LauncherCmdHandler.STRING_DIALOGSTATE, "COMPLETED");
                        res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS, "NONE");
                        return res.toString();
                    }
                    else if(TextUtils.equals(action,LauncherCmdHandler.STRING_DOWN)){
                        res.put(LauncherCmdHandler.STRING_INTENTNAME, LauncherCmdHandler.INTENT_NAME_VOL_DOWN);
                        res.put(LauncherCmdHandler.STRING_DIALOGSTATE, "COMPLETED");
                        res.put(LauncherCmdHandler.STRING_CONFIRMSTATUS, "NONE");
                        return res.toString();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

            }
            return null;
        }
        @Override
        public void onResults(AIResult results) {
            Log.i(TAG, results.getResultObject().toString());
            try {

                String res =new JSONObject(results.getResultObject().toString()).toString(4);
                QAILog.i(TAG, "Local engine recognize result:"+ res);
                //JSONResultParser parser = new JSONResultParser(results.getResultObject().toString());
                JSONResultParser parser = new JSONResultParser(res);
                double conf = parser.getConf();
                if(conf > 0.63) {
                    String restxt = parser.getRec();
                    JSONObject postobj = parser.getSem();
                    QAILog.i(TAG, "Local engine recognize result:" + restxt);
                    if (mListener != null){
                        //pre parse local asr result. if match execute result.
                        String resStr = preparseRes(postobj);
                        if(resStr != null)
                        {
                            mListener.onResults(restxt, "");
                            //pre-parse success stop AI engine.
                            stop();
                            mListener.onCmd(resStr);



                        }
                        else if((mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_ASR)){
                            mListener.onResults(restxt, "");
                        }
                    }
                }else { //end ASR result. if ASR engine only have local ASR engine return result.
                    if(mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_ASR){
                        if(mListener != null){
                            mListener.onResults("","");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // if only have local asr engine, finish recognizing state.
                if(mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_ASR){
                    if(mListener != null){
                        mListener.onResults("","");
                    }
                }
            }
            //setAsrBtnState(true, "识别");
        }

        @Override
        public void onInit(int status) {
            if (status == 0) {
                QAILog.i(TAG, "success end of init asr engine");
                mAILocalASREngineInitialized = true;

            } else {
                QAILog.e(TAG, "failed init asr engine。 status:"+status);
                mAILocalASREngineInitialized = false;
            }
        }

        @Override
        public void onRecorderReleased() {
            // showInfo("检测到录音机停止");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNotOneShot() {
            // TODO Auto-generated method stub

        }
    }

    private boolean initSpeechCloudASR(){
        mAICloudASREngine = AICloudASREngine.createInstance();
//        mEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下

        mAICloudASREngine.setVadResource(SampleConstants.RES_VAD);
        mAICloudASREngine.setDeviceId(Util.getIMEI(mContext));
        mAICloudASREngine.setHttpTransferTimeout(10);
        mAICloudASREngine.setRes(SampleConstants.RES_AIHOME);
        mAICloudASREngine.init(mContext, new AICloudASRListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mAICloudASREngine.setNoSpeechTimeOut(0);
        mAICloudASREngine.setUseCustomFeed(true);
        return true;
    }
    private class AICloudASRListenerImpl implements AIASRListener {

        @Override
        public void onReadyForSpeech() {
            QAILog.v(TAG, "ASR ready for speech.");
            if(mListener!=null) {
                mListener.onReady();
            }
        }

        @Override
        public void onBeginningOfSpeech() {
            QAILog.v(TAG, "ASR on beginning of speech.");
        }

        @Override
        public void onEndOfSpeech() {
            QAILog.v(TAG, "Detect end of speech, start recognizing.");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            QAILog.v(TAG, "Detect end of speech, start recognizing.");
        }

        @Override
        public void onError(AIError error) {
            QAILog.v(TAG, "error:" + error.toString());

        }

        @Override
        public void onResults(AIResult results) {
            if (results.isLast()) {
                if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                    QAILog.v(TAG,"result JSON = " + results.getResultObject().toString());
                    // 可以使用JSONResultParser来解析识别结果
                    // 结果按概率由大到小排序
                    JSONResultParser parser = new JSONResultParser(results.getResultObject()
                            .toString());
                    //resultText.append("识别结果为 :  " + parser.getRec());
                }
            }
        }

        @Override
        public void onInit(int status) {
            QAILog.v(TAG, "Init result " + status);
            if (status == AIConstant.OPT_SUCCESS) {
                mAICloudASREngineInitialized = true;
            } else {
                mAICloudASREngineInitialized = false;
            }
        }

        @Override
        public void onRecorderReleased() {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNotOneShot() {
            // TODO Auto-generated method stub

        }

    }

    public boolean feedData(byte buff[]){
        boolean res = false;
        if(mAsrLocalEngine != null && ((mCurrEngine & QAIConstants.ENGINE_SPEECH_LOCAL_ASR) == QAIConstants.ENGINE_SPEECH_LOCAL_ASR)   ){
            if(mAILocalASREngineInitialized == true){
                mAsrLocalEngine.feedData(buff);
                res = true;
            }
        }
        if(mTXCloudASREngine != null && ((mCurrEngine & QAIConstants.ENGINE_TX_CLOUD_ASR) == QAIConstants.ENGINE_TX_CLOUD_ASR) ){
            //byte[] pcmBuffer = mTXCloudASREngine.getData();
            byte[] pcmBuffer = QAISpeechStatesMachine.getInstance().feedAudioToDispatcher(buff);
            mTXCloudASREngine.feedData(pcmBuffer);
            res = true;
        }
        return res;
    }

    public void setListener(ASRListener listener){
        mListener = listener;
        if((mCurrEngine & QAIConstants.ENGINE_TX_CLOUD_ASR) == QAIConstants.ENGINE_TX_CLOUD_ASR)
        {
            mTXCloudASREngine.setASRListener(mListener);
        }
    }

    public  void start(){
        if((mCurrEngine & QAIConstants.ENGINE_TX_CLOUD_ASR) == QAIConstants.ENGINE_TX_CLOUD_ASR)
        {
            if(SystemTool.checkNet(mContext)==true)//need more consideration in disconnection status avoid send this message.
            {
                QAILog.v(TAG, "TX cloud ASR engine started!");
                mTXCloudASREngine.onLocalWakeup();
            }
        }
        if(mAsrLocalEngine != null && ((mCurrEngine & QAIConstants.ENGINE_SPEECH_LOCAL_ASR) == QAIConstants.ENGINE_SPEECH_LOCAL_ASR)   ){
            if(mAILocalASREngineInitialized == true){
                QAILog.v(TAG, "Local ASR engine started!");
                mAsrLocalEngine.start();
            }
        }
    }

    public  void stopRecording(){
        if(mAsrLocalEngine != null && ((mCurrEngine & QAIConstants.ENGINE_SPEECH_LOCAL_ASR) == QAIConstants.ENGINE_SPEECH_LOCAL_ASR)   ){
            if(mAILocalASREngineInitialized == true){
                mAsrLocalEngine.stopRecording();

            }
        }
    }
    public  void stop(){
        if(mAsrLocalEngine != null && ((mCurrEngine & QAIConstants.ENGINE_SPEECH_LOCAL_ASR) == QAIConstants.ENGINE_SPEECH_LOCAL_ASR)   ){
            if(mAILocalASREngineInitialized == true){
                //stop asr engine
                QAILog.v(TAG, "Local ASR engine stopped!");
                mAsrLocalEngine.cancel();

            }
        }
        if (mTXCloudASREngine != null && ((mCurrEngine & QAIConstants.ENGINE_TX_CLOUD_ASR) == QAIConstants.ENGINE_TX_CLOUD_ASR)) {
            QAILog.v(TAG, "TX cloud ASR engine stopped!");
            mTXCloudASREngine.stopRequest();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAICloudASREngine != null) {
            mAICloudASREngine.destroy();
            mAICloudASREngine = null;
        }
    }
}
