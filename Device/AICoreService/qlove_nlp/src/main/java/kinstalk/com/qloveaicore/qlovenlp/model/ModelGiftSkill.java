package kinstalk.com.qloveaicore.qlovenlp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.common.utils.QAILog;

public class ModelGiftSkill {

    public String engine;
    public int code;
    public String service;
    public int playtts;
    public String text;
    public String voiceID;
    public String intentName;

    public static String getSkillJsonData(String responseData, String requestText, String voiceID) {
        Gson gson = new GsonBuilder().create();

        String result = gson.toJson(optSkillData(responseData, requestText, voiceID));

        QAILog.i("ModelGiftSkill", "getSkillDataJson: " + result);
        return result;
    }

    public static ModelGiftSkill optSkillData(String responseData, String requestText, String voiceID) {
        ModelGiftSkill model = new ModelGiftSkill();
        try {
            JSONObject jsonObj = new JSONObject(responseData);
            String intentName = jsonObj.optString("intentName");
            model.code = 0;
            model.engine = "advtech";
            model.service = "gift";
            model.playtts = 1;
            model.text = requestText;
            model.voiceID = voiceID;
            model.intentName = intentName;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return model;
    }
}
