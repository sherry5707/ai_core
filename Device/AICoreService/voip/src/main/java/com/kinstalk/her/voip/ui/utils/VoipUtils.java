package com.kinstalk.her.voip.ui.utils;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.lang.reflect.Method;

public class VoipUtils {

    public static final String ACTION_LOGIN_SUCCESS = "ACTION_LOGIN_SUCCESS";
    public static final String ACTION_LOGIN_FAILED = "ACTION_LOGIN_FAILED";
    public static final String ACTION_ON_BINDER_LIST_CHANGE = "BinderListChange";   //绑定列表变化
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
            Log.e("audiomanager", "error in requestAudioFocusForCall " + e.getMessage());
            throw e;
        } catch (Exception e) {
            Log.e("audiomanager", "error in requestAudioFocusForCall " + e.getMessage());
        }
    }

    public static void abandonAudioFocusForCall(Context context) {
        try {
            @SuppressWarnings("rawtypes")
            Class audioManagerCls = Class.forName("android.media.AudioManager");
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            Method abandonMethod = audioManagerCls.getMethod("abandonAudioFocusForCall", (Class<?>[]) new Class[]{});
            abandonMethod.invoke(audioManager, new Object[]{});
        } catch (IllegalArgumentException e) {
            Log.e("audiomanager", "error in abandonAudioFocusForCall " + e.getMessage());
            throw e;
        } catch (Exception e) {
            Log.e("audiomanager", "error in abandonAudioFocusForCall " + e.getMessage());
        }
    }

}
