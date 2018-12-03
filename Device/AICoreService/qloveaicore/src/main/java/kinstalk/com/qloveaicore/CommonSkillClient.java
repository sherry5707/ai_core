package kinstalk.com.qloveaicore;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.kinstalk.her.voip.activity.MainActivity;
import com.kinstalk.her.voip.activity.RecordsActivity;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWResponseInfo;
import com.tencent.xiaowei.util.QLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kinstalk.com.common.utils.CountlyEvents;
import kinstalk.com.common.utils.FileUtils;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.qloveaicore.qlovenlp.model.ModelLauncherSkill;

public class CommonSkillClient {
    public static final String TAG = "CommonSkillClient";

    public static final String INTENT_NAME_ADVICE = "advice";
    public static final String INTENT_NAME_CONTACTS = "openContacts";
    public static final String INTENT_NAME_CALL_LOG = "openCallLog";
    public static final String INTENT_NAME_TIMER = "timer";
    public static final String INTENT_NAME_SCHEDULE = "schedule";
    public static final String INTENT_NAME_QINJIAN = "playQinjian";
    public static final String INTENT_NAME_DIANSHIJIA = "openDianshijia";
    public static final String INTENT_NAME_KIDS_VIDEO = "OpenKidsVideo";
    public static final String INTENT_NAME_OPEN_HEALTH = "OpenHealth";
    public static final String INTENT_NAME_CLOSE_COLOURLIFE = "CloseColourlife";
    public static final String INTENT_NAME_OPEN_COLOURLIFE = "OpenColourlife";
    public static final String INTENT_NAME_OPEN_COLOURLIFE_COMPLAINTS = "OpenPropertyComplaints";
    public static final String INTENT_NAME_OPEN_COLOURLIFE_NOTICE = "OpenPropertyNotice";
    public static final String INTENT_NAME_OPEN_COLOURLIFE_REPAIR = "OpenPropertyRepair";
    public static final String INTENT_NAME_OPEN_HONGEN = "HongEnSketch_Open";
    public static final String INTENT_NAME_CLOSE_HONGEN = "HongEnSketch_Close";

    public static final String INTENT_CheckBloodGlucose = "CheckBloodGlucose";
    public static final String INTENT_CheckBloodGlucoseReport = "CheckBloodGlucoseReport";
    public static final String INTENT_CheckBloodPressure = "CheckBloodPressure";
    public static final String INTENT_CheckBloodPressureReport = "CheckBloodPressureReport";
    public static final String INTENT_CheckHealthFile = "CheckHealthFile";
    public static final String INTENT_ContactXixinHealth = "ContactXixinHealth";
    public static final String INTENT_MeasureBloodGlucose = "MeasureBloodGlucose";
    public static final String INTENT_MeasureBloodPressure = "MeasureBloodPressure";
    public static final String INTENT_OpenXinxinHealth = "OpenXinxinHealth";
    public static final String INTENT_RecordBloodGlucose = "RecordBloodGlucose";
    public static final String INTENT_RecordBloodPressure = "RecordBloodPressure";


    public static final String PACKAGE_NAME_LINKS = "com.kinstalk.links";
    public static final String CLASS_NAME_ADVICE_ACTIVITY = "com.kinstalk.links.LinksMainActivity";

    public static final String BASE_URL_KIDS_VIDEO = "qqlivekid://v.qq.com/JumpAction?";

    private static final String HEALTH_TARGET_APP_HEALTH = "Health";
    private static final String HEALTH_TARGET_APP_HEALTH_PINGAN = "HealthPingan";
    private static final String HEALTH_TARGET_APP_HEALTH_HEHUAN = "HealthHehuan";

    private static final String HEALTH_APP_PACKAGE = "com.kinkstalk.her.qinjianhealth";
    private static final String HEALTH_APP_MAIN_ACTIVITY = "com.kinkstalk.her.qinjianhealth.MainActivity";
    private static final String HEALTH_ACTION_TARGET_EXTRA = "target_extra";
    private static final String HEALTH_ACTION_SUBPARAM_EXTRA = "subparam_extra";
    private static final String HEALTH_PACKAGE_NAME_PINGAN = "com.pajk.stabletqj";
    private static final String HEALTH_CLASS_NAME_PINGAN_ACTIVITY = "com.pajk.stabletqj.MainActivity";

    private static final String COLOURLIFE_PACKAGE_NAME = "cn.net.colourlife";
    private static final String COLOURLIFE_OPEN_Main = "cn.net.colourlife.activity.MainActivity";
    private static final String COLOURLIFE_OPEN_COMPLAIN = "cn.net.colourlife.activity.ComplainActivity";
    private static final String COLOURLIFE_OPEN_NOTICE = "cn.net.colourlife.activity.NoticeActivity";
    private static final String COLOURLIFE_OPEN_REPAIR = "cn.net.colourlife.activity.RepairActivity";
    private Context mContext;
    private String jsonStr = "";
    private String appName ;
    private String responseData ;
    private String intentName ;
    public CommonSkillClient(Context context){
        this.mContext = context;
        jsonStr = FileUtils.getJsonFileStr(context,"skill.json");
    }

