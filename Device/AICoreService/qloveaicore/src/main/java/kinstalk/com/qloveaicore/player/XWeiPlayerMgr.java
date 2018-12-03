/*
 * Tencent is pleased to support the open source community by making  XiaoweiSDK Demo Codes available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package kinstalk.com.qloveaicore.player;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.tencent.xiaowei.control.Constants;
import com.tencent.xiaowei.control.IXWeiPlayer;
import com.tencent.xiaowei.control.IXWeiPlayerMgr;
import com.tencent.xiaowei.control.info.XWeiMediaInfo;
import com.tencent.xiaowei.control.info.XWeiPlayState;

import java.util.HashMap;

import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.QAIUtils;
import kinstalk.com.qloveaicore.ITTSCallback;
import kinstalk.com.qloveaicore.QAIAudioFocusMgr;

import static com.tencent.xiaowei.control.IXWeiPlayer.IXW_PARAM_TYPE_NOTIFY_CB;
import static com.tencent.xiaowei.control.IXWeiPlayer.IXW_PARAM_TYPE_TTS_CB;

/**
 * 播放器示例
 */
public class XWeiPlayerMgr implements IXWeiPlayerMgr {
    private static final String TAG = "XWeiPlayerMgr";
    private static int mAudioSessionId;
    private static SkillUIEventListener mSkillUIEventListener;
    private HashMap<Integer, IXWeiPlayer> players = new HashMap<>();
    private Context context;
    // szjy begin
    private IXWeiPlayer mPlayingPlayer;
    private IXWeiPlayer mIdlePlayer;
    private static final int Q_PLAYERS_NUM = 1;
    private final IXWeiPlayer[] mQPlayers;
    private final Object mPlayerLock = new Object();
    private XWeiPlayer.IXWeiPlayerNotify mXWeiPlayerNotify;
    // szjy end

    public static final String ACTION_ON_SUPPLEMENT = "action_on_supplement";  // 开启多轮会话
    public static final String EXTRA_KEY_SESSION_ID = "extra_key_session_id";
    public static final String EXTRA_KEY_CONTEXT_ID = "extra_key_context_id";
    public static final String EXTRA_KEY_SPEAK_TIMEOUT = "extra_key_speak_timeout";
    public static final String EXTRA_KEY_SILENT_TIMEOUT = "extra_key_silent_timeout";

    public XWeiPlayerMgr(Context context) {
        this.context = context;
        // szjy begin
        mXWeiPlayerNotify = new XWPlayerNotify();
        mPlayingPlayer = getPlayer(0);
        mPlayingPlayer.setParam(IXW_PARAM_TYPE_NOTIFY_CB, mXWeiPlayerNotify);
        mIdlePlayer = getPlayer(1);
        mIdlePlayer.setParam(IXW_PARAM_TYPE_NOTIFY_CB, mXWeiPlayerNotify);

        mQPlayers = new IXWeiPlayer[Q_PLAYERS_NUM];
        for (int i = 0; i < Q_PLAYERS_NUM; i++) {
            mQPlayers[i] = new XWeiPlayer(i,context);
            mQPlayers[i].setParam(IXW_PARAM_TYPE_NOTIFY_CB, mXWeiPlayerNotify);
        }
        // szjy end
    }

    public static void setAudioSessionId(int audioSessionId) {
        mAudioSessionId = audioSessionId;
    }

    public static void setPlayerEventListener(SkillUIEventListener skillUIEventListener) {
        mSkillUIEventListener = skillUIEventListener;
    }

    public synchronized IXWeiPlayer getPlayer(int sessionId) {

        IXWeiPlayer player = players.get(sessionId);

        synchronized (this) {
            if (player == null) {
                player = new XWeiPlayer(sessionId ,context);
                players.put(sessionId, player);
            }

        }

        return player;
    }

    @Override
    public IXWeiPlayer getXWeiPlayer(int sessionId) {
        return players.get(sessionId);
    }

    @Override
    public boolean OnPlayFinish(int sessionId) {
        IXWeiPlayer player = getPlayer(sessionId);

        boolean handled = (player != null && player.stop(sessionId));

        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onFinish(sessionId);
        }
        if (player != null)
            player.release();
        players.remove(sessionId);

