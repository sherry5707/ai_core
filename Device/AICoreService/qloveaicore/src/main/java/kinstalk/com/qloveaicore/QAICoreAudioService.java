package kinstalk.com.qloveaicore;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import kinstalk.com.common.QAIConfig;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.qloveaicore.engines.IAudioDispatchListener;
import kinstalk.com.qloveaicore.statemachine.QAISpeechStatesMachine;
import kinstalk.com.qloveaicore.wakeup.tencent.RecordDataManager;


public class QAICoreAudioService extends Service implements IAudioDispatchListener {
    private static final String TAG = "AI-QAICoreAudioService";

    private int SAMPLE_RATE_IN_HZ = 16000;
    private int CHANNLE_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    //20-second audio data, about 640k
    private int MAX_AUDIO_BUFFER_COUNT = 60 * SAMPLE_RATE_IN_HZ * 2;

    private AudioListener mAudioListener = null;
    private Object mLock = new Object();

    private int mTotalCount = 0;
    private int mHeadOffset = 0;
    private byte[] mHeadBuffer = null;
    private LinkedBlockingQueue<byte[]> mAudioBuffer = new LinkedBlockingQueue<>();

    //3rd party APP has been waken up, will or doing process the audio data
    private AtomicBoolean mIsInnerRecognizing = new AtomicBoolean(false);
    private AtomicBoolean mIsOuterRecognizing = new AtomicBoolean(false);
    private AtomicBoolean mWakeupQueued = new AtomicBoolean(false);
    private QAICoreAudioService mService;

    public QAICoreAudioService() {
        QAILog.d(TAG, "QAICoreAudioService constr: Enter");

    }

