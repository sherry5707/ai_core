package kinstalk.com.qloveaicore.statemachine;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.aispeech.export.listeners.AIAuthListener;
import com.aispeech.speech.AIAuthEngine;
import com.kinstalk.her.voip.manager.AVChatManager;
import com.tencent.xiaowei.control.XWeiAudioFocusManager;
import com.tencent.xiaowei.info.XWContextInfo;
import com.tencent.xiaowei.sdk.XWSDK;
import com.tencent.xiaowei.util.QLog;

import java.io.File;
import java.io.FileNotFoundException;

import kinstalk.com.common.utils.AssetsUtil;
import kinstalk.com.common.utils.Common2;
import kinstalk.com.common.utils.CountlyEvents;
import kinstalk.com.common.utils.FileUtils;
import kinstalk.com.common.utils.QAICommandUtils;
import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.SystemTool;
import kinstalk.com.qloveaicore.AICoreDef;
import kinstalk.com.qloveaicore.BuildConfig;
import kinstalk.com.qloveaicore.ICoreController;
import kinstalk.com.qloveaicore.QAIConfigController;
import kinstalk.com.qloveaicore.R;
import kinstalk.com.qloveaicore.engines.ASREngine;
import kinstalk.com.qloveaicore.engines.ASRListener;
import kinstalk.com.qloveaicore.engines.TTSEngine;
import kinstalk.com.qloveaicore.engines.WakeupEngine;
import kinstalk.com.qloveaicore.engines.WakeupListener;
import kinstalk.com.qloveaicore.wakeup.tencent.RecordDataManager;

import static java.lang.Thread.sleep;

//import com.tencent.aiaudio.CommonApplication;
//import com.tencent.aiaudio.demo.IAIAudioService;
//import com.tencent.aiaudio.utils.AssetsUtil;

/**
 * Created by majorxia on 2018/3/30.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public class SpeechController {
    private static final String TAG = "AI-SpeechController";



    public static boolean localVad;
    private Handler mHandler = new Handler();
    //szjy begin
    private Context mContext;
    private ICoreController mCoreController;

    private static SpeechController sInstance = null;
    private boolean mDebug = !(SystemTool.isUserType());
    private RecordController mRecordController;
    private QAISpeechStatesMachine mSpeechSM = null;


    private AIAuthEngine mAuthEngine = null;
    private TTSEngine mTTSEngine = null;
    public ASREngine mASREngine = null;
    private WakeupEngine mWakeupEngine = null;
    public boolean mAuthRes = false;
    //temp code.
    private boolean use_speech = false;
    private boolean mWakeupEnabled = true;
    //szjy end
    /**
     * 查询语音场景信息
     */

    public static final String QUERY = "com.ktcp.voice.QUERY";
    /**
     * 查询语音场景信息
     */
    public static final String COMMIT = "com.ktcp.voice.COMMIT";
    /**
     * 执行语音命令
     */
    public static final String EXECUTE = "com.ktcp.voice.EXECUTE";
    /**
     * 语音搜索
     */
    public static final String SEARCH = "com.ktcp.voice.SEARCH";
    /**
     * 语音执行结果
     */
    public static final String EXERESULT = "com.ktcp.voice.EXERESULT";

    //    static AIAudioService service;
    private XWeiAudioFocusManager.OnAudioFocusChangeListener listener;