    public boolean handleLocalSkillData(XWResponseInfo rspData){
        try {
            appName = rspData.appInfo.name;
            responseData = rspData.responseData;
            JSONObject responseObject = new JSONObject(responseData);
            intentName = responseObject.optString("intentName");
            QLog.d(TAG,"intentName ="+intentName);
            if (launcherCmd(intentName)) {
                return true;
            }
            if (jsonStr.equals("")) {
                return false;
            }
            JsonParser parser = new JsonParser();
            JsonObject rootObject = parser.parse(jsonStr).getAsJsonObject();
            JsonElement versionElement = rootObject.get("v");
            String version = versionElement.toString();
            JsonElement skillElement = rootObject.get("d");

            Type listType = new TypeToken<LinkedList<CommonSkillBean.SkillModel>>(){}.getType();
            Gson gson = new Gson();
            LinkedList<CommonSkillBean.SkillModel> skills = gson.fromJson(skillElement.toString(), listType);
            QLog.d("CommonSkillClient"," skills.size()"+skills.size());
            if(skills.size()==0){
                return false;

            }else{
                for(int i=0;i<skills.size();i++){
                    if (intentName.equals(skills.get(i).getIntentName())){

                        QAICoreService.getInstance().mControl.playTextWithStr(skills.get(i).getPlay_text(),null);
                        return true;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
    private boolean launcherCmd(String intentName){
        if (intentName == null) {
            CountlyEvents.voiceRecognitionNoSkillMatch(appName);
            return false;
        }
        if (intentName.startsWith(INTENT_NAME_OPEN_HEALTH)) {
            QAILog.d(TAG, "tryLaunchSkillApp: launch health");
            dispatchHealthApp(responseData);
        }
        switch (intentName) {
            case INTENT_NAME_ADVICE:
                QAILog.d(TAG, "tryLaunchSkillApp: launch Advice");
                openAdvicePage();
                return true;
            case INTENT_NAME_CONTACTS:
                QAILog.d(TAG, "tryLaunchSkillApp: launch Contacts");
                MainActivity.actionStart(QAIApplication.getInstance());
                return true;
            case INTENT_NAME_CALL_LOG:
                QAILog.d(TAG, "tryLaunchSkillApp: launch CallLog");
                RecordsActivity.actionStart(QAIApplication.getInstance(), true);
                 return true;
            case INTENT_NAME_KIDS_VIDEO:
                QAILog.d(TAG, "tryLaunchSkillApp: launch KidsVideo");
                openKidsVideo();
                return true;
            case INTENT_NAME_DIANSHIJIA:
                return true;
            case INTENT_NAME_CLOSE_COLOURLIFE:
            case INTENT_NAME_OPEN_COLOURLIFE:
            case INTENT_NAME_OPEN_COLOURLIFE_COMPLAINTS:
            case INTENT_NAME_OPEN_COLOURLIFE_NOTICE:
            case INTENT_NAME_OPEN_COLOURLIFE_REPAIR:
                openColourLifeApp(intentName);
                return true;
            case INTENT_NAME_OPEN_HONGEN:
                openHongEnPackage();
                return true;
            case INTENT_NAME_CLOSE_HONGEN:
                closeHongEnPackage();
                return true;
            case INTENT_CheckHealthFile:
            case INTENT_ContactXixinHealth:
            case INTENT_MeasureBloodGlucose:
            case INTENT_MeasureBloodPressure:
            case INTENT_OpenXinxinHealth:
            case INTENT_CheckBloodGlucose:
            case INTENT_CheckBloodGlucoseReport:
            case INTENT_CheckBloodPressure:
            case INTENT_CheckBloodPressureReport:
            case INTENT_RecordBloodGlucose:
            case INTENT_RecordBloodPressure:
                openXiXinHealth(responseData, intentName);
                return true;
            default:
                CountlyEvents.voiceRecognitionNoSkillMatch(appName);
                return false;
        }
    }
    //打开洪恩绘本
    private static void openHongEnPackage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.ihuman.book", "com.ihuman.book.AppActivity");
        try {
            intent.setComponent(cn);
            QAIApplication.getInstance().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭洪恩绘本
    private static void closeHongEnPackage() {
        //TODO
    }

    private static void dispatchHealthApp(String responseData) {
        ModelLauncherSkill skill = ModelLauncherSkill.optSkillData(responseData, null, null);
        if (skill.semantic == null || skill.semantic.slots == null || skill.semantic.slots.target == null) {
            return;
        }
        switch (skill.semantic.slots.target) {
            case HEALTH_TARGET_APP_HEALTH:
            case HEALTH_TARGET_APP_HEALTH_HEHUAN:
                openQinjianHealthApp(skill);
                break;
            case HEALTH_TARGET_APP_HEALTH_PINGAN:
                openPinganHealthApp();
                break;
        }
    }

    private static void openQinjianHealthApp(ModelLauncherSkill skill) {
        String target = skill.semantic.slots.target;
        String person = skill.semantic.slots.param != null ? skill.semantic.slots.param.person : null;
        try {
            Intent intent = new Intent();
            intent.setPackage(HEALTH_APP_PACKAGE);
            intent.setClassName(HEALTH_APP_PACKAGE, HEALTH_APP_MAIN_ACTIVITY);
            intent.putExtra(HEALTH_ACTION_TARGET_EXTRA, target);
            intent.putExtra(HEALTH_ACTION_SUBPARAM_EXTRA, person);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QAIApplication.getInstance().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openPinganHealthApp() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName(HEALTH_PACKAGE_NAME_PINGAN,
                    HEALTH_CLASS_NAME_PINGAN_ACTIVITY));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QAIApplication.getInstance().startActivity(intent);
        } catch (ActivityNotFoundException ane) {
            showAppNotFound("平安好医生");
        }
    }

    private static void openKidsVideo() {
        String url = BASE_URL_KIDS_VIDEO + "cht=1&chid=100186&jump_source=QROBOT";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QAIApplication.getInstance().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openAdvicePage() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName(PACKAGE_NAME_LINKS,
                    CLASS_NAME_ADVICE_ACTIVITY));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QAIApplication.getInstance().startActivity(intent);
        } catch (ActivityNotFoundException ane) {
            showAppNotFound("我要吐槽");
        }
    }

    private static void openXiXinHealth(String responseData, String intentName) {

        try {
            JSONObject responseObject = new JSONObject(responseData);
            JSONArray ja = responseObject.optJSONArray("slots");
            switch (intentName) {
                case INTENT_CheckHealthFile:
                case INTENT_ContactXixinHealth:
                case INTENT_MeasureBloodGlucose:
                case INTENT_MeasureBloodPressure:
                case INTENT_OpenXinxinHealth:
                    QAILog.i(TAG, "无槽位");
                    break;
                case INTENT_CheckBloodGlucose:
                case INTENT_CheckBloodGlucoseReport:
                case INTENT_CheckBloodPressure:
                case INTENT_CheckBloodPressureReport:
                    String time = ja.optJSONObject(0).optString("time");
                    QAILog.i(TAG, "time = "+time);
                    break;
                case INTENT_RecordBloodGlucose:
                    String time2 = ja.optJSONObject(1).optString("time");
                    String timePoint = ja.optJSONObject(2).optString("timePoint");
                    String bloodGlucose = ja.optJSONObject(0).optString("bloodGlucose");
                    QAILog.i(TAG, "time = "+time2+"   timePoint = "+timePoint+"   bloodGlucose = "+bloodGlucose);
                    break;
                case INTENT_RecordBloodPressure:
                    String time3 = ja.optJSONObject(4).optString("time");
                    String diastolicPressure = ja.optJSONObject(0).optString("diastolicPressure");
                    String systolicPressure = ja.optJSONObject(3).optString("systolicPressure");
                    String heartRate = ja.optJSONObject(1).optString("heartRate");
                    String pulseRate = ja.optJSONObject(2).optString("pulseRate");
                    QAILog.i(TAG, "time = "+time3+"   diastolicPressure = "+diastolicPressure+"   systolicPressure = "+systolicPressure+
                            "   heartRate = "+heartRate+"   pulseRate = "+pulseRate);
                    break;
            }

        } catch (ActivityNotFoundException ane) {
            showAppNotFound("熙心健康");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void openColourLifeApp(String type) {
        QAILog.i(TAG, "openColourLifeApp: " + "type = " + type);
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            ComponentName componentName = null;
            switch (type) {
                case INTENT_NAME_CLOSE_COLOURLIFE:
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    break;
                case INTENT_NAME_OPEN_COLOURLIFE:
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    componentName = new ComponentName(COLOURLIFE_PACKAGE_NAME, COLOURLIFE_OPEN_Main);
                    break;
                case INTENT_NAME_OPEN_COLOURLIFE_COMPLAINTS:
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    componentName = new ComponentName(COLOURLIFE_PACKAGE_NAME, COLOURLIFE_OPEN_COMPLAIN);
                    break;
                case INTENT_NAME_OPEN_COLOURLIFE_NOTICE:
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    componentName = new ComponentName(COLOURLIFE_PACKAGE_NAME, COLOURLIFE_OPEN_NOTICE);
                    break;
                case INTENT_NAME_OPEN_COLOURLIFE_REPAIR:
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    componentName = new ComponentName(COLOURLIFE_PACKAGE_NAME, COLOURLIFE_OPEN_REPAIR);
                    break;
            }
            if (componentName != null)
                intent.setComponent(componentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            QAIApplication.getInstance().startActivity(intent);
        } catch (ActivityNotFoundException ane) {
            showAppNotFound("彩之云");
        }
    }

    private static void showAppNotFound(String appName) {
        String tips = "您未安装“ " + appName + " ”应用";
        Toast.makeText(QAIApplication.getInstance(), tips, Toast.LENGTH_SHORT).show();
//        TXAIAudioSDK.getInstance().requestTTS(tips);
    }
}
