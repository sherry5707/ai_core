package kinstalk.com.qloveaicore.qlovenlp.model;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.qloveaicore.qlovenlp.utils.Serializable;

public class ModelTimerSkill {
    /**
     * {
     * "engine": "advtech", "code": 0, "service": "timer", "playtts": 1,
     * "text": "\u5012\u8ba1\u65f6\u4e94\u5206\u949f", "duration": "5",
     * "answer": {"text": "\u597d\u7684", "type": "T"},
     * "operation": "START", "voiceID": "WXNAHAEFQJOZCDRKPWJZDHTLDRUYKOOS", "unit": "minute"
     * }
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

    public static ModelTimerSkill getTestObj() {
        ModelTimerSkill timerSkill = new ModelTimerSkill();

        timerSkill.engine = "advtech";
        timerSkill.code = "0";
        timerSkill.service = "timer";
        timerSkill.playtts = 1;
        timerSkill.text = "daojishi5fenzhong";
        timerSkill.duration = "5";
        timerSkill.operation = "START";
        timerSkill.voiceID = "WXNAHAEFQJOZCDRKPWJZDHTLDRUYKOOS";
        timerSkill.unit = "minute";

        return timerSkill;
    }

    public static String getSkillJsonData(String voiceID, String requestText, String responseData) {
        return new GsonBuilder().create().toJson(optSkillData(voiceID, requestText, responseData));
    }

    public static ModelTimerSkill optSkillData(String voiceID, String requestText, String responseData) {
        ModelTimerSkill modelTimerSkill = new ModelTimerSkill();
        modelTimerSkill.code = "0";
        modelTimerSkill.voiceID = voiceID;
        modelTimerSkill.engine = "advtech";
        modelTimerSkill.service = "timer";
        modelTimerSkill.playtts = 1;
        modelTimerSkill.text = requestText;

        try {
            JSONObject responseObject = new JSONObject(responseData);
            String intentName = responseObject.optString("intentName");
            if ("timer".equals(intentName)) {
                modelTimerSkill.operation = "START";
                JSONArray slots = responseObject.optJSONArray("slots");
                if (slots != null && slots.length() > 0) {
                    String num = null;
                    String time = null;
                    String specialTime = null;
                    int length = slots.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject slot = slots.optJSONObject(i);
                        if (slot != null) {
                            String key = slot.optString("key");
                            String value = slot.optString("value");
                            if ("num".equals(key) && !TextUtils.isEmpty(value)) {
                                num = value;
                            } else if ("time".equals(key) && !TextUtils.isEmpty(value)) {
                                if ("秒".equals(value) || "秒钟".equals(value)) {
                                    time = "second";
                                } else if ("分钟".equals(value) || "分".equals(value)) {
                                    time = "minute";
                                } else if ("小时".equals(value)) {
                                    time = "hour";
                                } else if ("半小时".equals(value) || "半个小时".equals(value)) {
                                    Log.e("MOdelTimerSKill", "optSkillData: ", );
                                    modelTimerSkill.duration = "30";
                                    modelTimerSkill.unit = "minute";
                                }
                            } else if ("specialtime".equals(key) && !TextUtils.isEmpty(value)) {
                                specialTime = value;
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(num) && !TextUtils.isEmpty(time)) {
                        modelTimerSkill.duration = num;
                        modelTimerSkill.unit = time;
                    } else {
                        if ("一刻钟".equals(specialTime)) {
                            modelTimerSkill.duration = "15";
                            modelTimerSkill.unit = "minute";
                        } else if ("十分".equals(specialTime)) {
                            modelTimerSkill.duration = "10";
                            modelTimerSkill.unit = "minute";
                        } else if ("半分钟".equals(specialTime)) {
                            modelTimerSkill.duration = "30";
                            modelTimerSkill.unit = "second";
                        } else if ("半小时".equals(specialTime)) {
                            modelTimerSkill.duration = "30";
                            modelTimerSkill.unit = "minute";
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return modelTimerSkill;
    }
}
