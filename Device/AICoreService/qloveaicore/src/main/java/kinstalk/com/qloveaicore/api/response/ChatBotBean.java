/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package kinstalk.com.qloveaicore.api.response;

import java.io.Serializable;

/**
 * Created by Knight.Xu on 2017/3/22.
 */

public class ChatBotBean implements Serializable {

    /**
     * service : chat
     * text : 世界最高峰是什么峰
     * advtech : {"audiourl":"/static/cache/D6XA9U.mp3","webdesc":"珠穆朗玛峰简称珠峰,又意译作圣母峰,尼泊尔称为萨加马塔峰,也叫埃非勒斯峰\u201dEverest,位于中华人民共和国和尼泊尔交界的喜马拉雅山脉之上,终年积雪。","weburl":"/show/talk3d/WBTIKG6712/65QL2Y.mp3","showavatar":"on"}
     * rc : 0
     * answer : {"text":"珠穆朗玛峰简称珠峰,又意译作圣母峰,尼泊尔称为萨加马塔峰,也叫埃非勒斯峰\u201dEverest,位于中华人民共和国和尼泊尔交界的喜马拉雅山脉之上,终年积雪。","type":"T"}
     * operation : ANSWER
     * soundPickup : off
     * "semantic": {
     * -"slots": {
     * "attrValue": "关",
     * "attrType": "String",
     * -"location": {
     * "type": "LOC_HOUSE",
     * "room": "卧室"
     * },
     * "attr": "开关"
     * }
     * }
     */

    private String service;
    private String text;
    private AdvtechBean advtech;
    private int rc;
    private AnswerBean answer;
    private String operation;
    private String soundPickup;
    private Object semantic;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AdvtechBean getAdvtech() {
        return advtech;
    }

    public void setAdvtech(AdvtechBean advtech) {
        this.advtech = advtech;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public AnswerBean getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerBean answer) {
        this.answer = answer;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSoundPickup() {
        return soundPickup;
    }

    public void setSoundPickup(String soundPickup) {
        this.soundPickup = soundPickup;
    }

    public Object getSemantic() {
        return semantic;
    }

    public void setSemantic(Object semantic) {
        this.semantic = semantic;
    }

    public static class AdvtechBean {
        /**
         * audiourl : /static/cache/D6XA9U.mp3
         * webdesc : 珠穆朗玛峰简称珠峰,又意译作圣母峰,尼泊尔称为萨加马塔峰,也叫埃非勒斯峰”Everest,位于中华人民共和国和尼泊尔交界的喜马拉雅山脉之上,终年积雪。
         * weburl : /show/talk3d/WBTIKG6712/65QL2Y.mp3
         * showavatar : on
         */

        private String audiourl;
        private String webdesc;
        private String weburl;
        private String showavatar;
        private String videoid;

        public String getAudiourl() {
            return audiourl;
        }

        public void setAudiourl(String audiourl) {
            this.audiourl = audiourl;
        }

        public String getWebdesc() {
            return webdesc;
        }

        public void setWebdesc(String webdesc) {
            this.webdesc = webdesc;
        }

        public String getWeburl() {
            return weburl;
        }

        public void setWeburl(String weburl) {
            this.weburl = weburl;
        }

        public String getShowavatar() {
            return showavatar;
        }

        public void setShowavatar(String showavatar) {
            this.showavatar = showavatar;
        }

        public String getVideoid() {
            return videoid;
        }

        public void setVideoid(String videoid) {
            this.videoid = videoid;
        }
    }

    public static class AnswerBean {
        /**
         * text : 珠穆朗玛峰简称珠峰,又意译作圣母峰,尼泊尔称为萨加马塔峰,也叫埃非勒斯峰”Everest,位于中华人民共和国和尼泊尔交界的喜马拉雅山脉之上,终年积雪。
         * type : T
         */

        private String text;
        private String type;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
