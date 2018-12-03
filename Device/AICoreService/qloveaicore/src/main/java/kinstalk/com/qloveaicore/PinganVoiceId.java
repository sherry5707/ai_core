package kinstalk.com.qloveaicore;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kinstalk.com.common.utils.QAILog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PinganVoiceId {
    public final static String TAG = "AI-PinganVoiceprint";
    static Method systemPropertie_get = null;

    private File AudioFileRecongnition = new File("/sdcard/temp/audio-wakeup.pcm");
    private FileInputStream AudioInRecongnition = null;

    public void sendAudioRecongnition(final Context context, String responseData) {

        try {
            JSONObject jsonObj = new JSONObject(responseData);
            String intentName = jsonObj.optString("intentName");

            switch (intentName) {
                case "CheckBloodGlucose":
                case "CheckBloodGlucoseReport":
                case "CheckBloodPressure":
                case "CheckBloodPressureReport":
                case "RecordBloodGlucose":
                case "RecordBloodPressure":
                    break;
                default:
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String base64_audio = null;

                    int size;

                    String url = "https://test-vprc-core.pingan.com.cn:56443/vprc_core_portal/rest/api/group_voice_check";

                    long timestamp = System.currentTimeMillis();

                    String timestampstr = "" + timestamp;

                    String strtoken = "vprccore10042group_ai_cll" + timestamp +"portal";

                    String sign;

                    sign = stringToMD5(strtoken);

                    String property = "ro.serialno";

                    String groupId = "SZJY" + getAndroidOsSystemProperties(property);

                    try {
                        AudioInRecongnition = new FileInputStream(AudioFileRecongnition);
                        byte[] buf = new byte[(int)AudioFileRecongnition.length()];
                        while ((size = AudioInRecongnition.read(buf)) != -1) {
                            base64_audio = Base64.encodeToString(buf, Base64.DEFAULT);
                        }
                        AudioInRecongnition.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    QAILog.d(TAG, "timestampstr: " + timestampstr);
                    QAILog.d(TAG, "strtoken: " + strtoken);
                    QAILog.d(TAG, "sign: " + sign);
                    QAILog.d(TAG, "groupId: " + groupId);

                    OkHttpClient okHttpClient = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .add("appId", "10042")
                            .add("scene", "group_ai_cll")
                            .add("groupId", groupId)
                            .add("groupName", "test1")
                            .add("voice", base64_audio)
                            .add("fileFormat", "pcm")
                            .add("timestamp", timestampstr)
                            .add("token", sign)
                            .add("appIdKey", "88d4d7fa0a2a49d39c5c958ecb8bbe57")
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();

                    Response response = okHttpClient.newCall(request).execute();

                    String strJson = response.body().string();

                    QAILog.i(TAG, strJson);

                    try {
                        if (strJson != null) {
                            JSONObject object = new JSONObject(strJson);
                            if (object != null && (object instanceof JSONObject)) {
                                JSONObject dataObject = object.getJSONObject("data");
                                if (dataObject != null && (dataObject instanceof JSONObject)) {
                                    String returnMsg = dataObject.getString("returnMsg");
                                    JSONObject returnDataObject = dataObject.getJSONObject("returnData");
                                    if (returnDataObject != null && (returnDataObject instanceof JSONObject)) {
                                        String Code = returnDataObject.getString("code");
                                        String userId = returnDataObject.getString("groupUserId");
                                        String level = returnDataObject.getString("level");
                                        int intCode = Integer.parseInt(Code);
                                        String retString = null;

                                        switch (intCode) {
                                            case 200:
                                                retString = "success";
                                                QAILog.i(TAG, retString + userId + level);
                                                break;
                                            default:
                                                retString = "failed";
                                                QAILog.i(TAG, retString + returnMsg);
                                                break;
                                        }

                                        Intent intent = new Intent();
                                        intent.setAction("com.android.voicerecongnition");
                                        intent.putExtra("result", retString);
                                        intent.putExtra("matchuser", userId);
                                        intent.putExtra("matchlevel", level);
                                        intent.putExtra("msg", returnMsg);
                                        context.sendBroadcast(intent);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    private static String getAndroidOsSystemProperties(String property) {
        String ret;
        try {
            systemPropertie_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);

            if ((ret = (String) systemPropertie_get.invoke(null, property)) != null)
                return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return "";
    }
}
