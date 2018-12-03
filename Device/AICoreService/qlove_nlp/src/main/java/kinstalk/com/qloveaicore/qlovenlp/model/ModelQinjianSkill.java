package kinstalk.com.qloveaicore.qlovenlp.model;

import com.google.gson.GsonBuilder;

public class ModelQinjianSkill {

    /**
     * engine : advtech
     * code : 0
     * service : playQinjian
     * playtts : 1
     * text : 打开帮助指南
     * answer : {"text":"正在打开","type":"T"}
     * operation : OPEN
     * voiceID : QEQBXMRJPHXABMUFNUBROBZLJMNKBZMH
     */

    public String engine;
    public int code;
    public String service;
    public int playtts;
    public String text;
    public AnswerBean answer;
    public String operation;
    public String voiceID;

    public static class AnswerBean {
        /**
         * text : 正在打开
         * type : T
         */

        public String text;
        public String type;
    }

    public static String getSkillJsonData(String responseData, String requestText, String voiceID) {
        return new GsonBuilder().create().toJson(optSkillData(responseData, requestText, voiceID));
    }

    public static ModelQinjianSkill optSkillData(String responseData, String requestText, String voiceID) {
        ModelQinjianSkill model = new ModelQinjianSkill();
        model.code = 0;
        model.engine = "advtech";
        model.service = "playQinjian";
        model.playtts = 1;
        model.text = requestText;
        model.voiceID = voiceID;
        model.operation = "OPEN";
        model.answer = new AnswerBean();
        model.answer.text = "好的";
        model.answer.type = "T";
        return model;
    }
}