//    public static AIAudioService getInstance() {
//        return service;
//    }



    private AudioManager mAudioManager = null;

    private File AudioFileRecongnition = new File("/sdcard/temp/audio-wakeup.pcm");
    private AudioRecord mAudioRecord  = null;
    private int sampleRateInHz = 16000;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)*2;
    protected int bufferSizeInBytesRead = bufferSizeInBytes / 4;
    private static final int  AUDIO_BUFF_SIZE = 120;
    //It is a 120 size static PCM circle buff. Pay attention to low layer buff process.
    byte mBuffer[][] = new byte[AUDIO_BUFF_SIZE][bufferSizeInBytesRead];
    int mBuffPt = 0;

    public static synchronized SpeechController getInstance() {
        if (sInstance == null) {
            sInstance = new SpeechController();
        }
        return sInstance;
    }

    public void init(Context mContext, ICoreController coreController, QAISpeechStatesMachine sm) {
        this.mContext = mContext;
        mCoreController = coreController;
        QAILog.d(TAG, "SpeechController: Enter");
        // create mSpeechSM and set initial state to mInitState.
        mSpeechSM =  sm;

    }

    /*public SpeechController() {
        this.mContext = mContext;
        mCoreController = coreController;
        QAILog.d(TAG, "SpeechController: Enter");
    }*/

    public void onCreate(WakeupListener wakeupListener, ASRListener asrListener) {
//        service = this;
        QAILog.d(TAG, "onCreate: Enter");


        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        use_speech = QAIConfigController.readSpeechEnable(mContext);
        mWakeupEngine = WakeupEngine.getInstance(mContext);

        mASREngine = ASREngine.getInstance(mContext);
        mASREngine.setListener(asrListener);
        //mASREngine.setListener(new ASREngineListenerImpl());
        // use_speech = false;


        //next code need check how to set core controller.
        RecordDataManager.getInstance().setCoreController(mCoreController);

        if(use_speech == true){
            //init local wakeup engine, after authenticated init local wakeup engine and start.
            mWakeupEngine.setListener(wakeupListener);
            InitAuthEngine();
            RecordDataManager.getInstance().start(mContext);

        }
        else {
            //
            mWakeupEngine.initEngine(true, QAIConstants.ENGINE_TX_CLOUD_WAKEUP);
            mWakeupEngine.setListener(wakeupListener);

        }

        //RecordDataManager.getInstance().setASRListener(new ASREngineListenerImpl());
        //QAISpeechStatesMachine sm = mSpeechSM.get();

        AssetsUtil.init(mContext);

        //init2();// TODO TODO TODO TODO XXS
        XWSDK.getInstance().setNetworkDelayListener(new XWSDK.NetworkDelayListener() {
            @Override
            public void onDelay(String voiceId, long time) {
               /* if (WakeupAnimatorService.getInstance() != null)
                    WakeupAnimatorService.getInstance().setNetText(time + "ms");*/
            }
        });


        // startRecord will let SM goto Idle mode.
        startRecord();

    }

    private void InitAuthEngine(){
        mAuthEngine = AIAuthEngine.getInstance(mContext);
        try {
            QAILog.v(TAG, "build env is :"+BuildConfig.APPKEY.substring(BuildConfig.APPKEY.length()-4)+BuildConfig.SECRETKEY.substring(BuildConfig.SECRETKEY.length()-4));
            mAuthEngine.init(BuildConfig.APPKEY, BuildConfig.SECRETKEY, "");
            //mAuthEngine.init(BuildConfig.APPKEY, BuildConfig.SECRETKEY, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mAuthEngine.setOnAuthListener(new AIAuthListener() {

            @Override
            public void onAuthSuccess() {
                QAILog.v(TAG, "Speech Engine: onAuthSuccess");

                mAuthRes = true;
                if (mWakeupEngine != null) {
                    mWakeupEngine.initEngine(mAuthRes, QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP);
                }
                if(mASREngine != null){
                    mASREngine.initLocal();
                }

            }

            @Override
            public void onAuthFailed(final String result) {
                QAILog.e(TAG, "Speech Engine: onAuthFailed");
                //Goto into error state.
                mAuthRes = false;
                if (mWakeupEngine != null) {
                    mWakeupEngine.initEngine(mAuthRes, QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP);
                }
            }
        });
        if (mAuthEngine.isAuthed()) {
            QAILog.v(TAG, "Speech Engine: is authenticated");
            mAuthRes = true;
            mWakeupEngine.initEngine(mAuthRes, QAIConstants.ENGINE_SPEECH_LOCAL_WAKEUP);
            if(mASREngine != null){
                mASREngine.initLocal();
            }
        } else {
            QAILog.v(TAG, "Speech Engine: is not Authenticated, try do auth.");
            mAuthRes = false;

            new Thread(new Runnable() {
                int retryTime = 0;

                @Override
                public void run() {
                    while (!mAuthRes) {
                        try {
                            retryTime++;
                            if (retryTime < 10) {
                                //sleep 1 s.
                                sleep(1500);
                                if (retryTime == 3)
                                    showToastMessage(mContext.getString(R.string.api_error_authticated_failed), true);
                            } else {
                                sleep(1000 * 60);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String threadName = "Auth_T" + Thread.currentThread().getId();
                        Thread.currentThread().setName(threadName);
                        QAILog.v(TAG, "Thread id is:" + threadName);
                        mAuthRes = mAuthEngine.doAuth();
                    }
                    retryTime = 0;
                    QAILog.v(TAG, "Stop auth thread!");
                }

            }).start();
        }
    }

    private void InitEngines(){
        //







        //create TTS engine
        //mTTSEngine = TTSEngine.getInstance(mContext);
        //mTTSEngine.setListener(new TTSEngineListenerImpl());

        //create ASR engine

        //mASREngine = ASREngine.getInstance(mContext);
        //mASREngine.setListener(new ASREngineListenerImpl());
    }

    public  void showToastMessage(final String text, boolean show) {
        if (show) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

 public  void setWakeupEnabled(boolean enabled){
        mWakeupEnabled = enabled;
    }

    // 只给查设备信息的长文本使用，让TTS能正确念出来这个格式
    private String getFormatTTSText(String text) {
        StringBuffer sb = new StringBuffer();
        try {
            Long.valueOf(text);
            for (int i = 0; i < text.length(); i++) {
                sb.append(text.charAt(i));
                sb.append(" ");
            }
        } catch (Exception e) {
            for (int i = 0; i < text.length(); i++) {
                if (':' == text.charAt(i)) {
                    sb.append("冒号");
                } else {
                    sb.append(text.charAt(i));
                }
                sb.append(",");
            }
        }

        return sb.toString();
    }

    private void wakeup() {
        Log.e(TAG, "context.voiceRequestBegin true wakup");
        XWContextInfo contextInfo = new XWContextInfo();
        if (localVad) {
            contextInfo.requestParam |= XWContextInfo.REQUEST_PARAM_USE_LOCAL_VAD;
        }
        RecordDataManager.getInstance().onWakeup(new XWContextInfo());
    }


    public void wakeup(String contextId, int speakTimeout, int silentTimeout, long requestParam) {
        XWContextInfo contextInfo = new XWContextInfo();
        contextInfo.ID = contextId;
        contextInfo.silentTimeout = silentTimeout;
        contextInfo.speakTimeout = speakTimeout;
        contextInfo.requestParam = requestParam;
        // this code should use wake engine engine replace this code.
        RecordDataManager.getInstance().onWakeup(contextInfo);
        if(mSpeechSM != null)
            mSpeechSM.gotoRecognize();
    }

    public void setVolume(int value) {
        int tag = AudioManager.STREAM_MUSIC;
        if (mAudioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
            tag = AudioManager.MODE_IN_COMMUNICATION;
        }
        int max = mAudioManager.getStreamMaxVolume(tag);
        int current = mAudioManager.getStreamVolume(tag);
        int vol;
        float percent;
        if (value <= 1 && value >= -1) {
            percent = value;
        } else {
            percent = value / 100f;
        }

        vol = (int) (percent * max);
        if (vol < 0) {
            vol = 0;
        }
        if (vol > max) {
            vol = max;
        }
        mAudioManager.setStreamVolume(tag, vol, AudioManager.FLAG_SHOW_UI);
        QLog.d(TAG, "setVolume old: " + current + ", new: " + vol);
    }

    public int getVolume() {
        int tag = AudioManager.STREAM_MUSIC;
        if (mAudioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
            tag = AudioManager.MODE_IN_COMMUNICATION;
        }
        int max = mAudioManager.getStreamMaxVolume(tag);
        int current = mAudioManager.getStreamVolume(tag);
        int vol = (int) (current * 100.0f / max);
        QLog.d(TAG, "getVolume current: " + current + ", max: " + max + ", vol: " + vol);
        return vol;
    }

    private void startRecord() {

        //start recording thread
        if (mRecordController == null) {
            mRecordController = new RecordController(/*createAudioRecord1()*/);
            mRecordController.startRecord();
        }
        else if(mRecordController.isKeepRecording()==false){

            //if another application occpy the MIC.
            mRecordController.startRecord();
        }
    }





    //////////////////////////////////////////////////////////////

    private static String getWIFILocalIpAdress(Context context) {
        /*
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        return ip;
        */
        return "";
    }

    private static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF) + "." +
                ((ipAdress >> 8) & 0xFF) + "." +
                ((ipAdress >> 16) & 0xFF) + "." +
                (ipAdress >> 24 & 0xFF);
    }



    // Sco related feature redefined (bug 9485)
    private boolean mIsScoStart = false;
    private volatile boolean mIsScoRegistered = false;
    private Object mScoLock = new Object();

    private void registerScoReceiver() {
        QAILog.d(TAG, "registerScoReceiver");
        synchronized (mScoLock) {
            if (!mIsScoRegistered) {
                mContext.registerReceiver(mScoBroadcastReceiver, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
                mIsScoRegistered = true;
            }
        }
    }

    private void unRegisterScoReceiver() {
        QAILog.d(TAG, "unRegisterScoReceiver");
        synchronized (mScoLock) {
            if (mIsScoRegistered) {
                mContext.unregisterReceiver(mScoBroadcastReceiver);
                mIsScoRegistered = false;
            }
        }
    }

    private BroadcastReceiver mScoBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
            if (state == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) {
                if (mSpeechSM.isCallState()) {
                    mAudioManager.setBluetoothScoOn(true);
                    mAudioManager.startBluetoothSco();
                    mIsScoStart = true;
                }
                unRegisterScoReceiver();
            } else if (state == AudioManager.SCO_AUDIO_STATE_CONNECTING) {
                mAudioManager.setBluetoothScoOn(false);
                mAudioManager.stopBluetoothSco();
                mIsScoStart = false;
            }
        }
    };

    public void connectToSco() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mAudioManager.isBluetoothScoAvailableOffCall()) {
            QLog.d(TAG, "AudioManager startBluetoothSco start time:" + System.currentTimeMillis());
            registerScoReceiver();
        }
    }

    public void disConnectToSco() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mAudioManager.isBluetoothScoAvailableOffCall()) {
            QLog.d(TAG, "AudioManager endBluetoothSco end time:" + System.currentTimeMillis());
            unRegisterScoReceiver();
            if (mIsScoStart) {
                mAudioManager.setBluetoothScoOn(false);
                mAudioManager.stopBluetoothSco();
                mIsScoStart = false;
            }
        }
    }

    public void wakeUpWordDataSave() {
        int buffPt_tmp = mBuffPt;
        int buffPt_tmp_2;
        QLog.d(TAG, "wakeUpWordDataSave enter ...");

        FileUtils.deleteFile(AudioFileRecongnition);
        for (int i = 0; i < 50; i++) {
            if (buffPt_tmp < 50) {
                buffPt_tmp = 120 + buffPt_tmp;
            }
//数组最多120,我们要回退50个,数组下标小于50时从120+0开始,当数组下标加120以后,再加加i时,可能又会超过数组下标，需要再次回到下标0,
//所以我们再次当下标大于119时减去120,继续从0开始继续抓数据
            buffPt_tmp_2 = buffPt_tmp - 50 + i;
            if (buffPt_tmp_2 > 119) {
                buffPt_tmp_2 = buffPt_tmp - 120;
            }
            FileUtils.saveFile("/sdcard/temp/",mBuffer[buffPt_tmp_2], "audio-wakeup", "pcm", true);
        }
    }

    private class RecordController{
        private static final int START_RECORD_RETRY_TIMES = 3;

        private boolean mKeepRecording = true;
        private boolean mThreadExited = false;

        private Object objLock = new Object();

        private  long failedRestartTimes = 0;
        private int recognizeAudioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;
        private RecordController() {

        }


        private boolean createAndStartAudioRecord1(String tag, int tryCount) {
            QAILog.d(TAG, tag + ": createAndStartAudioRecord1: E, tryCount = " + tryCount);
            boolean isSuccess;
            if (mAudioRecord != null && AudioRecord.STATE_INITIALIZED != mAudioRecord.getState()) {
                mAudioRecord.release();
                mAudioRecord = null;
            }
            mAudioRecord = new AudioRecord(recognizeAudioSource,
                    sampleRateInHz,
                    channelConfig,
                    audioFormat,
                    bufferSizeInBytes);
            // when everything is ready, call startRecord;
            //  mAudioRecord1.startRecording();
            try {
                mAudioRecord.startRecording();

                mKeepRecording = true;
                QAILog.d(TAG, tag + ": createAndStartAudioRecord1: tryCount = " + tryCount + " success");
                new Thread(new RecordTask(tag)).start();

                isSuccess = true;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                QAILog.e(TAG, tag + ": createAndStartAudioRecord1: tryCount = " + tryCount + " exception: " + e.getMessage());
                CountlyEvents.startRecordingRetryException("Tencent_Record_" + tag, e.getMessage(), tryCount);
                isSuccess = false;
            }

            return isSuccess;
        }

        private void tryStartRecord(String tag) {
            int tryCount = 0;
            boolean isSuccess = createAndStartAudioRecord1(tag, tryCount);
            while (!isSuccess ) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    CountlyEvents.startRecordingException("Tencent_Record_" + tag,e.toString());
                    e.printStackTrace();
                }
                tryCount++;
                // retry startRecord
                isSuccess = createAndStartAudioRecord1(tag, tryCount);
            }

        }

        /**
         * 初始化识别及唤醒的AudioRecorder
         */
        private synchronized void startRecord() {
            QAILog.d(TAG, "startRecord inner: ");
            mKeepRecording = true;
            synchronized (objLock) {
                tryStartRecord("startRecord");
            }
        }

        /**
         * 重试初始化识别及唤醒的AudioRecorder
         * FIX bug 11625：初始化录音的时候，底层不一定已经恢复，加上重试策略
         */
        private synchronized void reStartRecord(String reStartTag) {
            QAILog.d(TAG, "reStartRecord inner: reason: " + reStartTag);
            synchronized (objLock) {

                stopRecord();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while(!isThreadExited()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                tryStartRecord(reStartTag);

            }
        }

        public void stopRecord() {
            mKeepRecording = false;
        }

        public boolean isKeepRecording() {
            return mKeepRecording;
        }

        public boolean isThreadExited() {
            return mThreadExited;
        }

        private class RecordTask implements Runnable {

            private long count_failed = 0;
            private long count_record = 0;
            private String threadTag = "";

            public RecordTask(String threadTag) {
                this.threadTag = threadTag;
            }

            private void dbgRecording() {

                count_record ++;
                if(count_record % 150 == 0) {
                    long i = (int) (count_record * 0.02);
                    QAILog.d(TAG, "Recording Sates: " + mSpeechSM.getCurrentStateName() + "  Outer Recognizing: "+mSpeechSM.isOuterRecognizing()+" "+ i + "s >>>>>>>>>>>>>>>>>>>>>>\n");
                }

            }

            private boolean dbgCountFailed() {
                count_failed++;
                boolean isNeedBreak = false;

                if (count_failed % 1000 == 0) {
                    long i = (int) (count_failed * 0.001);
                    QAILog.d(TAG, "Tencent_recognizer record failed! Sates:" + mSpeechSM.getCurrentStateName() + "  "+ i + "s >>>>>>>>>>>>>>>>>>>\n");

                    // FIX bug 11625：偶现底层AudioRecorder挂了，录音失败，需要重新初始化
                    // 从log看的话10ms会调用1000次failed
                    if (count_failed % 10000 == 0) {
                        isNeedBreak = true;
                    }
                }

                return isNeedBreak;
            }

            @Override
            public void run() {
                mThreadExited = false;
                Thread.currentThread().setName("RecordTask");
                QAILog.d(TAG, "run: mAudioRecord1 thread started: " + threadTag);
                QAILog.d(TAG, "run: mAudioRecord1 created and startRecording");
                boolean isFailedTooManyTimes = false;

                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                while (mKeepRecording) {


                    int read_len = mAudioRecord.read(mBuffer[mBuffPt], 0, bufferSizeInBytesRead);

                    if (read_len  < bufferSizeInBytesRead) {
                        isFailedTooManyTimes = dbgCountFailed();
                        if (isFailedTooManyTimes) {
                            break;
                        }
                        continue;
                    }
                    if(mDebug){
                        dbgRecording();
                    }

                    //dump PCM
                    /*
                    if(dumpPCM) {
                        FileUtils.saveFile("/sdcard/temp/",mBuffer[mBuffPt], "audio-tx", "pcm", true);
                    }*/

                    if (mSpeechSM.ismFeedToWakeUp()) {
                        mBuffer[mBuffPt] = mSpeechSM.feedAudioToDispatcher(mBuffer[mBuffPt]);
                        //if(use_speech == true) {
                        mWakeupEngine.feedData(mBuffer[mBuffPt]);
                        //mASREngine.feedData(mBuffer[mBuffPt]);
                        /*}
                        else{
                            RecordDataManager.getInstance().feedData(mBuffer[mBuffPt]);
                        }*/
                    }
                    if (mSpeechSM.ismFeedToRecognize()) {
                       // RecordDataManager.getInstance().feedData(mBuffer[mBuffPt]);// 给SDK填充pcm语音数据
                        //byte[] pcmBuffer = mSpeechSM.feedAudioToDispatcher(mBuffer[mBuffPt]);
                        mASREngine.feedData(mBuffer[mBuffPt]); //

                        if(!mSpeechSM.isOuterRecognizing()) {
                            int vol = Common2.calculateVolumn(mBuffer[mBuffPt], mBuffer[mBuffPt].length);
                            String strText = QAICommandUtils.getVoiceResponsePacket(vol);
                            mSpeechSM.showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_VOLUME, strText);
                        }
                    }

                    if (mSpeechSM.ismFeedToCall()) {
                        AVChatManager.putAudioData(mBuffer[mBuffPt], read_len);
                    }

                    //TODO Just for test since the bug is too hard to reproduce


                    //increase buff point for static buff.
                    mBuffPt ++;
                    if(mBuffPt >= AUDIO_BUFF_SIZE){
                        mBuffPt = 0;
                    }

                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
                QAILog.d(TAG, "run: mAudioRecord1 stop and released, mKeepRecording = " + mKeepRecording + " State = " + mSpeechSM.getCurrentStateName());
                QAILog.d(TAG, "run: mAudioRecord1 thread exited!");
                mThreadExited = true;

                // FIX bug 11625：偶现底层AudioRecorder挂了，录音失败，需要重新初始化
                if (isFailedTooManyTimes) {
                    failedRestartTimes++;
                    //Sleep thread for avoid occupy too frequently.
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String failedTag = " FailedTooManyTimes restart_sum: " + failedRestartTimes;
                    QAILog.d(TAG, "run: mAudioRecord1 : " + failedTag);
                    reStartRecord(failedTag);
                    CountlyEvents.recordReadFailedTooManyTimes("Tencent_Record_" + threadTag, failedTag, failedRestartTimes);

                }
            }
        }
    }
}
