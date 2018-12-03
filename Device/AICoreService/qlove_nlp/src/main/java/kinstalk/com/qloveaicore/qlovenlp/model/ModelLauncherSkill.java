package kinstalk.com.qloveaicore.qlovenlp.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.common.utils.QAILog;

public class ModelLauncherSkill {

//    {
//        "engine": "advtech",
//            "code": 0,
//            "semantic": {
//        "slots": {
//            "action": "OPEN",
//                    "target": "Health"
//        }
//    },
//        "service": "launcher",
//            "playtts": 1,
//            "text": "孩子要看病",
//            "answer": {
//        "text": "好的",
//                "type": "T"
//    },
//        "voiceID": "WIZLFWXNRPYHOGHVWETSEQTPSAZENKQX"
//    }

    public String engine;

    public String code;

    public ModelSkillSemantic semantic;

    public String service;

    public int playtts;

    public String text;

    public ModelSkillAnswer answer;

    public String voiceID;

    public static class ModelSkillSemantic {
        public ModelSkillSlots slots;
    }

    public static class ModelSkillSlots {
        public String action;

        public String target;

        public ModelSkillSlotsParam param;
    }

    public static class ModelSkillSlotsParam {
        public String person;
    }

    public static class ModelSkillAnswer {
        public String text;
        public String type;
    }

    public static String getSkillDataJson(String responseData, String requestText, String voiceID) {
        Gson gson = new GsonBuilder().create();

        String result = gson.toJson(optSkillData(responseData, requestText, voiceID));

        QAILog.i("ModelLauncherSkill", "getSkillDataJson: " + result);
        return result;
    }

    public static ModelLauncherSkill optSkillData(String responseData, String requestText, String voiceID) {
        ModelLauncherSkill skill = new ModelLauncherSkill();
        skill.engine = "advtech";
        skill.code = "0";
        skill.service = "launcher";
        skill.playtts = 1;
        skill.text = requestText;
        skill.voiceID = voiceID;

        skill.answer = new ModelSkillAnswer();
        try {
            JSONObject jsonObj = new JSONObject(responseData);
            String intentName = jsonObj.optString("intentName");

            skill.semantic = new ModelSkillSemantic();
            skill.semantic.slots = new ModelSkillSlots();
            if (TextUtils.equals("OpenHealth", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "Health";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("OpenHealthPingan", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "HealthPingan";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("OpenHealthHehuan", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "HealthHehuan";
                if (jsonObj.has("slots")) {
                    JSONArray jsonArray = jsonObj.getJSONArray("slots");
                    if (null != jsonArray && jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String person = jsonObject.optString("person");
                        skill.semantic.slots.param = new ModelSkillSlotsParam();
                        if (TextUtils.equals("儿童", person)) {
                            skill.semantic.slots.param.person = "children";
                        } else {
                            skill.semantic.slots.param.person = "adult";
                        }
                    }
                }

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("advice", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "Advice";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = intentName;

                skill.answer.text = "好的";
                skill.answer.type = "T";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return skill;
    }
}
