package kinstalk.com.qloveaicore.qlovenlp.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.common.utils.QAILog;

public class ModelFittimeSkill {

    //    {
//        "engine": "advtech",
//            "code": 0,
//            "semantic": {
//        "slots": {
//            "action": "OPEN",
//                    "target": "AbdominalMusclesTraining"
//        }
//    },
//        "service": "fittime",
//            "playtts": 1,
//            "text": "我要练马甲线",
//            "answer": {
//        "text": "激活背部，纠正驼背",
//                "type": "T"
//    },
//        "voiceID": "RASBYPGAUSFRPWLUQFGLLPFDIRXCYOSG"
//    }
//
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
        public String teacher;
    }

    public static class ModelSkillAnswer {
        public String text;
        public String type;
    }

    public static String getSkillDataJson(String responseData, String requestText, String voiceID) {
        Gson gson = new GsonBuilder().create();

        String result = gson.toJson(optSkillData(responseData, requestText, voiceID));

        QAILog.i("ModelFittimeSkill", "getSkillDataJson: " + result);
        return result;
    }

    public static ModelFittimeSkill optSkillData(String responseData, String requestText, String voiceID) {
        ModelFittimeSkill skill = new ModelFittimeSkill();
        skill.engine = "advtech";
        skill.code = "0";
        skill.service = "fittime";
        skill.playtts = 1;
        skill.text = requestText;
        skill.voiceID = voiceID;

        skill.answer = new ModelSkillAnswer();
        try {
            JSONObject jsonObj = new JSONObject(responseData);
            String intentName = jsonObj.optString("intentName");

            skill.semantic = new ModelSkillSemantic();
            skill.semantic.slots = new ModelSkillSlots();

            if (TextUtils.equals("OpenFittime", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "Fittime";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("CloseFittime", intentName)) {
                skill.semantic.slots.action = "CLOSE";
                skill.semantic.slots.target = "Fittime";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("Training", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "Training";

                skill.answer.text = "减脂入门训练，全身激活训练";
                skill.answer.type = "T";
            } else if (TextUtils.equals("GeneralStretchTraining", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "GeneralStretchTraining";

                skill.answer.text = "普拉提高级训练，腹肌形成";
                skill.answer.type = "T";
            } else if (TextUtils.equals("SelectedTraining", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "SelectedTraining";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("TrainingStarting", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "TrainingStarting";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("LoseWeight", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "LoseWeight";

                skill.answer.text = "好的";
                skill.answer.type = "T";
            } else if (TextUtils.equals("AbdominalMusclesTraining", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "AbdominalMusclesTraining";

                skill.answer.text = "激活背部，纠正驼背";
                skill.answer.type = "T";
            } else if (TextUtils.equals("DecompressionMouldingTraining", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "DecompressionMouldingTraining";

                skill.answer.text = "提神练习";
                skill.answer.type = "T";
            } else if (TextUtils.equals("TeacherCourse", intentName)) {
                skill.semantic.slots.action = "OPEN";
                skill.semantic.slots.target = "TeacherCourse";

                skill.answer.text = "好的";
                if (jsonObj.has("slots")) {
                    JSONArray jsonArray = jsonObj.getJSONArray("slots");
                    if (null != jsonArray && jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String teacher = jsonObject.optString("teacher");
                        skill.semantic.slots.param = new ModelSkillSlotsParam();
                        skill.semantic.slots.param.teacher = teacher;

                        switch (skill.semantic.slots.param.teacher) {
                            case "陈思宇":
                                skill.answer.text = "陈思宇老师的蜜臀美腿燃脂训练";
                                break;
                            case "安迪":
                                skill.answer.text = "安迪老师的基础体能训练";
                                break;
                            case "杨":
                                skill.answer.text = "杨老师的FitTime瑜伽课堂";
                                break;
                            case "璐璐":
                                skill.answer.text = "璐璐老师的马甲线养成计划";
                                break;
                            case "郑蓓":
                                skill.answer.text = "郑蓓老师的普拉提线条雕刻初级";
                                break;
                            case "迈克":
                                skill.answer.text = "迈克老师的四分钟家庭健身";
                                break;
                            case "弗莱维娅":
                                skill.answer.text = "弗莱维娅老师的Hello Venus";
                                break;
                            default:
                                skill.answer.text = "好的";
                                break;
                        }
                    }
                }

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
