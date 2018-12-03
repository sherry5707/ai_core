package kinstalk.com.qloveaicore.qlovenlp.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.qloveaicore.qlovenlp.utils.Serializable;

public class ModelCameraSkill {
    /**
     * {"engine": "advtech", "code": 0, "service": "camera", "playtts": 1,
     * "text": "\u6253\u5f00\u76f8\u673a", "answer": {"text": "\u597d\u7684", "type": "T"},
     * "operation": "OPEN", "voiceID": "QIOHYQZPBTHLVDUDRJPWWVNNJPOCIHSR"}
     */
    @Serializable(name = "engine")
    public String engine;

    @Serializable(name = "code")
    public String code;

    @Serializable(name = "service")
    public String service;

    @Serializable(name = "playtts")
    public int playtts;

    @Serializable(name = "text")
    public String text;

    @Serializable(name = "duration")
    public String duration;

    @Serializable(name = "operation")
    public String operation;

    @Serializable(name = "voiceID")
    public String voiceID;

    @Serializable(name = "unit")
    public String unit;

    public static ModelCameraSkill getTestObj() {
        ModelCameraSkill timerSkill = new ModelCameraSkill();

        timerSkill.engine = "advtech";
        timerSkill.code = "0";
        timerSkill.service = "camera";
        timerSkill.playtts = 1;
        timerSkill.text = "daojishi5fenzhong";
//        timerSkill.duration = "5";
        timerSkill.operation = "OPEN";
        timerSkill.voiceID = "QIOHYQZPBTHLVDUDRJPWWVNNJPOCIHSR";
//        timerSkill.unit = "minute";

        return timerSkill;
    }

    public static ModelCameraSkill optSkillData(String responseData) {
        ModelCameraSkill cameraSkill = new ModelCameraSkill();
        cameraSkill.engine = "advtech";
        cameraSkill.code = "0";
        cameraSkill.service = "camera";
        cameraSkill.playtts = 1;
        try {
            JSONObject jsonObj = new JSONObject(responseData);
            String intentName = jsonObj.optString("intentName");
            if(TextUtils.equals(intentName, "openCamera")) {
                cameraSkill.operation = "OPEN";
            } else if (TextUtils.equals(intentName, "takePhoto")) {
                cameraSkill.operation = "TAKE";
            } else if (TextUtils.equals(intentName, "viewPhoto")) {
                cameraSkill.operation = "OPEN";
                cameraSkill.service = "gallery";
            } else if (TextUtils.equals(intentName, "recordVideo")) {
                cameraSkill.operation = "RECORD";
                JSONArray slotArr = jsonObj.optJSONArray("slots");
                if(null != slotArr && slotArr.length() > 0) {
                    for(int i=0; i<slotArr.length(); i++) {
                        JSONObject slotObj = slotArr.optJSONObject(i);
                        String key = slotObj.optString("key");
                        String value = slotObj.optString("value");
                        if(TextUtils.equals(key, "time")) {
                            if(TextUtils.equals(value, "分钟")) {
                                cameraSkill.unit = "minute";
                            } else if(TextUtils.equals(key, "秒")){
                                cameraSkill.unit = "second";
                            }
                        } else if(TextUtils.equals(key, "num")) {
                            cameraSkill.duration = value;
                        }
                    }
                }
            } else if(TextUtils.equals(intentName, "closeCamera")) {
                cameraSkill.operation = "CLOSE";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cameraSkill;
    }
}