    @Override
    public void onCreate() {
        QAILog.d(TAG, "onCreate: Enter version:" + QAIConfig.VERSION);
        super.onCreate();

        mService = this;
    	QAISpeechStatesMachine.getInstance().setAudioDispatchListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        QAILog.d(TAG, "onBind: Enter, i:" + intent);
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        QAILog.d(TAG, "onStartCommand: Enter,i:" + intent);
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        QAILog.d(TAG, "onUnbind: Enter," + intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        QAILog.d(TAG, "onRebind: Enter");
        super.onRebind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        QAILog.d(TAG, "onConfigurationChanged: Enter");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        QAILog.d(TAG, "onDestroy: Enter");
        super.onDestroy();

        QAISpeechStatesMachine.getInstance().setAudioDispatchListener(null);

    }

    private final IAudioRouter.Stub mBinder = new IAudioRouter.Stub() {
        @Override
        public void startRecord(int sampleRateInHz, int channelConfig, int audioFormat, IAudioDataEvent onRecordDataCb) throws RemoteException {
            if ((sampleRateInHz != SAMPLE_RATE_IN_HZ) && (audioFormat != AUDIO_FORMAT) && (channelConfig != CHANNLE_CONFIG)) {
                QAILog.d(TAG, "startRecord: wrong parameter [sampleRateInHz=" + sampleRateInHz + "] [channelConfig=" + channelConfig +
                        "] [audioFormat=" + audioFormat + "]");
                return;
            }
            synchronized (mLock){
                if (mAudioListener != null) {
                    //TODO
                    QAILog.d(TAG, "startRecord:only support one audio listener now");
                    return;
                }
            }
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();

            QAILog.d(TAG, "startRecord: process " + pid + " start to recording");
            ListenerRecord record = new ListenerRecord(onRecordDataCb, pid, uid);
            onRecordDataCb.asBinder().linkToDeath(record, 0);
            synchronized (mLock) {
                mAudioListener = new AudioListener(pid, uid, record);
                if(mWakeupQueued.get()) {
                    mAudioListener.getListenerRecord().getCallback().onAudioDataEvent(1, "");
                    mIsOuterRecognizing.set(true);
                    QAILog.d(TAG, "startRecord: notify process " + mAudioListener.getPid());
                    mWakeupQueued.set(false);
                } else {
                    mTotalCount = 0;
                    mAudioBuffer.clear();
                }
            }
        }

        @Override
        public void stopRecord() throws RemoteException {
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            QAILog.d(TAG, "stopRecord: process " + pid);

            synchronized (mLock) {
                if ((mAudioListener != null) && (pid == mAudioListener.getPid())) {
                    mAudioListener.getListenerRecord().setAudioListener(null);
                    mAudioListener = null;
                    mTotalCount = 0;
                    mAudioBuffer.clear();
                    if(mIsOuterRecognizing.get()) {
                        RecordDataManager.getInstance().onExternalAsrEvent(null);
                    }
                    mIsOuterRecognizing.set(false);
                }
            }
        }

        @Override
        public int readAudioData(byte[] record_buffer) throws RemoteException {
            if((record_buffer == null) || (record_buffer.length == 0)) {
                return 0;
            }
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();

            synchronized (mLock) {
                if ((mAudioListener == null) || (pid != mAudioListener.getPid())) {
                    return 0;
                }
            }

            int toRead = record_buffer.length;
            while(true) {
                try {
                    if(mHeadBuffer != null) {
                        if((mHeadBuffer.length - mHeadOffset) <= toRead) {
                            toRead = mHeadBuffer.length - mHeadOffset;
                        }
                        System.arraycopy(mHeadBuffer, mHeadOffset, record_buffer, 0, toRead);
                        mHeadOffset += toRead;
                        if(mHeadBuffer.length <= mHeadOffset) {
                            mHeadBuffer = null;
                        }
                        //QAILog.d(TAG, "readAudioData offset:" + mHeadOffset + " toRead:" + toRead);
                        break;
                    } else {
                        mHeadBuffer = mAudioBuffer.take();
                        mTotalCount -= mHeadBuffer.length;
                        mHeadOffset = 0;

                        if(mHeadBuffer.length == 0) {
                            toRead = 0;
                            break;
                        }
                        /*QAILog.d(TAG, "HeadBuffer length:" + mHeadBuffer.length);
                        if(mHeadBuffer.length > 640) {
                            FileUtils.saveFile("/sdcard/",mHeadBuffer, "audio-tx", "pcm", false);
                        }*/
                    }

                } catch (InterruptedException e) {
                    QAILog.d(TAG, "readAudioData:" + e);
                    toRead = 0;
                    break;
                }
            }
            //QLog.d(TAG, "read data:" + toRead);
            return toRead;
        }

        @Override
        public void onAsrEvent(String jsonResult) throws RemoteException {
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();

            QAILog.d(TAG, "onAsrEvent:" + jsonResult);
            synchronized (mLock) {
                if ((mAudioListener == null) || (pid != mAudioListener.getPid())) {
                    return;
                }
                if(mAudioListener != null) {
                    mAudioListener.clearError();
                }
            }
            RecordDataManager.getInstance().onExternalAsrEvent(jsonResult);

            mIsOuterRecognizing.set(false);
            mTotalCount = 0;
            mAudioBuffer.clear();
            try {
                //write a 0 length buffer for readAudioData()
                mAudioBuffer.put(new byte[0]);
            } catch (InterruptedException e) {
                QAILog.d(TAG, "onAsrEvent:" + e);
            }
        }
    };

    //BEGIN impelemnt AbsSpeechController.AudioDispatchListener
    @Override
    public byte[] write(byte[] buffer) {
        if((buffer == null) || (buffer.length == 0)) {
            return buffer;
        }

        synchronized (mLock) {
            if (mAudioListener == null) {
                mIsOuterRecognizing.set(false);
            }
        }

        if(mIsOuterRecognizing.get() || mIsInnerRecognizing.get()) {
            try{
                byte[] audioData = new byte[buffer.length];
                System.arraycopy(buffer, 0, audioData, 0, buffer.length);
                mAudioBuffer.put(audioData);

                mTotalCount += buffer.length;
                if(mIsOuterRecognizing.get() && (mTotalCount > MAX_AUDIO_BUFFER_COUNT)) {
                    QAILog.d(TAG, "something wrong, too many audio buffers queued");
                    if(mIsOuterRecognizing.get()) {
                        RecordDataManager.getInstance().onExternalAsrEvent(null);
                    }
                    mIsOuterRecognizing.set(false);
                    mTotalCount = 0;
                    mAudioBuffer.clear();
                    synchronized (mLock) {
                        if(mAudioListener != null) {
                            mAudioListener.onError();
                        }
                    }
                }
            } catch (InterruptedException e) {
                QAILog.d(TAG, "write buffer:" + e);
            }
        }
        if(!mIsOuterRecognizing.get()) {
            return buffer;
        } else {
            //if the 3rd-party is processing audio, return a silence audio frame
            return new byte[buffer.length];
        }
    }

    @Override
    public boolean onWakeup() {
        mIsInnerRecognizing.set(true);
        mWakeupQueued.set(false);

        if(mIsOuterRecognizing.get()) {
            //3rd party has been waked, do nothing
            return true;
        }
        mAudioBuffer.clear();
        //has been wake up, notify 3rd party
        boolean waken = false;

        synchronized (mLock) {
            if (mAudioListener != null) {
                if(mAudioListener.isDead()) {
                    QAILog.d(TAG, "onWakeup: something wrong with client, do not read audio data");
                    waken = false;
                } else {
                    try {
                        mIsOuterRecognizing.set(true);
                        mAudioListener.getListenerRecord().getCallback().onAudioDataEvent(1, "");
                        QAILog.d(TAG, "onWakeup: notify process " + mAudioListener.getPid());
                        waken = true;
                    } catch (RemoteException e) {
                        if(mIsOuterRecognizing.get()) {
                            RecordDataManager.getInstance().onExternalAsrEvent(null);
                        }
                        mIsOuterRecognizing.set(false);
                        QAILog.d(TAG, "onWakeup:" + e);
                        mAudioListener.getListenerRecord().setAudioListener(null);
                        mAudioListener = null;
                        //TODO there should be only one 3rd APP, what about many?
                        waken = false;
                    }
                }
            }
        }
        return waken;
    }

    @Override
    public void queueWakeupNotification() {
        synchronized (mLock) {
            if (mAudioListener != null) {
                if(mAudioListener.isDead()) {
                    QAILog.d(TAG, "qeuueWakeupNotification: something wrong with client, do not read audio data");
                } else {
                    try {
                        if(!mIsOuterRecognizing.get()) {
                            //client has callback registered, but not waken up yet
                            //wake up it now
                            mAudioListener.getListenerRecord().getCallback().onAudioDataEvent(1, "");
                            QAILog.d(TAG, "qeuueWakeupNotification: notify process " + mAudioListener.getPid());
                            mIsOuterRecognizing.set(true);
                        } else {
                            //the client is receiving audio frame, do not wakeup
                        }
                    } catch (RemoteException e) {
                        if(mIsOuterRecognizing.get()) {
                            RecordDataManager.getInstance().onExternalAsrEvent(null);
                        }
                        mIsOuterRecognizing.set(false);
                        QAILog.d(TAG, "qeuueWakeupNotification:" + e);
                        mAudioListener.getListenerRecord().setAudioListener(null);
                        mAudioListener = null;

                    }
                }
            } else {
                //not callback requested, queue the notificaiton
                mWakeupQueued.set(true);
            }
        }
    }

    @Override
    public void onIdle() {
        mIsInnerRecognizing.set(false);
    }

    @Override
    public boolean isOuterRecognizing() {
        return mIsOuterRecognizing.get();
    }
    //END impelemnt AbsSpeechController.AudioDispatchListener

    final class ListenerRecord implements IBinder.DeathRecipient {
        private final IAudioDataEvent mCallback;
        private int mPid;
        private int mUid;
        private AudioListener mListener;

        ListenerRecord(IAudioDataEvent callback, int pid, int uid) {
            mCallback = callback;
            mPid = pid;
            mUid = uid;
        }

        public void setAudioListener(AudioListener listener) {
            mListener = listener;
        }

        @Override
        public void binderDied() {
            synchronized (mLock) {
                mAudioListener = null;
            }
            this.mListener = null;
            if(mIsOuterRecognizing.get()) {
                RecordDataManager.getInstance().onExternalAsrEvent(null);
            }
            mIsOuterRecognizing.set(false);
        }

        IAudioDataEvent getCallback() {
            return mCallback;
        }
    }

    private class AudioListener {
        private int uid;
        private int pid;
        private int mError = 0;
        private ListenerRecord record;

        AudioListener(int pid, int uid, ListenerRecord record) {
            this.pid = pid;
            this.uid = uid;
            this.record = record;
        }

        int getPid() {
            return pid;
        }

        int getUid() {
            return uid;
        }

        boolean isSame(int pid, int uid) {
            return ((pid == this.pid) && (uid == this.uid));
        }

        ListenerRecord getListenerRecord() {
            return record;
        }

        void clearError() {
            mError = 0;
        }
        void onError() {
            mError++;
        }

        boolean isDead() {
            return mError >= 3;
        }
    }
}
