package kinstalk.com.qloveaicore.qlovenlp.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.common.utils.QAILog;

public class ModelColourlifeSkill {

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

        public String param;
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

    public static ModelColourlifeSkill optSkillData(String responseData, String requestText, String voiceID) {
        ModelColourlifeSkill skill = new ModelColourlifeSkill();
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
            if (TextUtils.equals("OpenColourlife", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "Colourlife";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("CloseColourlife", intentName)) {
                skill.semantic.slots.action = "CLOSE";
                skill.semantic.slots.target = "Colourlife";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("OpenPropertyComplaints", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "PropertyComplaints";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("OpenPropertyRepair", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "PropertyRepair";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("OpenPropertyNotice", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "PropertyNotice";
                if (jsonObj.has("slots")) {
                    JSONArray jsonArray = jsonObj.getJSONArray("slots");
                    if (null != jsonArray && jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String time = jsonObject.optString("time");
                        skill.semantic.slots.param = time;
                    }
                }

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
