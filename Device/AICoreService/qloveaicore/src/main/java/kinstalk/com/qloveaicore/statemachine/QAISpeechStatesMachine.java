package kinstalk.com.qloveaicore.statemachine;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.kinstalk.her.voip.manager.AVChatManager;
import com.tencent.xiaowei.util.QLog;

import kinstalk.com.common.sm.State;
import kinstalk.com.common.sm.StateMachine;
import kinstalk.com.common.utils.QAICommandUtils;
import kinstalk.com.common.utils.QAIConstants;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.SystemTool;
import kinstalk.com.qloveaicore.AIClients;
import kinstalk.com.qloveaicore.AICoreDef;
import kinstalk.com.qloveaicore.ICoreController;
import kinstalk.com.qloveaicore.R;
import kinstalk.com.qloveaicore.engines.ASRListener;
import kinstalk.com.qloveaicore.engines.IAudioDispatchListener;
import kinstalk.com.qloveaicore.engines.WakeupListener;
import kinstalk.com.qloveaicore.wakeup.tencent.RecordDataManager;

import static kinstalk.com.qloveaicore.AICoreDef.QLServiceType.TYPE_HEALTH;


/**
 * Created by shaoyi on 2018/6/4.
 */

public class QAISpeechStatesMachine  extends StateMachine {
    private static final String TAG = "AI-QSState Machine";
    //messages
    //private static final int MSG_GENERAL_BASE = 100;

    private static final int MSG_TRANSITION_BASE = 900;
    private static final int MSG_TRANSITION_TO_INIT = MSG_TRANSITION_BASE + 1;
    private static final int MSG_TRANSITION_TO_IDLE= MSG_TRANSITION_BASE + 2;
    private static final int MSG_TRANSITION_TO_RECOGNIZE = MSG_TRANSITION_BASE + 3;
    private static final int MSG_TRANSITION_TO_CALL = MSG_TRANSITION_BASE + 4;
    private static final int MSG_TRANSITION_TO_SUSPEND = MSG_TRANSITION_BASE + 5;
    private static final int MSG_RESUME_FROM_SUSPEND = MSG_TRANSITION_BASE + 6;
    private static final int MSG_TRANSITION_TO_WAKEUPFEEDBACK = MSG_TRANSITION_BASE + 7;

    private static final int MSG_RECOGNIZE_TIMEOUT = MSG_TRANSITION_BASE + 10;


    private static final int RECOGNIZE_TIMEOUT = 35 * 1000;
    private static final int WAKEUPFEEDBACK_TIMEOUT = 1000;


    private static QAISpeechStatesMachine sInst;


    private InitState mInitState = new InitState();
    private IdleState mIdleState = new IdleState();
    private RecognizeState mRecognizeState = new RecognizeState();
    private CallState mCallState = new CallState();
    private SuspendState mSuspendState = new SuspendState();
    private FeedbackState mFeedbackState = new FeedbackState();

    private boolean mFeedToWakeUp = false ;
    private boolean mFeedToRecognize = false ;
    private boolean mFeedToCall = false ;
    private boolean mFeedbackStateFlag = false;
    private boolean mWakeupEnabled = false;


    private Context mContext;

    //----------------------------------------------------
    private ICoreController mCoreController;
    private SpeechController mSpeechController;
    private boolean mDebug = !(SystemTool.isUserType());
    private Handler mHandler = new Handler();
    //add for 3rd-party NLP
    private IAudioDispatchListener mADListener;
    private MediaPlayer mMediaPlayer = null;



    public QAISpeechStatesMachine(String name) {
        super(name);
        addState(mInitState);
        addState(mIdleState);
        addState(mFeedbackState);
        addState(mRecognizeState);
        addState(mCallState);
        addState(mSuspendState);
        setInitialState(mInitState);

    }
/*  public synchronized static QAISpeechStatesMachine getInstance(Context c, ICoreController coreController) {
        if (sInst == null) {
            sInst = new QAISpeechStatesMachine("QSStateInit", c,coreController);
        }
        return sInst;
    }*/