        return handled;
    }

    @Override
    public boolean OnStopPlayer(int sessionId) {
        return OnPausePlayer(sessionId, true);
    }

    @Override
    public boolean OnPausePlayer(int sessionId, boolean pause) {
        IXWeiPlayer player = getPlayer(sessionId);

        boolean handled = (player != null && (pause ? player.pause(sessionId) : player.resume(sessionId)));
        if (mSkillUIEventListener != null) {
            if (pause) {
                mSkillUIEventListener.onPause(sessionId);
            } else {
                mSkillUIEventListener.onResume(sessionId);
            }
        }

        return handled;
    }

    @Override
    public boolean OnChangeVolume(int sessionId, int volume) {
        IXWeiPlayer player = getPlayer(sessionId);
        return (player != null && player.changeVolume(sessionId, volume));
    }

    @Override
    public boolean OnSetRepeatMode(int sessionId, int repeatMode) {
        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onSetPlayMode(sessionId, repeatMode);
        }
        return true;
    }

    @Override
    public boolean OnPlaylistAddAlbum(int sessionId, XWeiMediaInfo[] mediaInfoArray) {
        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onPlaylistAddAlbum(sessionId, mediaInfoArray[0]);
        }
        return true;
    }

    @Override
    public boolean OnPlaylistAddItem(int sessionId, boolean isFront, XWeiMediaInfo[] mediaInfoArray) {
        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onPlaylistAddItem(sessionId, isFront, mediaInfoArray);
        }
        return true;
    }

    @Override
    public boolean OnPlaylistUpdateItem(int sessionId, XWeiMediaInfo[] mediaInfoArray) {
        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onPlaylistUpdateItem(sessionId, mediaInfoArray);
        }
        return true;
    }

    @Override
    public boolean OnPlaylistRemoveItem(int sessionId, XWeiMediaInfo[] mediaInfoArray) {
        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onPlayListRemoveItem(sessionId, mediaInfoArray);
        }
        return true;
    }

    @Override
    public boolean OnPushMedia(int sessionId, XWeiMediaInfo mediaInfo, boolean needReleaseRes) {
        IXWeiPlayer player = getPlayer(sessionId);
        boolean handled = (player != null && player.playMediaInfo(sessionId, mediaInfo, needReleaseRes));

        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onPlay(sessionId, mediaInfo, false);
        }

        return handled;
    }

    @Override
    public boolean OnFavoriteEvent(String event, String playId) {
        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onFavoriteEvent(event, playId);
        }
        return true;
    }

    @Override
    public boolean OnSupplement(int sessionId, String contextId, int speakTimeout, int silentTimeout, long requestParam) {
        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onAutoWakeup(sessionId, contextId, speakTimeout, silentTimeout, requestParam);
        }
        return true;
    }

    @Override
    public void OnTips(int sessionId, int tipsType) {
        if (mSkillUIEventListener != null) {
            mSkillUIEventListener.onTips(tipsType);
        }

    }

    @Override
    public void onNeedReportPlayState(int sessionId, XWeiPlayState state) {
        IXWeiPlayer player = getPlayer(sessionId);
        if (player != null) {
            player.onNeedReportPlayState(sessionId, state);
        }
    }

    @Override
    public void onAudioMsgRecord(int sessionId) {
        //MAJOR TODO    XWeiMsgTransfer.getInstance().onAudioMsgRecord();
    }

    @Override
    public void onAudioMsgSend(int sessionId, long tinyId) {
        //MAJOR TODO        XWeiMsgTransfer.getInstance().onAudioMsgSend(tinyId);
    }

    public void onDownloadMsgFile(int sessionId, long tinyId, int channel, int type, String key1,
                                  String key2, int duration, int timestamp) {
        //控制层通知消息文件到来的信息，由app控制是否下载
        //MAJOR TODO    XWeiMsgTransfer.getInstance().onDownloadMsgFile(sessionId, tinyId, channel, type, key1, key2, duration, timestamp);
    }

    private void notifyControlEvent(String action, Bundle extra) {
        Intent intent = new Intent(action);
        if (extra != null) {
            intent.putExtras(extra);
        }

        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }


    /**
     * Skill场景UI事件监听
     */
    public interface SkillUIEventListener {
        /**
         * 播放列表封面信息
         *
         * @param sessionId 场景sessionId
         * @param mediaInfo 新增播放列表
         */
        void onPlaylistAddAlbum(int sessionId, XWeiMediaInfo mediaInfo);

        /**
         * 播放列表新增资源
         *
         * @param sessionId      场景sessionId
         * @param mediaInfoArray 新增播放资源
         */
        void onPlaylistAddItem(int sessionId, boolean isFront, XWeiMediaInfo[] mediaInfoArray);

        /**
         * 播放列表资源信息更新
         *
         * @param sessionId      场景sessionId
         * @param mediaInfoArray 更新的播放资源
         */
        void onPlaylistUpdateItem(int sessionId, XWeiMediaInfo[] mediaInfoArray);

        /**
         * 播放列表删减资源
         *
         * @param sessionId      场景sessionId
         * @param mediaInfoArray 新增播放资源
         */
        void onPlayListRemoveItem(int sessionId, XWeiMediaInfo[] mediaInfoArray);

        /**
         * 开始播放一首歌
         *
         * @param sessionId 场景sessionId
         * @param mediaInfo 播放列表中的媒体信息
         * @param fromUser  true 表示为用户主动切歌，如果界面被盖住了，需要重新展示界面
         */
        void onPlay(int sessionId, XWeiMediaInfo mediaInfo, boolean fromUser);

        /**
         * 歌曲暂停播放
         *
         * @param sessionId 场景sessionId
         */
        void onPause(int sessionId);

        /**
         * 歌曲恢复播放
         *
         * @param sessionId 场景sessionId
         */
        void onResume(int sessionId);

        /**
         * 设置了播放模式
         *
         * @param sessionId  场景sessionId
         * @param repeatMode {@linkplain Constants.RepeatMode }
         */
        void onSetPlayMode(int sessionId, int repeatMode);

        /**
         * 列表的所有资源播放完毕了
         *
         * @param sessionId 场景sessionId
         */
        void onFinish(int sessionId);

        /**
         * 歌曲收藏或取消收藏事件
         *
         * @param event  "收藏"或"取消收藏"
         * @param playId 播放资源ID
         */
        void onFavoriteEvent(String event, String playId);

        /**
         * 播放列表操作提示
         *
         * @param tipsType 提示类型 {@link Constants.TXPlayerTipsType}
         */
        void onTips(int tipsType);

        /**
         * 设备应该自动唤醒
         *
         * @param sessionId
         * @param contextId
         * @param speakTimeout
         * @param silentTimeout
         */
        void onAutoWakeup(int sessionId, String contextId, int speakTimeout, int silentTimeout, long requestParam);
    }

    // szjy begin
    private boolean isAllQPlayersPlaying() {
        for (IXWeiPlayer player : mQPlayers) {
            QAILog.d(player.toString());
        }
        for (IXWeiPlayer player : mQPlayers) {
            if (!player.isPlaying())
                return false;
        }
        return true;
    }

    private IXWeiPlayer getIdlePlayer() {
        for (IXWeiPlayer player : mQPlayers) {
            QAILog.d(player.toString());
        }
        for (IXWeiPlayer player : mQPlayers) {
            if (!player.isPlaying())
                return player;
        }
        return null;
    }

    @Override
    public boolean playTTS(XWeiMediaInfo mediaInfo, boolean needReleaseRes, ITTSCallback cb) {
        IXWeiPlayer playingPlayer = null;
        IXWeiPlayer idlePlayer = getIdlePlayer();
        if (isAllQPlayersPlaying() || idlePlayer == null) {
            Log.e(TAG, "OnPushMedia: no player resources");
            // TODO new a temp player to play and return.
            if (cb != null) {
                try {
                    cb.onTTSPlayError(mediaInfo.resId, -100, "no player resources");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        // get playing player
        for (IXWeiPlayer player : mQPlayers) {
            if (player.isPlaying()) {
                playingPlayer = player;
                break;
            }
        }

        if (playingPlayer != null && playingPlayer.isPlaying()) {
            playingPlayer.stop(0);
            //playingPlayer.stopAndSend();
            //and send out tts_end event
        }

        //if (QAIAudioFocusMgr.getInst().getCurrentFocus() < 0)
        {
            int result = QAIAudioFocusMgr.getInst().requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                QAILog.d(TAG, "AUDIOFOCUS_REQUEST_GRANTED");
                // try to play the tts
                idlePlayer.setParam(IXW_PARAM_TYPE_TTS_CB, cb);
                idlePlayer.playMediaInfo(0, mediaInfo, needReleaseRes);
            } else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                QAILog.d(TAG, "AUDIOFOCUS_REQUEST_FAILED");

                if (cb != null) {
                    try {
                        context.sendBroadcast(QAIUtils.getTTSStopIntent());
                        QAILog.d(TAG, "notifyEvent: XW_PLAYER_EVT_TTS_END");

                        cb.onTTSPlayEnd(mediaInfo.resId);
                        cb.onTTSPlayError(mediaInfo.resId, -99, "");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    private final AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            QAILog.d(TAG, "ChangeListener:" + focusChange);

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                QAILog.d(TAG, "ChangeListener: AUDIOFOCUS_LOSS:" + focusChange);

                // get playing player
                IXWeiPlayer playingPlayer = null;
                for (IXWeiPlayer player : mQPlayers) {
                    if (player.isPlaying()) {
                        playingPlayer = player;
                        break;
                    }
                }
                if (playingPlayer != null) {
                    playingPlayer.stop(0);
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                QAILog.d(TAG, "ChangeListener: AUDIOFOCUS_GAIN");

            }
        }
    };

    private class XWPlayerNotify implements XWeiPlayer.IXWeiPlayerNotify {
        @Override
        public void notifyEvent(int event, int param1, int param2, String param3, Object paramObj) {
            switch (event) {
                case XW_PLAYER_EVT_TTS_END:
                    context.sendBroadcast(QAIUtils.getTTSStopIntent());
                    QAILog.d(TAG, "notifyEvent: XW_PLAYER_EVT_TTS_END");
                    QAIAudioFocusMgr.getInst().abandonAudioFocus(mAudioFocusChangeListener);
                    break;
                case XW_PLAYER_EVT_TTS_START:
                    context.sendBroadcast(QAIUtils.getTTSStartIntent());
                    QAILog.d(TAG, "notifyEvent: XW_PLAYER_EVT_TTS_START");
                    break;
                default:
                    break;
            }
        }
    }
// szjy end

}
