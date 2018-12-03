package kinstalk.com.qloveaicore.engines;

import android.content.Context;
import android.os.Environment;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AICloudTTSEngine;
import com.aispeech.export.engines.AILocalTTSEngine;
import com.aispeech.export.listeners.AITTSListener;
import com.aispeech.speech.AppKey;
import com.aispeech.util.SampleConstants;

import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;

/**
 * Created by shaoyi on 2018/4/8.
 */

public class TTSEngine extends Engine {
    private static TTSEngine sInst = null;
    protected static final String TAG = "AI-TTSEngine";
    private int mCurrEngine = QAIConstants.ENGINE_SPEECH_LOCAL_TTS; //ENGINE_TX_CLOUD_TTS ENGINE_SPEECH_CLOUD_TTS ENGINE_SPEECH_LOCAL_TTS
    //private int mCurrEngine = AIConstants.ENGINE_SPEECH_CLOUD_TTS;
    AILocalTTSEngine mAILocalTTSEngine = null;
    AICloudTTSEngine mAICloudTTSEngine = null;
    Context mContext = null;
    private boolean mAILocalTTSEngineInitialized = false;
    private boolean mAICloudTTSEngineInitialized = false;

    private TTSListener mListener = null;

    public static TTSEngine getInstance(Context c) {

        if (sInst == null) {
            QAILog.v(TAG, "Create TTS Engine.");
            sInst = new TTSEngine(c);
        }
        return sInst;
    }

    private TTSEngine(Context c) {
        //super(c);
        mContext = c;
        init();
    }
    public void setListener(TTSListener listener){
        mListener = listener;
    }
    public boolean play(String txt){
        if(!txt.isEmpty()){
            if(mAICloudTTSEngine != null && mCurrEngine == QAIConstants.ENGINE_SPEECH_CLOUD_TTS ){
                mAICloudTTSEngine.setLanguage(AIConstant.CN_TTS);
                mAICloudTTSEngine.speak(txt, "1024");
                mCurrEngine = QAIConstants.ENGINE_SPEECH_CLOUD_TTS;
                return true;
            }
            else if (mAILocalTTSEngine != null){ //last selection
                mAILocalTTSEngine.setSavePath(Environment.getExternalStorageDirectory() + "/tts/"
                        + System.currentTimeMillis() + ".wav");
                mAILocalTTSEngine.speak(txt, "1024");
                mCurrEngine = QAIConstants.ENGINE_SPEECH_LOCAL_TTS;
                return true;
            }
        }
        return false;
    }

    public void pause(){
        if(mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_TTS) {
            mAILocalTTSEngine.pause();
        }
        else if(mCurrEngine == QAIConstants.ENGINE_SPEECH_CLOUD_TTS){
            mAICloudTTSEngine.pause();
        }
    }

    public void stop(){
        if(mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_TTS) {
            mAILocalTTSEngine.stop();
        }
        else if(mCurrEngine == QAIConstants.ENGINE_SPEECH_CLOUD_TTS){
            mAICloudTTSEngine.stop();
        }
    }

    public void resume(){
        if(mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_TTS) {
            mAILocalTTSEngine.resume();
        }
        else if(mCurrEngine == QAIConstants.ENGINE_SPEECH_CLOUD_TTS){
            mAICloudTTSEngine.resume();
        }
    }

    @Override
    public boolean init(){
        QAILog.v(TAG, "Init TTS Engine.");
        if(ininSpeechLocalTTS()){
            QAILog.v(TAG, "Init Speech Local TTS Engine success.");
        }
        return true;
    }

    private boolean ininSpeechLocalTTS(){
        //init Local tts engine.
        if (mAILocalTTSEngine != null) {
            mAILocalTTSEngine.destroy();
        }
        mAILocalTTSEngine = AILocalTTSEngine.createInstance();
        mAILocalTTSEngine.setResource(SampleConstants.RES_TTS);
        mAILocalTTSEngine.setDictDbName(SampleConstants.RES_DICT);
        mAILocalTTSEngine.init(mContext, new AILocalTTSListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        mAILocalTTSEngine.setSpeechRate(0.85f);
        mAILocalTTSEngine.setDeviceId(Util.getIMEI(mContext));
        //init AI Cloud TTS.

        mAICloudTTSEngine = AICloudTTSEngine.createInstance();
        mAICloudTTSEngine.setRealBack(true);
        mAICloudTTSEngine.init(mContext, new AICloudTTSListenerImpl(), AppKey.APPKEY, AppKey.SECRETKEY);
        // 指定默认中文合成
        mAICloudTTSEngine.setLanguage(AIConstant.CN_TTS);

        // 默认女声
        mAICloudTTSEngine.setRes("syn_chnsnt_zhilingf");
        mAICloudTTSEngine.setDeviceId(Util.getIMEI(mContext));



        return true;
    }




    private class AICloudTTSListenerImpl implements AITTSListener {

        @Override
        public void onInit(int status) {
            QAILog.i(TAG, "AICloudTTSListenerImpl init completed return：" + status);
            QAILog.i(TAG, "onInit");
            if (status == AIConstant.OPT_SUCCESS) {
                mAILocalTTSEngineInitialized  = true;
            } else {
                mAILocalTTSEngineInitialized  = false;
            }
            if(mListener!=null) {
                mListener.onInit(status);
            }
        }


        @Override
        public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {
            if(mListener!=null) {
                mListener.onProgress(currentTime,totalTime,isRefTextTTSFinished);
            }
        }

        @Override
        public void onError(String utteranceId, AIError error) {

            if(mListener!=null) {
                mListener.onError(utteranceId,0);
            }
        }

        @Override
        public void onReady(String utteranceId) {
            // TODO Auto-generated method stub
            QAILog.i(TAG,"onReady");
            if(mListener!=null) {
                mListener.onReady(utteranceId);
            }
        }

        @Override
        public void onCompletion(String utteranceId) {
            QAILog.i(TAG,"onCompletion");
            if(mListener!=null) {
                mListener.onCompletion(utteranceId);
            }
        }

    }
    private class AILocalTTSListenerImpl implements AITTSListener {

        @Override
        public void onInit(int status) {
            QAILog.i(TAG, "AICloudTTSListenerImpl init completed return：" + status);
            QAILog.i(TAG, "onInit");
            if (status == AIConstant.OPT_SUCCESS) {
                mAILocalTTSEngineInitialized  = true;
            } else {
                mAILocalTTSEngineInitialized  = false;
            }
            if(mListener!=null) {
                mListener.onInit(status);
            }
        }

        @Override
        public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {
            //QAILog.i(TAG, "time:" + currentTime + "ms, sum:" + totalTime + "ms, ref:" + isRefTextTTSFinished);
            if(mListener!=null) {
                mListener.onProgress(currentTime,totalTime,isRefTextTTSFinished);
            }
        }

        @Override
        public void onError(String utteranceId, AIError error) {
            QAILog.i(TAG, "\nError: " + error.toString());
            if(mListener!=null) {
                mListener.onError(utteranceId,0);
            }
        }

        @Override
        public void onReady(String utteranceId) {
            QAILog.i(TAG,"onReady");
            if(mListener!=null) {
                mListener.onReady(utteranceId);
            }
        }

        @Override
        public void onCompletion(String utteranceId) {
            QAILog.i(TAG,"onCompletion");
            if(mListener!=null) {
                mListener.onCompletion(utteranceId);
            }
        }
    }

}
