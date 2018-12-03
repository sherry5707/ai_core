package com.kinstalk.her.voip.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

import com.tencent.xiaowei.util.QLog;

import java.lang.reflect.Method;

public class AudioFocusManager {
    private static Context mContext;
    private static final String TAG = "AudioFocusHelper";
    private volatile static AudioFocusManager instance; // 实例
    private OnAudioFocusChangeListener mAudioFocusChangeListener;
    private AudioManager mAudioManager;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final int AUDIO_FOCUSED  = 2;
    private int mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;

    public static AudioFocusManager getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            synchronized (AudioFocusManager.class) {
                if (instance == null) {
                    instance = new AudioFocusManager();
                }
            }
        }
        return instance;
    }

    private AudioFocusManager() {
        if(null != mContext){
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
    }

    /**
     * 设置 audiofocus changelistener,只调一次
     */
    public void setAudioFocusChangeListener(OnAudioFocusChangeListener listener){
        if(null == mAudioManager && null != mContext){
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        mAudioFocusChangeListener = listener;
        QLog.i(TAG, "setAudioFocusChangeListener");
    }

    /**
     * get AudioFocus,获取音频焦点
     *
     */
    public static void requestAudioFocusForCall(Context context, int streamType, int durationHint) {
        try {
            @SuppressWarnings("rawtypes")
            Class audioManagerCls = Class.forName("android.media.AudioManager");
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            Method requestAudioMethod = audioManagerCls.getMethod("requestAudioFocusForCall", new Class[]{
                    int.class, int.class
            });
            requestAudioMethod.invoke(audioManager, new Object[]{
                    streamType, durationHint
            });
        } catch (IllegalArgumentException e) {
            QLog.e("audiomanager", "error in requestAudioFocusForCall " + e.getMessage());
            throw e;
        } catch (Exception e) {
            QLog.e("audiomanager", "error in requestAudioFocusForCall " + e.getMessage());
        }
    }

    /**
     * give up AudioFocus，播放完成调用
     * return true: stop success
     */
    public static void abandonAudioFocusForCall(Context context) {
        try {
            @SuppressWarnings("rawtypes")
            Class audioManagerCls = Class.forName("android.media.AudioManager");
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            Method abandonMethod = audioManagerCls.getMethod("abandonAudioFocusForCall", (Class<?>[]) new Class[]{});
            abandonMethod.invoke(audioManager, new Object[]{});
        } catch (IllegalArgumentException e) {
            QLog.e("audiomanager", "error in abandonAudioFocusForCall " + e.getMessage());
            throw e;
        } catch (Exception e) {
            QLog.e("audiomanager", "error in abandonAudioFocusForCall " + e.getMessage());
        }
    }
    /**
     * 释放资源
     */
    public void release(){
        mAudioManager = null;
        instance = null;
    }
}

