package kinstalk.com.qloveaicore.qlovenlp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.common.utils.QAILog;

public class ModelXiXinHealthSkill {
    /**
     * confirmStatus : NONE
     * dialogState : COMPLETED
     * intentName : RecordBloodPressure
     * slots : [{"confirmStatus":"NONE","diastolicPressure":"100","key":"diastolicPressure","value":"100"},{"confirmStatus":"NONE","heartRate":"","key":"heartRate","value":""},{"confirmStatus":"NONE","key":"pulseRate","pulseRate":"","value":""},{"confirmStatus":"NONE","key":"systolicPressure","systolicPressure":"60","value":"60"},{"confirmStatus":"NONE","key":"time","time":"","value":""}]
     */

    public String engine;

    public String code;

    public ModelSkillSemantic semantic;

    public String service;

    public int playtts;

    public String text;

    public String voiceID;

    public static class ModelSkillSemantic {
        public ModelSkillSlots slots;
        public String intentName;
    }

    public static class ModelSkillSlots {
        public String diastolicPressure;
        public String heartRate;
        public String pulseRate;
        public String systolicPressure;
        public String time;
        public String timePoint;
        public String bloodGlucose;
    }

    public static String getSkillDataJson(String responseData, String requestText, String voiceID) {
        Gson gson = new GsonBuilder().create();

        String result = gson.toJson(optSkillData(responseData, requestText, voiceID));

        QAILog.i("ModelXiXinHealthSkill", "getSkillDataJson: " + result);
        return result;
    }

    public static ModelXiXinHealthSkill optSkillData(String responseData, String requestText, String voiceID) {
        ModelXiXinHealthSkill skill = new ModelXiXinHealthSkill();
        skill.engine = "qlove_nlp";
        skill.code = "0";
        skill.service = "ikeeper";
        skill.playtts = 0;
        skill.text = requestText;
        skill.voiceID = voiceID;

        try {
            JSONObject jsonObj = new JSONObject(responseData);
            String intentName = jsonObj.optString("intentName");

            skill.semantic = new ModelSkillSemantic();
            skill.semantic.slots = new ModelSkillSlots();

            JSONArray ja = jsonObj.optJSONArray("slots");

            switch (intentName) {
                case "CheckHealthFile":
                case "ContactXixinHealth":
                case "MeasureBloodGlucose":
                case "MeasureBloodPressure":
                case "OpenXinxinHealth":
                    skill.semantic.intentName = intentName;
                    break;
                case "CheckBloodGlucose":
                case "CheckBloodGlucoseReport":
                case "CheckBloodPressure":
                case "CheckBloodPressureReport":
                    skill.semantic.intentName = intentName;
                    skill.semantic.slots.time = ja.optJSONObject(0).optString("time");
                    break;
                case "RecordBloodGlucose":
                    skill.semantic.intentName = intentName;
                    skill.semantic.slots.time = ja.optJSONObject(1).optString("time");
                    skill.semantic.slots.timePoint = ja.optJSONObject(2).optString("timePoint");
                    skill.semantic.slots.bloodGlucose = ja.optJSONObject(0).optString("bloodGlucose");
                    break;
                case "RecordBloodPressure":
                    skill.semantic.intentName = intentName;
                    skill.semantic.slots.time = ja.optJSONObject(4).optString("time");
                    skill.semantic.slots.diastolicPressure = ja.optJSONObject(0).optString("diastolicPressure");
                    skill.semantic.slots.systolicPressure = ja.optJSONObject(3).optString("systolicPressure");
                    skill.semantic.slots.heartRate = ja.optJSONObject(1).optString("heartRate");
                    skill.semantic.slots.pulseRate = ja.optJSONObject(2).optString("pulseRate");
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return skill;
    }
}
