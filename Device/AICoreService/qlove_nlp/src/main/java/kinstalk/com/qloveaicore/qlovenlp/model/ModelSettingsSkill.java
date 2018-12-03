package kinstalk.com.qloveaicore.qlovenlp.model;

import android.text.TextUtils;

import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;
import com.tencent.xiaowei.info.XWResponseInfo;

import org.json.JSONException;
import org.json.JSONObject;

import kinstalk.com.qloveaicore.qlovenlp.utils.Serializable;

/**
 * 创建日期：18/5/19  时间：下午5:21
 * 创建人：董万福
 * 功能描述：
 */
public class ModelSettingsSkill {

    /**
     * {
     * "engine": "advtech", "code": 0, "service": "system", "playtts": 0,
     * "semantic": {"slots": {"action": "SET", "target": "HigherBrightness"}},
     * "text": "\u5c4f\u5e55\u8c03\u4eae\u70b9
     * "voiceID": "ACCUBYUVOYNCDTIBLSZVEWIFIJJYRVWT"
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

    @Serializable(name = "voiceID")
    public String voiceID;

    @Serializable(name = "semantic")
    public Semantic semantic;

    @Serializable(name = "answer")
    public Answer answer;

    @Serializable(name = "answer")
    public static class Answer {
        @Serializable(name = "text")
        public String text;

        @Serializable(name = "type")
        public String type;
    }

    @Serializable(name = "semantic")
    public static class Semantic {
        @Serializable(name = "slots")
        public Slots slots;
    }

    @Serializable(name = "slots")
    public static class Slots {

        @Serializable(name = "action")
        public String action;

        @Serializable(name = "target")
        public String target;

        @Serializable(name = "params")
        public Params params;
    }

    @Serializable(name = "params")
    public static class Params {

        @Serializable(name = "volumeType")
        public int volumeType;

        @Serializable(name = "isIncrement")
        public boolean isIncrement;

        @Serializable(name = "value")
        public float value;
    }

    public static ModelSettingsSkill optSkillData(String responseData, String requestText, String voiceId) {
        ModelSettingsSkill settingsSkill = new ModelSettingsSkill();
        settingsSkill.engine = "advtech";
        settingsSkill.code = "0";
        settingsSkill.service = "system";
        settingsSkill.text = requestText;
        settingsSkill.voiceID = voiceId;
        try {
            JSONObject jsonObj = new JSONObject(responseData);
            String intentName = jsonObj.optString("intentName");

            settingsSkill.semantic = new Semantic();
            settingsSkill.answer = new Answer();

            settingsSkill.semantic.slots = new Slots();
            switch (intentName) {
                case "ElectricQuantity":
                    settingsSkill.semantic.slots.action = "CHECK";
                    settingsSkill.semantic.slots.target = "BatteryLevel";
                    settingsSkill.playtts = 0;
                    break;
                case "HigherBrightness":
                    settingsSkill.semantic.slots.action = "SET";
                    settingsSkill.semantic.slots.target = "HigherBrightness";
                    settingsSkill.playtts = 0;
                    break;
                case "LowerBrightness":
                    settingsSkill.semantic.slots.action = "SET";
                    settingsSkill.semantic.slots.target = "LowerBrightness";
                    settingsSkill.playtts = 0;
                    break;
                case "HomeBack":
                    settingsSkill.semantic.slots.action = "SET";
                    settingsSkill.semantic.slots.target = "HomeBack";
                    settingsSkill.playtts = 1;

                    settingsSkill.answer.text = "好的";
                    settingsSkill.answer.type = "T";
                    break;
                case "OffScreen":
                    settingsSkill.semantic.slots.action = "SET";
                    settingsSkill.semantic.slots.target = "OffScreen";
                    settingsSkill.playtts = 1;

                    settingsSkill.answer.text = "好的";
                    settingsSkill.answer.type = "T";
                    break;
                case "PowerOff":
                    settingsSkill.semantic.slots.action = "SET";
                    settingsSkill.semantic.slots.target = "PowerOff";
                    settingsSkill.playtts = 1;

                    settingsSkill.answer.text = "请您长按电源键后点击关机按钮";
                    settingsSkill.answer.type = "T";
                    break;
                case "Restart":
                    settingsSkill.semantic.slots.action = "SET";
                    settingsSkill.semantic.slots.target = "Restart";
                    settingsSkill.playtts = 1;

                    settingsSkill.answer.text = "请您长按电源键后点击重启按钮";
                    settingsSkill.answer.type = "T";
                    break;
                default:
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return settingsSkill;
    }

    public static ModelSettingsSkill optVolumeSkillData(String cmd) {
        ModelSettingsSkill settingsSkill = new ModelSettingsSkill();
        settingsSkill.engine = "advtech";
        settingsSkill.code = "0";
        settingsSkill.service = "system";


        settingsSkill.semantic = new Semantic();
        settingsSkill.semantic.slots = new Slots();

        try {
            JSONObject jsonObj = new JSONObject(cmd);
            String intentName = jsonObj.optString("intentName");

                        settingsSkill.semantic.slots.params = new Params();
                        switch (intentName) {
                            case "700150"://声音最大
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = false;
                                settingsSkill.semantic.slots.params.value = 100;
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "700151"://声音最小
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = false;
                                settingsSkill.semantic.slots.params.value = 0;
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "700101"://声音调到百分之五十
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = false;
                                //settingsSkill.semantic.slots.params.value = Float.valueOf(resourceInfo.content);
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "volUp"://声音大一点
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = true;
                                /*
                                if (!TextUtils.isEmpty(resourceInfo.content)) {
                                    float value = Float.valueOf(resourceInfo.content);
                                    if (value < 1) {
                                        settingsSkill.semantic.slots.params.value = value;
                                    } else {
                                        settingsSkill.semantic.slots.params.value = value * 0.067F;
                                    }
                                } else*/ {
                                    settingsSkill.semantic.slots.params.value = 0.067F;
                                }
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "volDown"://声音小一点
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = true;
                                /*if (!TextUtils.isEmpty(resourceInfo.content)) {
                                    float value = Float.valueOf(resourceInfo.content);
                                    if (value < 1) {
                                        settingsSkill.semantic.slots.params.value = -value;
                                    } else {
                                        settingsSkill.semantic.slots.params.value = -value * 0.067F;
                                    }
                                } else */{
                                    settingsSkill.semantic.slots.params.value = -0.067F;
                                }
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "700128"://静音
                                /*if (!TextUtils.isEmpty(resourceInfo.content)) {
                                    int isSilence = Integer.valueOf(resourceInfo.content);
                                    settingsSkill.semantic.slots.action = isSilence == 0 ? "silence" : "unSilence";
                                    settingsSkill.semantic.slots.target = "StreamVolume";

                                    settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                }*/
                                break;

                        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        settingsSkill.playtts = 0;
        return settingsSkill;

    }
    public static ModelSettingsSkill optVolumeSkillData(XWResponseInfo rspData) {
        ModelSettingsSkill settingsSkill = new ModelSettingsSkill();
        settingsSkill.engine = "advtech";
        settingsSkill.code = "0";
        settingsSkill.service = "system";
        settingsSkill.text = rspData.requestText;
        settingsSkill.voiceID = rspData.voiceID;

        settingsSkill.semantic = new Semantic();
        settingsSkill.semantic.slots = new Slots();

        try {
            XWResGroupInfo[] resources = rspData.resources;
            if (resources != null && resources.length == 1) {
                XWResourceInfo[] resources1 = resources[0].resources;
                if (resources1 != null && resources1.length == 1) {
                    XWResourceInfo resourceInfo = resources1[0];
                    if (resourceInfo.format == 5) {
                        settingsSkill.semantic.slots.params = new Params();
                        switch (resourceInfo.ID) {
                            case "700150"://声音最大
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = false;
                                settingsSkill.semantic.slots.params.value = 100;
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "700151"://声音最小
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = false;
                                settingsSkill.semantic.slots.params.value = 0;
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "700101"://声音调到百分之五十
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = false;
                                settingsSkill.semantic.slots.params.value = Float.valueOf(resourceInfo.content);
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "700001"://声音大一点
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = true;
                                if (!TextUtils.isEmpty(resourceInfo.content)) {
                                    float value = Float.valueOf(resourceInfo.content);
                                    if (value < 1) {
                                        settingsSkill.semantic.slots.params.value = value;
                                    } else {
                                        settingsSkill.semantic.slots.params.value = value * 0.067F;
                                    }
                                } else {
                                    settingsSkill.semantic.slots.params.value = 0.067F;
                                }
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "700002"://声音小一点
                                settingsSkill.semantic.slots.action = "set";
                                settingsSkill.semantic.slots.target = "StreamVolume";

                                settingsSkill.semantic.slots.params.isIncrement = true;
                                if (!TextUtils.isEmpty(resourceInfo.content)) {
                                    float value = Float.valueOf(resourceInfo.content);
                                    if (value < 1) {
                                        settingsSkill.semantic.slots.params.value = -value;
                                    } else {
                                        settingsSkill.semantic.slots.params.value = -value * 0.067F;
                                    }
                                } else {
                                    settingsSkill.semantic.slots.params.value = -0.067F;
                                }
                                settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                break;
                            case "700128"://静音
                                if (!TextUtils.isEmpty(resourceInfo.content)) {
                                    int isSilence = Integer.valueOf(resourceInfo.content);
                                    settingsSkill.semantic.slots.action = isSilence == 0 ? "silence" : "unSilence";
                                    settingsSkill.semantic.slots.target = "StreamVolume";

                                    settingsSkill.semantic.slots.params.volumeType = Integer.MIN_VALUE;
                                }
                                break;
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        settingsSkill.playtts = 0;
        return settingsSkill;
    }

}
