package kinstalk.com.qloveaicore;

import java.io.Serializable;
import java.util.ArrayList;

public class CommonSkillBean implements Serializable {
    //    {
//        "c": 0,
//            "m": "OK",
//            "v": "99",
//            "d": {
//	[
//        {
//            "intentName": "WorldCup_Wiki",
//                "skill-id": "8dab4796-fa37-4114-0002-7637fa2b0001",
//                "model_exclude": "M10",
//                "show_text": "33万人口的冰岛喽，毕竟坐拥十几亿人口的亚洲大国都没进..对，我说的就是印度！",
//                "play_text": "33万人口的冰岛喽，毕竟坐拥十几亿人口的亚洲大国都没进..对，我说的就是印度！",
//                "show_card": {
//            "card_bg" : "www.szjy.com/1.img",
//                    "card_text": "哈哈",
//        }
//        },
//        {
//            "model_include": "M4 M10",
//                "intentName": "WorldCup_BlackHorse",
//                "skill-id": "8dab4796-fa37-4114-0002-7637fa2b0001",
//                "show_text": "小龙虾都去世界杯了，中国队还远吗～～ ",
//                "play_text": "小龙虾都去世界杯了，中国队还远吗～～ ",
//                "show_card": {
//            "card_bg" : "www.szjy.com/1.img",
//                    "card_text": "哈哈",
//        }
//        },
//	]
//    }
//    }
    private int c;
    private String m;
    private String v;//version
    private Data d;

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public Data getD() {
        return d;
    }

    public void setD(Data d) {
        this.d = d;
    }

    private static class Data{
        private ArrayList<SkillModel> skillModels;

        public ArrayList<SkillModel> getSkillModels() {
            return skillModels;
        }

        public void setSkillModels(ArrayList<SkillModel> skillModels) {
            this.skillModels = skillModels;
        }
    }
    public static class SkillModel{
        private String intentName;
        private String skillId;
        private String model_exclude;
        private String model_include;
        private String play_text;
        private ShowCard show_card;

        public String getModel_include() {
            return model_include;
        }

        public void setModel_include(String model_include) {
            this.model_include = model_include;
        }

        public String getIntentName() {
            return intentName;
        }

        public void setIntentName(String intentName) {
            this.intentName = intentName;
        }

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getModel_exclude() {
            return model_exclude;
        }

        public void setModel_exclude(String model_exclude) {
            this.model_exclude = model_exclude;
        }

        public String getPlay_text() {
            return play_text;
        }

        public void setPlay_text(String play_text) {
            this.play_text = play_text;
        }

        public ShowCard getShow_card() {
            return show_card;
        }

        public void setShow_card(ShowCard show_card) {
            this.show_card = show_card;
        }
    }
    private static class ShowCard{
        private String card_bg;
        private String card_text;

        public String getCard_bg() {
            return card_bg;
        }

        public void setCard_bg(String card_bg) {
            this.card_bg = card_bg;
        }
    }
}