    public synchronized static QAISpeechStatesMachine getInstance()
    {
        if (sInst == null) {
            sInst = new QAISpeechStatesMachine("QSStateInit");
        }
        return sInst;
    }
    public void start(Context c, ICoreController coreController)
    {
        mContext = c;
        mCoreController = coreController;
        start();
    }
    // start init process.
    private void  init(){
        //created speech controller
        mSpeechController= SpeechController.getInstance();
        mSpeechController.init(mContext,mCoreController,this);
        mSpeechController.onCreate(new WakeupEngineListenerImpl(),new ASREngineListenerImpl());

        //register boardcast.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        intentFilter.addAction(AVChatManager.ACTION_ENTER_VOIP);
        intentFilter.addAction(AVChatManager.ACTION_QUIT_VOIP);
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.wozai);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener());
    }

    class OnCompletionListener implements android.media.MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            QAILog.i(TAG, "OnCompletionListener");
            gotoRecognize();
        }
    }

    private class WakeupEngineListenerImpl implements WakeupListener {
        @Override
        public void onInit(int var1){
            if(var1 == 0)//
            {
                QAILog.v(TAG, "Init Wakeup Engine OK.");
                gotoIdle();
            }
            else{
                QAILog.v(TAG, "Init Wakeup Engine Failed:"+ var1);
            }
        }
        @Override
        public void onError(String var1){

        }
        @Override
        public void onReady(){

        }

        @Override
        public void onWakeup(String var1,int score) {
            QAILog.v(TAG, "On wakeup event.");
            if(mWakeupEnabled == false)
                return;
            if(!SystemTool.checkNet(mContext)){
                return;
            }
            AIClients.ClientInfo clientInfo = AIClients.getInstance().getClientInfoByType(TYPE_HEALTH);
            if (clientInfo != null) {
                mSpeechController.wakeUpWordDataSave();
            }
            gotoWakeUpFeedback();

        }
    }

    private class ASREngineListenerImpl implements ASRListener {
        @Override
        public void onInit(int status)
        {
            QAILog.v(TAG, "Init ASR Engine OK.");
        }

        @Override
        public void onStop(){
            QAILog.v(TAG, "Stop ASR .");
        }

        @Override
        public void onCmd(String results){
            //RecordDataManager.getInstance().onSleep();
            mCoreController.handleGeneralCmd(results);
        }

        @Override
        public void onPartialResult(String result, int type){
            if(type == AICoreDef.WATER_ANIM_CMD_STOP_RECORD){
                String strTextq = QAICommandUtils.getAnimationResponsePacket(AICoreDef.WATER_ANIM_CMD_STOP_RECORD, "");
                showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_STOP_RECORD, strTextq);
            }else if(type == AICoreDef.WATER_ANIM_CMD_START_RECORD)
            {
                String strText = QAICommandUtils.getAnimationResponsePacket(AICoreDef.WATER_ANIM_CMD_START_RECORD, "");
                showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_START_RECORD, strText);
            }
            else{
                String strTextf = QAICommandUtils.getAnimationResponsePacket(AICoreDef.WATER_ANIM_CMD_SHOW_TEXT, result);
                showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_SHOW_TEXT, strTextf);
            }
        }

        @Override
        public void onResults(String results, String answer){
            QAILog.v(TAG, "ASR Result."+ results);
            if(!TextUtils.isEmpty(answer)){
                if(answer!="")
                    showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_SHOW_TEXT, answer);

            }
            //String strText="";
            //if(!TextUtils.equals(results,""))
            String   strText = QAICommandUtils.getAnimationResponsePacket(AICoreDef.WATER_ANIM_CMD_ENDOFSPEECH,"{\""+"text"+"\":"+results+"}" );//WATER_ANIM_CMD_ENDOFSPEECH
            showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_ENDOFSPEECH, strText);

            gotoIdle();
        }
        @Override
        public void onError(String var1, int var2){

        }
        @Override
        public void onBeginningOfSpeech(){
            QAILog.v(TAG, "Init ASR Engine OK.");

        }
        @Override
        public void onEndOfSpeech(){
            QAILog.v(TAG, "ASR Engine End of speech.");

        }
        @Override
        public void onReady(){
            QAILog.v(TAG, "ASR Engine ready.");
        }

    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
                if (state == BluetoothAdapter.STATE_CONNECTED) {
                    QLog.d(TAG, "bluetooth is connected");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSpeechController.connectToSco();
                        }
                    }, 1000);
                } else if (state == BluetoothAdapter.STATE_DISCONNECTED) {
                    QLog.d(TAG, "bluetooth is disconnected");
                    mSpeechController.disConnectToSco();
                }
            } else if (AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED.equals(action)) {
                int previous = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_PREVIOUS_STATE, -1);
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                QLog.d(TAG, "AudioManager startBluetoothSco end time:" + System.currentTimeMillis() + "|state:" + state + "|previous:" + previous);
                //TODO voip 已经支持audiomode切换，不用跟蓝牙关联
            } else if(AVChatManager.ACTION_ENTER_VOIP.equals(action)) {
                gotoCall();
            }
            else if(AVChatManager.ACTION_QUIT_VOIP.equals(action)) {
                gotoIdle();
            }
        }
    };

