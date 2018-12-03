package kinstalk.com.qloveaicore.engines;

import android.content.Context;
import android.content.SharedPreferences;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.export.engines.AILocalWakeupDnnEngine;
import com.aispeech.export.listeners.AILocalWakeupDnnListener;
import com.aispeech.util.SampleConstants;

import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.SystemTool;
import kinstalk.com.qloveaicore.QAIConfigController;
import kinstalk.com.qloveaicore.BuildConfig;
import kinstalk.com.qloveaicore.statemachine.QAISpeechStatesMachine;
import kinstalk.com.qloveaicore.wakeup.tencent.RecordDataManager;

import static java.lang.Thread.sleep;

/**
 * Created by shaoyi on 2018/4/8.
 */

public class WakeupEngine extends Engine {
    private static WakeupEngine sInst = null;
    protected static final String TAG = "AI-WakeupEngine";
    private String[] mSpeechWakeupWord = {QAIConstants.AI_DFT_SPEECH_WAKEUP_WORD};
    private float mSpeechSearchThreshold = QAIConstants.AI_DFT_SPEECH_WAKEUP_THRESHOLD;
    private int mCurrEngine = QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP; //ENGINE_TX_CLOUD_TTS ENGINE_SPEECH_CLOUD_TTS ENGINE_SPEECH_LOCAL_TTS
    //private int mCurrEngine = AIConstants.ENGINE_SPEECH_CLOUD_TTS;
    AILocalWakeupDnnEngine mLocalEngine = null;
    Context mContext = null;
    //-------------Local wakeup engine -----------------------
    private boolean mAILocalWakeupDnnEngineInitialized = false;
    private boolean mAILocalWakeupDnnEngineStarted = false;
    private boolean mAILocalAuthed = false;
    //--------------Cloud Wakeup Engine-----------------------

    RecordDataManager mXWEngine =null;
    //--------------------------------------------------------

    private WakeupListener mListener = null;
    private boolean mDebug = !(SystemTool.isUserType());

    public static WakeupEngine getInstance(Context c) {

        if (sInst == null) {
            QAILog.v(TAG, "Create Wakeup Engine.");
            sInst = new WakeupEngine(c);
        }
        return sInst;
    }

    private WakeupEngine(Context c) {
        //super(c);
        mContext = c;
        init();
    }
    public void setListener(WakeupListener listener){
            //local wakeup engine
        mListener = listener;
        if(mCurrEngine == QAIConstants.ENGINE_TX_CLOUD_WAKEUP )
        {
            mXWEngine.setWakeupListener(mListener);
        }

    }

    public void initEngine(boolean Authed,int engine){
        if(engine == QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP) {
            mAILocalAuthed = Authed;
            mCurrEngine = QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP;
            if(mAILocalAuthed == true ) {
                if(initSpeechLocalWakeup()){
                    QAILog.v(TAG, "Init Speech Local Wakeup Engine ....");
                }
            }else{
                mLocalEngine =null;
            }

        }
        else if(engine == QAIConstants.ENGINE_TX_CLOUD_WAKEUP) {
            mXWEngine=RecordDataManager.getInstance();
            mCurrEngine = QAIConstants.ENGINE_TX_CLOUD_WAKEUP;
            SharedPreferences sp = mContext.getSharedPreferences("wakeup", Context.MODE_PRIVATE);
            mXWEngine.setWakeupEnable(sp.getBoolean("use", true));

            mXWEngine.start(mContext);

            new Thread() {
                public void run() {
                    if(mListener!=null) {
                        this.setName("XWinit_thread");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mListener.onInit(AIConstant.OPT_SUCCESS);
                    }
                }
            }.start();

        }
    }


    public void start(){
        QAILog.v(TAG, "Start Wakeup Engine: "+ QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP+" mAILocalWakeupDnnEngineInitialized: "+mAILocalWakeupDnnEngineInitialized);
        if(mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP) {
            if(mAILocalWakeupDnnEngineInitialized == true) {
                if(mAILocalWakeupDnnEngineStarted == false) {
                    if(mLocalEngine != null) {
                        mLocalEngine.start();
                    }
                }
                mAILocalWakeupDnnEngineStarted = true;
            }else{
                QAILog.v(TAG, "Start Wakeup Engine failed: mAILocalWakeupDnnEngineInitialized: "+mAILocalWakeupDnnEngineInitialized +" mAILocalWakeupDnnEngineStarted:"+mAILocalWakeupDnnEngineStarted);
            }
        }
    }

    public void stop(){
        QAILog.v(TAG, "Stop Wakeup Engine: "+ QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP);
        if(mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP) {
            if(mLocalEngine != null) {
                mLocalEngine.stop();
            }
            mAILocalWakeupDnnEngineStarted = false;
        }

    }

