package kinstalk.com.common.utils;

import android.content.ComponentName;
import android.text.TextUtils;
import android.util.Pair;

import com.tencent.xiaowei.info.XWResponseInfo;

import java.util.HashMap;
import java.util.Map;

import kinstalk.com.countly.CountlyUtils;
import ly.count.android.sdk.Countly;

public class CountlyEvents {

    public static String TAG = "AI-CountlyEvents";

    private static final int XW_ERROR_CODE_VOICE_TIMEOUT = 10008;

    private static final String BIND_SUCCEED = "bind_succeed";
    private static final String UNBIND_SUCCEED = "unbind_succeed";
    private static final String WAKEUP_SUCCEED_SNOWBOY = "wakeup_succeed_snowboy";
    private static final String WAKEUP_FAIL_SNOWBOY = "wakeup_fail_snowboy";
    private static final String WAKEUP_SUCCEED_TENCENT = "wakeup_succeed_tencent";
    private static final String WAKEUP_FAIL_TENCENT = "wakeup_fail_tencent";
    private static final String VOICE_RECOGNNITION_TEXT = "voice_recognition_text";
    private static final String VOICE_RECOGNITION_SEMANTIC = "voice_recognition_semantic";
    private static final String VOICE_RECOGNITION_NO_SKILL_MATCH = "voice_recognition_no_skill_match";
    private static final String VOICE_RECOGNITION_TEXT_EMPTY = "voice_recognition_text_empty";
    private static final String VOICE_RECOGNITION_SEMANTIC_EMPTY = "voice_recognition_semantic_empty";
    private static final String VOICE_RECOGNITION_TIMEOUT = "voice_recognition_timeout";
    private static final String VOICE_RECOGNITION_TRANSITION_WAKEUP = "voice_recognition_transition_wakeup";
    private static final String TEXT_RECOGNITION_SUCCEED = "text_recognition_succeed";
    private static final String TEXT_RECOGNITION_FAIL_NO_FEEDBACK = "text_recognition_fail_no_feedback";
    private static final String TEXT_RECOGNITION_APPINFO = "text_recognition_appInfo";
    private static final String TX_ON_RECOGNIZE_BEGIN = "voice_recognition_onRecognizeBegin";
    private static final String TX_ON_RECOGNIZE_END = "voice_recognition_onRecognizeEnd";
    private static final String TENCENT_PLAYER_EVENT_CALLBACK = "tencent_player_event_callback";
    private static final String ENGINEAPI_RESPONSE_SUCCEED = "engineApi_response_succeed";
    private static final String ENGINEAPI_RESPONSE_IOEXCEPTION = "engineApi_response_IOException";
    private static final String ENGINEAPI_RESPONSE_JSONEXCEPTION = "engineApi_response_JSONException";
    private static final String ENGINEAPI_RESPONSE_EMPTY = "engineApi_response_empty";
    private static final String ENGINEAPI_CALL_FAIL = "engineApi_call_fail";
    private static final String ENGINEAPI_INPUT_OUTPUT = "engineapi_input_output";
    private static final String CLIENT_REGISTER = "client_register";
    private static final String CLIENT_REGISTER_TYPEERROR = "client_register_typeError";
    private static final String CLIENT_UNREGISTER = "client_unRegister";
    private static final String CLIENT_UNREGISTER_TYPEERROR = "client_unRegister_typeError";
    private static final String CLIENT_REQUESTDATA = "client_requestData";
    private static final String CLIENT_REQUESTDATA_EMPTY = "client_requestData_empty";
    private static final String CLIENT_REQUESTDATA_ERROR = "client_requestData_error";
    private static final String CLIENT_REQUESTTTS = "client_requestTTS";
    private static final String CLIENT_REQUESTTTS_TEXT_EMPTY = "client_requestTTS_Text_Empty";
    private static final String REACTION_TIME_WAKEUP = "reaction_time_wakeup";
    private static final String REACTION_TIME_RECOGNITION = "reaction_time_recognition";
    private static final String REACTION_TIME_TX_ASR = "reaction_time_tx_asr";
    private static final String REACTION_TIME_TX_NLP = "reaction_time_tx_nlp";
    private static final String REACTION_TIME_FEEDBACK = "reaction_time_feedback";
    //    private static final String FACE_RECOGNITION_SOMEBODY = "face_recognition_somebody";
//    private static final String FACE_RECOGNITION_NOBODY = "face_recognition_nobody";
//    private static final String FACE_RECOGNITION_MALE = "face_recognition_male";
//    private static final String FACE_RECOGNITION_FEMALE = "face_recognition_female";
//    private static final String FACE_RECOGNITION_ADULT = "face_recognition_adult";
//    private static final String FACE_RECOGNITION_OLDMAN = "face_recognition_oldman";
//    private static final String FACE_RECOGNITION_CHILD = "face_recognition_child";
//    private static final String CAMERA_POWER_CONSUMPTION = "camera_power_consumption";
//    private static final String NATIVE_CRASH = "native_crash";
    private static final String VOLUME_CHANGE = "volume_change";
    private static final String OSS_AUDIOFILE_PARAMETER = "oss_audiofile_parameter";
    //    private static final String OSS_BUGFILE_PARAMETER = "oss_bugfile_parameter";
    private static final String OSS_COOKER_SUCESS = "osscooker_sucess";
    private static final String OSS_COOKER_FAIL = "osscooker_fail";
    private static final String AWS_AUDIOFILE_PARAMETER = "aws_audiofile_parameter";
    private static final String AWS_ON_WAKEUP_EVENT_BUFFER_PARAMETER = "aws_on_wakeup_event_buffer_parameter";
    private static final String AWS_RECOGNIZE_BUFFER_PARAMETER = "aws_recognize_buffer_parameter";
    private static final String WAKEUP_LOCALLY = "wakeup_event_locally";
    private static final String REQ_ACCESS_KEY_ERROR_MSG = "req_access_key_error_msg";
    private static final String REQ_CONFIG_LIST_ERROR_MSG = "req_config_list_error_msg";
    private static final String REQ_PID_LICENCE_ERROR_MSG = "req_pid_licence_error_msg";
    private static final String ACCESS_KEY_ERROR_MSG = "access_key_error_msg";
    private static final String START_RECORDING_EXCEPTION = "startRecording_Exception";
    private static final String START_RECORDING_RETRY_EXCEPTION = "startRecording_Retry_Exception";
    private static final String READ_RECORDING_FAILED = "readRecording_Failed";
    private static final String USER_ADDRESS = "user_address";
    private static final String INIT_TX_SDK_WRONG_SN = "init_tx_tsdk_wrong_sn";
    private static final String TX_ERROR_ONUPLOADREGINFO = "tx_error_onuploadreginfo";
    private static final String TX_ERROR_ONLOGINCOMPLETE = "tx_error_onlogincomplete";
    private static final String TX_ERROR_ONBINDCALLBACK = "tx_error_onbindcallback";
    private static final String TX_ERROR_ONBINDERLISTCHANGE = "tx_error_onbinderlistchange";
    private static final String TX_ERROR_ONSTATE = "tx_error_onstate";
    private static final String TX_ON_NETWORK_DELAY = "tx_on_network_delay";
    private static final String TX_ON_WAKEUP = "tx_on_wakeup";
    private static final String TX_ONSTATE_RESPONSE = "tx_onstate_response";
    private static final String COUNTLY_AICORE = "AIEngine";

