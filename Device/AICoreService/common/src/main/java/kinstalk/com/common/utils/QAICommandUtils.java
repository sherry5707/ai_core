package kinstalk.com.common.utils;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import kinstalk.com.qloveaicore.AICoreDef;

/**
 * Created by wangyong on 2018/4/20.
 * All rights reserved by Beijing ShuZiJiaYuan Corp.
 */

public class QAICommandUtils {

    private static final String TAG = "QAICommandUtils";

    public static String getVoiceResponsePacket(int volume) {

        JSONObject o = new JSONObject();
        String rex = "";

        try {
            o.put(AICoreDef.WATER_ANIM_JSON_CMD, AICoreDef.WATER_ANIM_CMD_VOLUME);
            o.put(AICoreDef.WATER_ANIM_JSON_VOLUME, volume);
            rex = o.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rex;
    }

    public static String getAnimationResponsePacket(int cmd, final String text) {

        String responsePacket = "";
        try {
            JSONObject json = TextUtils.isEmpty(text)? new JSONObject(): new JSONObject(text);
            json.put(AICoreDef.WATER_ANIM_JSON_CMD, cmd);
            responsePacket = json.toString();
        } catch (JSONException e) {
            responsePacket = "{\""+AICoreDef.WATER_ANIM_JSON_CMD+"\":"+cmd+"}";
            e.printStackTrace();
        }

        return responsePacket;
    }

}