    public boolean feedData(byte buff[]){
        if(mLocalEngine != null && mCurrEngine == QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP ){
            if(mAILocalWakeupDnnEngineStarted == true){
                // temp
                QAISpeechStatesMachine.getInstance().feedAudioToDispatcher(buff);
                mLocalEngine.feedData(buff);
            }

        }
        else if(mXWEngine != null && mCurrEngine == QAIConstants.ENGINE_TX_CLOUD_WAKEUP ) {
            mXWEngine.feedData(buff);
        }
            return false;
    }


    @Override
    public boolean init(){
        QAILog.v(TAG, "Init Wakeup Engine.");
        return true;
    }

    private boolean initSpeechLocalWakeup(){
        //init Local tts engine.
        if (mLocalEngine != null) {
            mLocalEngine.destroy();
        }
        mSpeechSearchThreshold = QAIConfigController.readSpeechWakeupThreshold(mContext);
        mLocalEngine = AILocalWakeupDnnEngine.createInstance();
        mLocalEngine.setResBin(SampleConstants.RES_WAKEUP); //非自定义唤醒资源可以不用设置words和thresh，资源已经自带唤醒词
//        mLocalEngine.setEchoWavePath("/sdcard/speech"); //保存aec音频到/sdcard/speech/目录,请确保该目录存在
        mLocalEngine.setWords(mSpeechWakeupWord);
        mLocalEngine.setThreshold(new float[]{mSpeechSearchThreshold});
        mLocalEngine.init(mContext, new AISpeechListenerImpl(), BuildConfig.APPKEY, BuildConfig.SECRETKEY);
        mLocalEngine.setStopOnWakeupSuccess(true);//设置当检测到唤醒词后自动停止唤醒引擎
        mLocalEngine.setUseCustomFeed(true);
        if(mDebug) {
            mLocalEngine.setUploadEnable(QAIConfigController.readSnsryWakeupDumpAudio(mContext) ? true : false);
            mLocalEngine.setTmpDir("/sdcard/temp");
        }

        return true;
    }




    private class AISpeechListenerImpl implements AILocalWakeupDnnListener {
        private  int retry_time = 0;

        @Override
        public void onError(AIError error) {
            QAILog.e(TAG, "LocalWakeup： error: ", error.toString());

            mAILocalWakeupDnnEngineStarted = false;
            if(retry_time < 3){
                if(mAILocalWakeupDnnEngineInitialized == true) {
                    if(mAILocalWakeupDnnEngineStarted == false) {
                        if (mLocalEngine != null) {
                            mLocalEngine.start();
                            mAILocalWakeupDnnEngineStarted = true;
                        }
                    }

                }
                else{
                    try {
                        //waiting for init.
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //mWakeupEngineStarted = true;
                /*if(mRecordController1 != null) {
                    mRecordController1.feedDataToWakeup(true).feedDataToRecognize(false);
                }*/
                retry_time++;
            }
            else {
                if(mListener!=null) {
                    mListener.onError(error.toString());
                }
                QAILog.e(TAG, "LocalWakeup： error more than 3 times. ", error.toString());
            }
        }

        @Override
        public void onInit(int status) {
            QAILog.v(TAG, " Init result: " + status);
            if(mListener!=null) {
                mListener.onInit(status);
            }
            if (status == AIConstant.OPT_SUCCESS) {
                mAILocalWakeupDnnEngineInitialized = true;
                QAILog.w(TAG, " Start wakeup engine after init! mAILocalWakeupDnnEngineStarted： " +mAILocalWakeupDnnEngineStarted);
                if(mAILocalWakeupDnnEngineStarted == false) {
                    mLocalEngine.start();
                    mAILocalWakeupDnnEngineStarted = true;
                }
            } else {
                QAILog.e(TAG, " Init result: " + status);
                mAILocalWakeupDnnEngineInitialized = false;
                mAILocalWakeupDnnEngineStarted = false;
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onWakeup(String recordId, double confidence, String wakeupWord) {
            QAILog.v(TAG, "LocalWakeup：  wakeupWord = " + wakeupWord + "  confidence = " + confidence);
            if(mListener!=null) {
                mListener.onWakeup(wakeupWord,(int) (confidence* 100));
            }
        }

        @Override
        public void onReadyForSpeech() {
            if(mListener!=null) {
                mListener.onReady();
            }
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onRecorderReleased() {
        }

        @Override
        public void onWakeupEngineStopped() {
            QAILog.d(TAG, "Local wakeup restart.");
            //if(mAILocalWakeupDnnEngineStarted == true) {
            if(mListener!=null) {

            }
            //}
            mLocalEngine.start();
            mAILocalWakeupDnnEngineStarted = true;
        }

    }

}