    public static void bindSucceed() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.BIND_SUCCEED, 1);
    }

    public static void unBindSucceed() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.UNBIND_SUCCEED, 1);
    }

    public static void wakeupSucceedSnowboy() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.WAKEUP_SUCCEED_SNOWBOY, 1);
    }

    public static void wakeupFailSnowboy() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.WAKEUP_FAIL_SNOWBOY, 1);
    }

    public static void wakeupSucceedTencent() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.WAKEUP_SUCCEED_TENCENT, 1);
    }

    public static void wakeupFailTencent() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.WAKEUP_FAIL_TENCENT, 1);
    }

    /**
     * 语义识别事件
     *
     * @param rspData 响应的数据
     */
    public static void recognitionSemantic(XWResponseInfo rspData) {

        if (rspData == null) {
            return;
        }

        endEventTimeRecognition(rspData);

        if (rspData.resultCode == XW_ERROR_CODE_VOICE_TIMEOUT) {
            voiceRecognitionTimeout();
            return;
        }
        if (!TextUtils.isEmpty(rspData.requestText)) {
            voiceRecognitionText(rspData.requestText, rspData.voiceID);
        } else {
            voiceRecognitionTextEmpty();
        }

        if (rspData.appInfo != null && !TextUtils.isEmpty(rspData.appInfo.name)) {
            voiceRecognitionSemantic(rspData.appInfo.name);
        } else {
            voiceRecognitionSemanticEmpty();
        }
    }

    public static void voiceRecognitionText(String speechText, String voiceID) {
        Map<String, String> m = CountlyUtils.getCountlyMap("speak", speechText, "voiceid", voiceID);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.VOICE_RECOGNNITION_TEXT, m, 1);
    }

    public static void voiceRecognitionSemantic(String appName) {
        Map<String, String> m = CountlyUtils.getCountlyMap("Recognize_appName", appName);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.VOICE_RECOGNITION_SEMANTIC, m, 1);
    }

    public static void voiceRecognitionTextEmpty() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.VOICE_RECOGNITION_TEXT_EMPTY, 1);
    }

    public static void voiceRecognitionSemanticEmpty() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.VOICE_RECOGNITION_SEMANTIC_EMPTY, 1);
    }

    public static void voiceRecognitionTimeout() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.VOICE_RECOGNITION_TIMEOUT, 1);
    }

    public static void voiceRecognitionNoSkillMatch(String skillName) {
        Map<String, String> seg = new HashMap<>(1);
        seg.put("skill", skillName);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, VOICE_RECOGNITION_NO_SKILL_MATCH, seg, 1);
    }

    public static void voiceOnRecognizeBegin(String voiceID) {
        Map<String, String> m = CountlyUtils.getCountlyMap("voiceid", voiceID);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TX_ON_RECOGNIZE_BEGIN, 1);
    }

    public static void voiceOnRecognizeEnd(String voiceID) {
        Map<String, String> m = CountlyUtils.getCountlyMap("voiceid", voiceID);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TX_ON_RECOGNIZE_END, 1);
    }

    public static void voiceRecognitionTransitionWakeup() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.VOICE_RECOGNITION_TRANSITION_WAKEUP, 1);
    }

    public static void textRecognitionSucceed(String speechText) {
        Map<String, String> m = CountlyUtils.getCountlyMap("touchText", speechText);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TEXT_RECOGNITION_SUCCEED, m, 1);
    }

    public static void textRecognitionFailNoFeedback() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TEXT_RECOGNITION_FAIL_NO_FEEDBACK, 1);
    }

    public static void textRecognitionAppInfo() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TEXT_RECOGNITION_APPINFO, 1);
    }

    public static void engineApiResponseSucceed(String type, String speechtext) {
        Map<String, String> m = CountlyUtils.getCountlyMap("ServiceType", type, "text", speechtext);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.ENGINEAPI_RESPONSE_SUCCEED, m, 1);
    }

    public static void engineApiResponseIoException() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.ENGINEAPI_RESPONSE_IOEXCEPTION, 1);
    }

    public static void engineApiResponseJsonException() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.ENGINEAPI_RESPONSE_JSONEXCEPTION, 1);
    }

    public static void engineApiResponseEmpty(String cmd) {
        Map<String, String> m = CountlyUtils.getCountlyMap("cmd", cmd);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.ENGINEAPI_RESPONSE_EMPTY, m, 1);
    }

    public static void engineApiCallFail(String className, String failReason) {
        Map<String, String> m = CountlyUtils.getCountlyMap("className", className, "failReason", failReason);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.ENGINEAPI_CALL_FAIL, m, 1);
    }

    public static void engineApiInputAndOutput(String input, String output, long time) {
        HashMap<String, String> segmentation = new HashMap<String, String>();
        segmentation.put("input", input);
        segmentation.put("output", output);
        segmentation.put("time", String.valueOf(time));
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.ENGINEAPI_INPUT_OUTPUT, segmentation, 1);
    }

    public static void clientRegister(Tuple3<String, ComponentName, Boolean> tuple) {
        if (tuple != null && !TextUtils.isEmpty(tuple.first)) {
            CountlyEvents.clientRegisterSuccess(tuple.first);
        } else {
            CountlyEvents.clientRegisterTypeError();
        }
    }

    public static void clientRegisterSuccess(String registerType) {
        Map<String, String> m = CountlyUtils.getCountlyMap("registerType", registerType);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_REGISTER, m, 1);
    }

    public static void clientRegisterTypeError() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_REGISTER_TYPEERROR, 1);
    }

    public static void clientUnregister(Pair<String, ComponentName> pair) {
        if (pair != null && !TextUtils.isEmpty(pair.first)) {
            CountlyEvents.clientUnregisterSuccess(pair.first);
        } else {
            CountlyEvents.clientUnregisterTypeError();
        }
    }

    public static void clientUnregisterSuccess(String unRegisterType) {
        Map<String, String> m = CountlyUtils.getCountlyMap("unRegisterType", unRegisterType);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_UNREGISTER, m, 1);
    }

    public static void clientUnregisterTypeError() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_UNREGISTER_TYPEERROR, 1);
    }

    public static void clientRequestData(String typeService, String opcode, String data) {
        if (!TextUtils.isEmpty(typeService) && !TextUtils.isEmpty(opcode)) {
            Map<String, String> m = CountlyUtils.getCountlyMap("requestService", typeService, "requestOpcode", opcode);
            if (!TextUtils.isEmpty(data)) {
                m.put("requestData", data);
            }
            Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_REQUESTDATA, m, 1);
        } else {
            CountlyEvents.clientRequestDataEmpty();
        }
    }

    public static void clientRequestDataEmpty() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_REQUESTDATA_EMPTY, 1);
    }

    public static void clientRequestDataError() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_REQUESTDATA_ERROR, 1);
    }

    public static void clientRequestTTS(boolean isSuccess, String request) {
        if (isSuccess) {
            clientRequestTTSSuccess(request);
        } else {
            clientRequestTTSTextEmpty();
        }
    }
    public static void clientRequestTTSSuccess(String speechtext) {
        Map<String, String> m = CountlyUtils.getCountlyMap("ttstext", speechtext);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_REQUESTTTS, m, 1);
    }

    public static void clientRequestTTSTextEmpty() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.CLIENT_REQUESTTTS_TEXT_EMPTY, 1);
    }

    public static void collectDropbox(String extratag, String crashdetail, String objectKey) {
        Countly.sharedInstance().dropboxCollecter(extratag, crashdetail, objectKey);
    }

    public static void volumeChange(String audiotag, String vol) {
        HashMap<String, String> segmentation = new HashMap<String, String>();
        segmentation.put("audiotag", audiotag);
        segmentation.put("vol", vol);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.VOLUME_CHANGE, segmentation, 1);
    }

    public static void ossCookerSucess() {
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.OSS_COOKER_SUCESS, 1);
    }

    public static void ossCookerFail(String clienterror, String serviceerror) {
        HashMap<String, String> segmentation = new HashMap<String, String>();
        segmentation.put("clienterror", clienterror);
        segmentation.put("serviceerror", serviceerror);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.OSS_COOKER_FAIL, segmentation, 1);
    }

    //上传成功唤醒的voiceid用来区分aws上的buffer是wakeup还是recognize
    public static void onWakeupEventAws(String localWakeup, String wakeup, int flag, String voiceid) {
        HashMap<String, String> segmentation = new HashMap<String, String>();
        segmentation.put("local_wakeup", localWakeup);
        //wakeup is empty when flag is 2, segmentation value cannot be null or empty,
        //so put localWakeup value into wakeup key
        if (TextUtils.isEmpty(wakeup)) {
            segmentation.put("wakeup", localWakeup);
        } else {
            segmentation.put("wakeup", wakeup);
        }
        segmentation.put("flag", String.valueOf(flag));
        segmentation.put("voiceid", voiceid);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.AWS_ON_WAKEUP_EVENT_BUFFER_PARAMETER, segmentation, 1);
    }

    public static void reqAccessKeyErrorMsg(String errorMsg, String detailMessage) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errormsg", errorMsg);
        if (!TextUtils.isEmpty(detailMessage)) {
            m.put("detailmsg", detailMessage);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.REQ_ACCESS_KEY_ERROR_MSG, m, 1);
    }

    public static void reqConfigListErrorMsg(String errorMsg, String detailMessage) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errormsg", errorMsg);
        if (!TextUtils.isEmpty(detailMessage)) {
            m.put("detailmsg", detailMessage);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.REQ_CONFIG_LIST_ERROR_MSG, m, 1);
    }

    public static void reqPidLicenceErrorMsg(String errorMsg, String detailMessage) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errormsg", errorMsg);
        if (!TextUtils.isEmpty(detailMessage)) {
            m.put("detailmsg", detailMessage);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.REQ_PID_LICENCE_ERROR_MSG, m, 1);
    }


    public static void accessKeyInterceptorErrorMsg(String errorMsg, String detailMessage) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errormsg", errorMsg);
        if (!TextUtils.isEmpty(detailMessage)) {
            m.put("detailmsg", detailMessage);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.ACCESS_KEY_ERROR_MSG, m, 1);
    }

    public static void initTxSdkWrongSN(String snType, String wrongSn) {
        Map<String, String> m = CountlyUtils.getCountlyMap("sn_type", snType, "wrong_sn", wrongSn);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.INIT_TX_SDK_WRONG_SN, m, 1);
    }

    public static void startRecordingException(String recordType, String errorMsg) {
        Map<String, String> m = CountlyUtils.getCountlyMap("recordType", recordType, "errorMsg", errorMsg);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.START_RECORDING_EXCEPTION, m, 1);
    }

    public static void startRecordingRetryException(String recordType, String errorMsg, int tryCount) {
        Map<String, String> m = CountlyUtils.getCountlyMap("recordType", recordType, "errorMsg", errorMsg, "tryCount", String.valueOf(tryCount));
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.START_RECORDING_RETRY_EXCEPTION, m, 1);
    }

    public static void recordReadFailedTooManyTimes(String recordType, String failedMsg, long failedCount) {
        Map<String, String> m = CountlyUtils.getCountlyMap("recordType", recordType, "errorMsg", failedMsg, "failedCount", String.valueOf(failedCount));
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.READ_RECORDING_FAILED, m, 1);
    }

    public static void recordUserAddress(String address) {
        Map<String, String> m = CountlyUtils.getCountlyMap("address", address);
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.USER_ADDRESS, m, 1);
    }

    public static void onUploadRegInfoError(int errorCode, String errorDetail) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errorCode", String.valueOf(errorCode));
        if (!TextUtils.isEmpty(errorDetail)) {
            m.put("errorDetail", errorDetail);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TX_ERROR_ONUPLOADREGINFO, m, 1);
    }

    public static void onLoginCompleteError(int errorCode, String errorDetail) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errorCode", String.valueOf(errorCode));
        if (!TextUtils.isEmpty(errorDetail)) {
            m.put("errorDetail", errorDetail);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TX_ERROR_ONLOGINCOMPLETE, m, 1);
    }

    public static void onBindCallbackError(long tinyId, int errorCode, String errorDetail) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errorCode", String.valueOf(errorCode), "tinyId", String.valueOf(tinyId));
        if (!TextUtils.isEmpty(errorDetail)) {
            m.put("errorDetail", errorDetail);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TX_ERROR_ONBINDCALLBACK, m, 1);
    }

    public static void onBinderListChangeError(int errorCode, String errorDetail) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errorCode", String.valueOf(errorCode));
        if (!TextUtils.isEmpty(errorDetail)) {
            m.put("errorDetail", errorDetail);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TX_ERROR_ONBINDERLISTCHANGE, m, 1);
    }

    public static void onStateError(int errorCode, String errorDetail, int responseType) {
        Map<String, String> m = CountlyUtils.getCountlyMap("errorCode", String.valueOf(errorCode),
                "responseType", String.valueOf(responseType));
        if (!TextUtils.isEmpty(errorDetail)) {
            m.put("errorDetail", errorDetail);
        }
        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TX_ERROR_ONSTATE, m, 1);
    }

    public static void onNetworkDelayCount(long delay1500MsMore, long delay1000MsMore,
                                           long delay500MsMore, long delay200MsMore) {
        Map<String, String> m = CountlyUtils.getCountlyMap(
                "delay1500MsMore", String.valueOf(delay1500MsMore),
                "delay1000MsMore", String.valueOf(delay1000MsMore),
                "delay500MsMore", String.valueOf(delay500MsMore),
                "delay200MsMore", String.valueOf(delay200MsMore));

        Countly.sharedInstance().recordEvent(COUNTLY_AICORE, CountlyEvents.TX_ON_NETWORK_DELAY, m, 1);
    }

    /**
     * 开始识别事件
     */
    public static void startEventTimeRecognition() {
        Countly.sharedInstance().startEvent(CountlyEvents.REACTION_TIME_RECOGNITION);
    }

    /**
     * 结束识别事件
     * isTimeout 表示是否有识别返回结果
     */
    public static void endEventTimeRecognition(XWResponseInfo rspData) {
        HashMap<String, String> segmentation = new HashMap<>(4);
        segmentation.put("tencent_recognition", rspData.resultCode == XW_ERROR_CODE_VOICE_TIMEOUT ? "timeout" : "normal");
        if (!TextUtils.isEmpty(rspData.voiceID)) {
            segmentation.put("voiceid", rspData.voiceID);
        }
        if (!TextUtils.isEmpty(rspData.requestText)) {
            segmentation.put("tencent_textQuestion", rspData.requestText);
        }
        if (rspData.appInfo != null && !TextUtils.isEmpty(rspData.appInfo.name)) {
            segmentation.put("tencent_appName", rspData.appInfo.name);
        }
        Countly.sharedInstance().endEvent(CountlyEvents.REACTION_TIME_RECOGNITION, segmentation, 1, 0);
    }

    public static void startEvent_Time_Tx_ASR() {
        Countly.sharedInstance().startEvent(CountlyEvents.REACTION_TIME_TX_ASR);
    }

    public static void endEvent_Time_Tx_ASR() {
        HashMap<String, String> segmentation = new HashMap();
        Countly.sharedInstance().endEvent(CountlyEvents.REACTION_TIME_TX_ASR, segmentation, 1, 0);
    }

    public static void startEventTimeTxNLP() {
        Countly.sharedInstance().startEventReNew(CountlyEvents.REACTION_TIME_TX_NLP);
    }

    public static void endEventTimeTxNLP(boolean isTimeout, String voiceID, String textQuestion, String appName) {
        HashMap<String, String> segmentation = new HashMap();
        segmentation.put("tencent_recognition", isTimeout ? "timeout" : "normal");
        if (!TextUtils.isEmpty(voiceID)) {
            segmentation.put("voiceid", voiceID);
        }
        if (!TextUtils.isEmpty(textQuestion)) {
            segmentation.put("tencent_textQuestion", textQuestion);
        }
        if (!TextUtils.isEmpty(appName)) {
            segmentation.put("tencent_appName", appName);
        }
        Countly.sharedInstance().endEvent(CountlyEvents.REACTION_TIME_TX_NLP);
    }

    public static void startEventTimeWakeup() {
        Countly.sharedInstance().startEvent(CountlyEvents.REACTION_TIME_WAKEUP);
    }

    public static void endEventTimeWakeup() {
        Countly.sharedInstance().endEvent(CountlyEvents.REACTION_TIME_WAKEUP);
    }

    public static void startEventTimeAIService() {
        Countly.sharedInstance().startEvent(CountlyEvents.REACTION_TIME_FEEDBACK);
    }

    public static void endEventTimeAIService() {
        Countly.sharedInstance().endEvent(CountlyEvents.REACTION_TIME_FEEDBACK);
    }
}
