package kinstalk.com.qloveaicore;

import android.text.TextUtils;

import com.tencent.xiaowei.info.XWAppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import kinstalk.com.common.utils.QAILog;

import static kinstalk.com.qloveaicore.AICoreDef.AppState;

/**
 * Created by majorxia on 2018/4/26.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

// 通用控制指令管理
public class ControlCommandController {
    private static final String TAG = "ControlCommandController";

    private static ControlCommandController sInst = null;
    final Map<String, PlayAppInfo> appInfoMap = new LinkedHashMap<>();
    final Map<String, PlayAppInfo> playInfoMap = new LinkedHashMap<>();

    public static synchronized ControlCommandController getInstance() {
        if (sInst == null) {
            sInst = new ControlCommandController();
        }
        return sInst;
    }

    public void updateAppState(String service, int state) {
        QAILog.i(TAG, "updateAppState() called with: service = [" + service + "], state = [" + state + "]");

        synchronized (appInfoMap) {
            if (state <= AppState.APP_STATE_ONDESTROY) {
                appInfoMap.remove(service);
                PlayAppInfo appInfo = new PlayAppInfo();
                appInfo.state = state;
                appInfoMap.put(service, appInfo);
            } else {
                playInfoMap.remove(service);
                PlayAppInfo appInfo = new PlayAppInfo();
                appInfo.state = state;
                playInfoMap.put(service, appInfo);
            }
        }
        QAILog.d(TAG, "updateAppState1: " + dumpAppState(appInfoMap));
        QAILog.d(TAG, "updateAppState2: " + dumpAppState(playInfoMap));
    }

    public String getRouteService() {
        Entry<String, PlayAppInfo> entry;
        synchronized (appInfoMap) {
            entry = getAppInfoRoute(appInfoMap);
            if (entry == null) {
                entry = getPlayInfoRoute(playInfoMap);
            }
        }
        if (entry != null) {
            return entry.getKey();
        }
        return null;
    }

    private Entry<String, PlayAppInfo> getAppInfoRoute(Map<String, PlayAppInfo> map) {
        QAILog.d(TAG, "getAppInfoRoute: " + dumpAppState(map));

        ListIterator<Entry<String, PlayAppInfo>> iterator = new ArrayList<>(map.entrySet()).listIterator(map.size());
        while (iterator.hasPrevious()) {
            Map.Entry<String, PlayAppInfo> entry = iterator.previous();
            if (entry.getValue().state == AppState.APP_STATE_ONRESUME) {
                return entry;
            }
        }

        return null;
    }

    private Entry<String, PlayAppInfo> getPlayInfoRoute(Map<String, PlayAppInfo> map) {
        QAILog.d(TAG, "getPlayInfoRoute: " + dumpAppState(map));

        ListIterator<Entry<String, PlayAppInfo>> iterator = new ArrayList<>(map.entrySet()).listIterator(map.size());
        while (iterator.hasPrevious()) {
            Map.Entry<String, PlayAppInfo> entry = iterator.previous();
            if (entry.getValue().state == AppState.PLAY_STATE_PLAY
                    || entry.getValue().state == AppState.PLAY_STATE_PAUSE) {
                return entry;
            }
        }

        return null;
    }

    public String dumpAppState(Map<String, PlayAppInfo> map) {
        StringBuffer sb = new StringBuffer();
        sb.append("AppState: {\n");

        ListIterator<Entry<String, PlayAppInfo>> iterator = new ArrayList<>(map.entrySet()).listIterator(map.size());
        while (iterator.hasPrevious()) {
            Map.Entry<String, PlayAppInfo> entry = iterator.previous();
            sb.append("service:").append(entry.getKey()).append("; ")
                    .append(entry.getValue()).append("\n");
        }
        sb.append("}");

        return sb.toString();
    }

    public static class PlayAppInfo {
        public int state = AppState.APP_STATE_BASE;
        public XWAppInfo appInfo;

        public static String state2String(int state) {
            Map<Integer, String> m = new HashMap<>();
            m.put(AppState.APP_STATE_ONRESUME, "ONRESUME");
            m.put(AppState.APP_STATE_ONPAUSE, "ONPAUSED");
            m.put(AppState.APP_STATE_ONCREATE, "ONCREATED");
            m.put(AppState.APP_STATE_ONDESTROY, "ONDESTROYED");
            m.put(AppState.PLAY_STATE_PLAY, "PLAY");
            m.put(AppState.PLAY_STATE_PAUSE, "PAUSE");

            String ret = m.get(state);
            if (TextUtils.isEmpty(ret)) ret = "Unknown";

            return ret;
        }

        @Override
        public String toString() {
            return "PlayAppInfo{" +
                    "state=" + state +
                    ", playState=" + state2String(state) +
                    ", appInfo=" + appInfo +
                    '}';
        }
    }
}