//--------------------------------------------------------------------------------------------------

    public boolean ismFeedToWakeUp() {
        return mFeedToWakeUp;
    }

    public boolean ismFeedToRecognize() {
        return mFeedToRecognize;
    }

    public boolean ismFeedToCall() {
        return mFeedToCall;
    }

    public boolean isCallState(){
        return getCurrentStateName().equals(mCallState.getName());
    }
    public  void setWakeupEnabled(boolean enabled){
        mWakeupEnabled = enabled;
    }

    public String getCurrentStateName(){
        if(getCurrentState() == null){
            return "";//return
        }
        return getCurrentState().getName();
    }

    public boolean gotoIdle() {
        QAILog.d(TAG, "gotoIdle: E");
        if (!TextUtils.equals(getCurrentStateName(), mIdleState.getName())) {
            QAILog.d(TAG, "gotoWakeup: send MSG_TRANSITION_TO_IDLE");
            sendMessage(this.obtainMessage(MSG_TRANSITION_TO_IDLE));
        }
        return true;
    }

    public boolean gotoWakeUpFeedback() {
        QAILog.d(TAG, "gotoWakeUpFeedback: send MSG_TRANSITION_TO_WAKEUPFEEDBACK");
        sendMessage(this.obtainMessage(MSG_TRANSITION_TO_WAKEUPFEEDBACK));

        return true;
    }

    public boolean gotoRecognize() {
       // QAILog.d(TAG, "gotoRecognize: E");
        //if (!TextUtils.equals(getCurrentState().getName(), mRecognizeState.getName())) {
        QAILog.d(TAG, "gotoRecognize: send MSG_TRANSITION_TO_RECOGNIZE");
        sendMessage(this.obtainMessage(MSG_TRANSITION_TO_RECOGNIZE));
        //}
        return true;
    }

    public boolean gotoCall() {
        QAILog.d(TAG, "gotoCall: send MSG_TRANSITION_TO_CALL");
        sendMessage(this.obtainMessage(MSG_TRANSITION_TO_CALL));
        return true;
    }


    //E.g when entering MediaRecorder, we'll lose the mic, in this case we suspend.
    public boolean gotoSuspend() {

        QAILog.d(TAG, "gotoSuspend: send  MSG_TRANSITION_TO_SUSPEND ");
        sendMessage(this.obtainMessage(MSG_TRANSITION_TO_SUSPEND));

        return true;
    }
    public void setAudioDispatchListener(IAudioDispatchListener listener) {
        mADListener = listener;
        QAILog.d(TAG, "setAudioDispatchListener");
    }
    /*public QAISpeechStatesMachine getState() {
        return state;
    }*/
    public boolean isOuterRecognizing() {
        if(mADListener != null) {
            return mADListener.isOuterRecognizing();
        }
        return false;
    }
    //
    public void manualWakeup(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RecordDataManager.getInstance().manualWakeup();
        gotoRecognize();
        //mSpeechController.manualWakeup();
    }


    public void showAnim(int type, int command, Object param) {
        if(!isOuterRecognizing()) {
            if(command != 10) {
                QAILog.d(TAG, "showAnim:" + type + "," + command + "," + param);
            }
            mCoreController.handleEventMethod(type, command, param);
        }
    }

    public byte[] feedAudioToDispatcher(byte[] buffer) {
        if(mADListener != null) {
            return mADListener.write(buffer);
        } else {
            return buffer;
        }
    }

    public void queueWakeupNotification() {
        if(mADListener != null) {
            QAILog.d(TAG, "queueWakeupNotification");
            mADListener.queueWakeupNotification();
        }
    }

    //audio dispatch feature
    public boolean onWakeupExternal() {
        return notifyWakeup();
    }

    private boolean notifyWakeup() {
        if(mADListener != null) {
            QAILog.d(TAG, "notifyWakeUp");
            return mADListener.onWakeup();
        } else {
            return false;
        }
    }

    public void notifyIdle() {
        if(mADListener != null) {
            QAILog.d(TAG, "notifyIdle");
            mADListener.onIdle();
        }
    }


    //------------------Initial state------------------//
    private class InitState extends State {
        private static final String TAG = "AI-QSState Init";

        protected InitState() {
            super();
            QAILog.d(TAG, "InitState: Created");
        }

        @Override
        public void enter() {
            super.enter();
            QAILog.d(TAG, "State_enter: ");
            init();
        }

        @Override
        public void exit() {
            super.exit();
            QAILog.d(TAG, "State_exit: ");
        }

        @Override
        public boolean processMessage(Message msg) {
            QAILog.d(TAG, "processMessage: " + msg.what);
            boolean handled = NOT_HANDLED;
            switch (msg.what) {
                case MSG_TRANSITION_TO_IDLE:
                    transitionTo(mIdleState);
                      handled = HANDLED;
                    break;
                default:
                    handled = NOT_HANDLED;
                    break;
            }

            return handled;
        }

        @Override
        public String getName() {
            //QAILog.d(TAG, "getName: ");
            return TAG;
        }
    }
    //------------------Initial state end-------------//

    //------------------Idle(Idle state to replace wkeup starte) state-------------//
    private class IdleState extends State {
        private static final String TAG = "AI-QSState Idle";

        protected IdleState() {
            super();
            QAILog.d(TAG, "IdleState: Created");
        }
        @Override
        public void enter() {
            super.enter();
            showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_ENDOFSPEECH, "");
            mFeedToWakeUp = true;
            notifyIdle();
            QAILog.d(TAG, "State_enter: mFeedToWakeUp: "+ mFeedToWakeUp);
        }

        @Override
        public void exit() {
            super.exit();
            mFeedToWakeUp = false;
            QAILog.d(TAG, "State_exit: mFeedToWakeUp: "+ mFeedToWakeUp);
        }

        @Override
        public boolean processMessage(Message msg) {
            QAILog.d(TAG, "processMessage: " + msg.what);
            boolean handled = HANDLED;
            switch (msg.what) {
                case MSG_TRANSITION_TO_RECOGNIZE:
                    transitionTo(mRecognizeState);
                    handled = HANDLED;
                    break;
                case MSG_TRANSITION_TO_CALL:
                    transitionTo(mCallState);
                    handled = HANDLED;
                    break;
                case MSG_TRANSITION_TO_WAKEUPFEEDBACK:
                    transitionTo(mFeedbackState);
                    handled = HANDLED;
                    break;
                default:
                    handled = NOT_HANDLED;
                    break;
            }

            return handled;
        }

        @Override
        public String getName() {
            //QAILog.d(TAG, "getName: ");
            return TAG;
        }
    }
    //------------------WakeUpState state end ---------//

    //------------------WakeUpFeedback state -----------//
    private class FeedbackState extends State {
        private static final String TAG = "AI-QSState WakeupFeedback";

        protected FeedbackState() {
            super();
            QAILog.d(TAG, "WakeupFeedback: Created");
        }

        @Override
        public void enter() {
            super.enter();
            mFeedbackStateFlag = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mMediaPlayer.start();
                }
            }).start();

            if(!notifyWakeup()) {
                showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_WAKEUP,
                        QAICommandUtils.getAnimationResponsePacket(AICoreDef.WATER_ANIM_CMD_WAKEUP, ""));
            }

            sendMessageDelayed(MSG_TRANSITION_TO_RECOGNIZE, WAKEUPFEEDBACK_TIMEOUT);
            QAILog.d(TAG, "State_enter: mFeedToWakeUp:"+mFeedToWakeUp);
        }

        @Override
        public void exit() {
            super.exit();
            removeMessages(MSG_TRANSITION_TO_RECOGNIZE);
            QAILog.d(TAG, "State_exit: mFeedToWakeUp:"+mFeedToWakeUp);
        }

        @Override
        public boolean processMessage(Message msg) {
            QAILog.d(TAG, "processMessage: " + msg.what);
            boolean handled = HANDLED;
            switch (msg.what) {
                case MSG_TRANSITION_TO_IDLE:
                    transitionTo(mIdleState);
                    handled = HANDLED;
                    break;
                case MSG_TRANSITION_TO_RECOGNIZE:
                    transitionTo(mRecognizeState);
                    handled = HANDLED;
                    break;
                case MSG_TRANSITION_TO_CALL:
                    transitionTo(mCallState);
                    handled = HANDLED;
                    break;
                case MSG_TRANSITION_TO_WAKEUPFEEDBACK:
                    transitionTo(mFeedbackState);
                    handled = HANDLED;
                    break;
                default:
                    handled = NOT_HANDLED;
                    break;
            }

            return handled;
        }

        @Override
        public String getName() {
            //QAILog.d(TAG, "getName: ");
            return TAG;
        }
    }
    //------------------WakeUpFeedback state end -------//

    //------------------RecognizeState state------------//
    private class RecognizeState extends State {
        private static final String TAG = "AI-QSState Recognize";

        protected RecognizeState() {
            super();
            QAILog.d(TAG, "RecognizeState: Created");
        }
        @Override
        public void enter() {
            super.enter();
            mFeedToRecognize = true;

            mSpeechController.mASREngine.start();
            if (mFeedbackStateFlag == false) {
                if (!notifyWakeup()) {
                    showAnim(QAIConstants.AI_CORE_COMMAND_SHOW_ANIM, AICoreDef.WATER_ANIM_CMD_WAKEUP,
                            QAICommandUtils.getAnimationResponsePacket(AICoreDef.WATER_ANIM_CMD_WAKEUP, ""));
                }
            }
            mFeedbackStateFlag = false;
            sendMessageDelayed(MSG_RECOGNIZE_TIMEOUT, RECOGNIZE_TIMEOUT);
            QAILog.d(TAG, "State_enter: mFeedToRecognize:"+mFeedToRecognize);
        }

        @Override
        public void exit() {
            super.exit();
            mFeedToRecognize = false;
            removeMessages(MSG_RECOGNIZE_TIMEOUT);
            QAILog.d(TAG, "State_exit: mFeedToRecognize:"+mFeedToRecognize);
        }

        @Override
        public boolean processMessage(Message msg) {
            QAILog.d(TAG, "processMessage: " + msg.what);
            boolean handled = HANDLED;
            switch (msg.what) {
                case MSG_TRANSITION_TO_IDLE:
                    transitionTo(mIdleState);
                    handled = HANDLED;
                    break;
                case MSG_RECOGNIZE_TIMEOUT:
                    QAILog.d(TAG, "Recognize State change timeout");
                    mSpeechController.mASREngine.stop();
                    handled = HANDLED;
                    break;
                case MSG_TRANSITION_TO_CALL:
                    transitionTo(mCallState);
                    handled = HANDLED;
                    break;
                default:
                    handled = NOT_HANDLED;
                    break;
            }

            return handled;
        }

        @Override
        public String getName() {
            //QAILog.d(TAG, "getName: ");
            return TAG;
        }
    }
    //------------------RecognizeState state end ---------//
    //------------------Call state------------//
    private class CallState extends State {
        private static final String TAG = "AI-QSState Call";

        protected CallState() {
            super();
            QAILog.d(TAG, "CallState: Created");
        }
        @Override
        public void enter() {
            super.enter();
            mFeedToRecognize = false;
            mFeedToWakeUp = false;
            mFeedToCall = true;
            QAILog.d(TAG, "State_enter: mFeedToRecognize = false mFeedToWakeUp = false");
        }

        @Override
        public void exit() {
            super.exit();
            mFeedToCall = false;
            QAILog.d(TAG, "State_exit: ");
        }

        @Override
        public boolean processMessage(Message msg) {
            QAILog.d(TAG, "processMessage: " + msg.what);
            boolean handled = HANDLED;
            switch (msg.what) {
                case MSG_TRANSITION_TO_IDLE:
                    transitionTo(mIdleState);
                    handled = HANDLED;
                    break;
                default:
                    handled = NOT_HANDLED;
                    break;
            }

            return handled;
        }

        @Override
        public String getName() {
            //QAILog.d(TAG, "getName: ");
            return TAG;
        }
    }
    //------------------Call state end ---------//
    //------------------Suspend state------------//
    private class SuspendState extends State {
        private static final String TAG = "AI-QSState Suspend";

        protected SuspendState() {
            super();
            QAILog.d(TAG, "SuspendState: E");
        }
        @Override
        public void enter() {
            super.enter();
            QAILog.d(TAG, "State_enter: ");
        }

        @Override
        public void exit() {
            super.exit();
            QAILog.d(TAG, "State_exit: ");
        }

        @Override
        public boolean processMessage(Message msg) {
            QAILog.d(TAG, "processMessage: " + msg.what);
            boolean handled = HANDLED;
            switch (msg.what) {

                default:
                    handled = NOT_HANDLED;
                    break;
            }

            return handled;
        }

        @Override
        public String getName() {
           // QAILog.d(TAG, "getName: ");
            return TAG;
        }
    }
    //------------------Suspend state end ---------//

}
