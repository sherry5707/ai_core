package com.kinstalk.her.voip.ring;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.kinstalk.her.voip.ui.utils.LogUtils;

import java.io.IOException;

/**
 * Created by siqing on 17/12/26.
 */

public class RingPlayerM7 implements RingManager,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private Context mContext;
    private MediaPlayer mediaPlayer = null;
    private String lastPlayName = "";
    private MediaPlayer.OnCompletionListener mListener = null;
    private int mLoopCount;

    public RingPlayerM7(Context context) {
        this.mContext = context;

    }

    @Override
    public void startRing(String name, int loop, MediaPlayer.OnCompletionListener listener) {
        LogUtils.i("RingPlayerM7 startRing... ");
        this.mListener = listener;
        this.mLoopCount = loop;
        this.mLoopCount --;
        initMediaPlayer();
        try {
            AssetManager assetManager = mContext.getAssets();
            AssetFileDescriptor afd = assetManager.openFd(name);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            LogUtils.i("RingPlayerM7 startRing... start : " + name);
        } catch (IOException e) {
            LogUtils.e("RingPlayerM7 startRing... error " + e.toString());
            mListener.onCompletion(mediaPlayer);
            stopRing();
        }
    }

    @Override
    public void stopRing() {
        LogUtils.i("RingPlayerM7 stopRing... ");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            LogUtils.i("RingPlayerM7 initMediaPlayer... ");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setLooping(false);
        } else {
            stopRing();
            initMediaPlayer();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtils.i("RingPlayerM7 onCompletion... " + mLoopCount);
        if (mLoopCount == 0) {
            if (mListener != null) {
                mListener.onCompletion(mp);
            }
        } else {
            mp.start();
            mLoopCount--;
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mListener != null) {
            mListener.onCompletion(mp);
        }
        return false;
    }

}
