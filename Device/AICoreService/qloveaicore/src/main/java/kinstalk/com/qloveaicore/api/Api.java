/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package kinstalk.com.qloveaicore.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
//import com.tencent.aiaudio.TXSdkWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

//import kinstalk.com.api.okhttp.AccessKeyInterceptor;
//import kinstalk.com.api.okhttp.FailedRetryInterceptor;
//import kinstalk.com.api.okhttp.RetryAndChangeIpInterceptor;
//import kinstalk.com.api.response.PidPubKeyLicence;
//import kinstalk.com.base.BaseApplication;
//import kinstalk.com.common.CountlyEvents;
import kinstalk.com.common.QAIConfig;
//import kinstalk.com.qloveaiengine.BuildConfig;
//import kinstalk.com.qloveaiengine.R;
//import kinstalk.com.utils.QAILog;
//import kinstalk.com.utils.SharePreUtils;
//import kinstalk.com.utils.SystemTool;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.SharePreUtils;
import kinstalk.com.common.utils.SystemTool;
import kinstalk.com.qloveaicore.BuildConfig;
import kinstalk.com.qloveaicore.R;
import kinstalk.com.qloveaicore.api.exception.ApiException;
import kinstalk.com.qloveaicore.api.exception.ErrorDetails;
import kinstalk.com.qloveaicore.api.okhttp.AccessKeyInterceptor;
import kinstalk.com.qloveaicore.api.okhttp.FailedRetryInterceptor;
import kinstalk.com.qloveaicore.api.okhttp.RetryAndChangeIpInterceptor;
import kinstalk.com.qloveaicore.api.response.PidPubKeyLicence;
import kinstalk.com.qloveaicore.txsdk.TXOpenSdkWrapper;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

//import java.io.File;
//import okhttp3.Cache;
//import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Knight.Xu on 2017/3/22.
 * Modified by Knight.Xu on 2018/1/18.
 */
public class Api {

    private static final String TAG = "AI-Api";

    /**
     * 服务器地址
     */
    // 请求公共部分
    public static String AI_HOST = "api.prod.qspeaker.com";//default api
    private static final String CONFIG_HOST = "api.prod.qspeaker.com";//default config api

    public static final String AI_HOST_PRODUCT = "api.prod.qspeaker.com";
    public static final String AI_HOST_TEST = "api.test.qspeaker.com";
    public static final String AI_HOST_DEV = "api.dev.qspeaker.com";

    public static final String ACCESS_KEY_PARA = "access_key";
    private static final String ACCESS_KEY_PATH = "engine/key/";
    private static final String LICENSE_PATH = "engine/license/";
    static final String CONFIG_PATH = "config/v1/";

    public static final int ERROR_CODE_NO_ACCESS_KEY = -1; // 未带校验access_key，或者未填请求数据
    public static final int ERROR_CODE_ACCESS_KEY_FAILED = -2; // access_key不正确或者过期
//    public static final int ERROR_CODE_REQ_METHOD_ERROR = -3; // 请求方法不对，比如POST请求，客户端却发了GET请求
//    public static final int ERROR_CODE_POST_DATA_ERROR = -4; // POST数据中，填写的数据不符合要求

    // 消息头
    private static final String HEADER_X_HB_Client_Type = "X-HB-Client-Type";
    private static final String FROM_ANDROID = "android";

//    private static final String CACHE_FOLDER = "okhttp_cache";

    private static final String APP_ID = "2016szjy";

    private static IApiService service;
    private static Retrofit retrofit;
    private static final boolean SHOW_OKHTTP_LOG = !SystemTool.isUserType() && !BuildConfig.IS_RELEASE;
    public static Context applicationContext;

    public static synchronized IApiService getService() {
        if (service == null) {
            service = getRetrofit().create(IApiService.class);
        }
        return service;
    }

    public static synchronized void restartService(){
        service = null;
        retrofit = null;
        getService();
    }

    /**
     * 错误码：
     * -1 ：未带校验access_key，或者未填请求数据
     * -2 ： access_key不正确或者过期
     * -3 : 请求方法不对，比如POST请求，客户端却发了GET请求
     * -4 ： POST数据中，填写的数据不符合要求
     */
    public static String reqAccessKey() throws ApiException {

        QAILog.d(TAG, "reqAccessKey: Enter");
        Context context = applicationContext;

        String accessKey = "";
        int rspCode = -100;
        String errorMsg = "";
        String detailMessage = "";
        Response response = null;

        try {
//            File cacheFile = new File(BaseApplication.getInstance().getCacheDir(), CACHE_FOLDER);
//            Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(8, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .addInterceptor(sHeaderInterceptor);
//                    .cache(cache);
            if (SHOW_OKHTTP_LOG) {
                okHttpBuilder.addInterceptor(sLogInterceptor);
            }
            OkHttpClient okHttpClient = okHttpBuilder.build();
            //发送请求获取响应
            try {
                response = okHttpClient.newCall(createAccessKeyRequest()).execute();
                //判断请求是否成功
                if (response == null) {
                    QAILog.e(TAG, "reqAccessKey response empty");
                    errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                    if (QAIConfig.isNetworkGood) {
                        detailMessage = ErrorDetails.RESPONSE_EMPTY;
                    } else {
                        // 网络不行，忽略上报此错误
                        detailMessage = "";
                    }
                } else if (!response.isSuccessful()) {
                    QAILog.e(TAG, "reqAccessKey response failed");
                    errorMsg = context.getString(R.string.api_error_rsp_SERVER_ERROR);
                    detailMessage = ErrorDetails.RESPONSE_CODE + response.code();
                } else {
                    if (!HttpHeaders.hasBody(response)) {
                        QAILog.e(TAG, "intercept rspBody empty");
                        errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                        detailMessage = ErrorDetails.RESPONSE_BODY_EMPTY;
                    } else {
                        //打印服务端返回结果
                        ResponseBody rspBody = response.body();
                        long contentLength = rspBody.contentLength();
                        if (contentLength != 0) {
                            String sBody = rspBody.string();
                            QAILog.d(TAG, "reqAccessKey body = : "/* + sBody*/);
                            try {
                                JSONObject rspJson = new JSONObject(sBody);
                                rspCode = rspJson.optInt("code");
                                QAILog.v(TAG, "reqAccessKey: rspCode: " + rspCode);
                                if (rspCode == 0) {
                                    accessKey = rspJson.optString("access_key");
                                    if (!TextUtils.isEmpty(accessKey)) {
                                        SharePreUtils.setUserToken(accessKey);
                                    } else {
                                        errorMsg = context.getString(R.string.api_error_ACCESS_KEY_EMPTY);
                                        detailMessage = ErrorDetails.ACCESS_KEY_EMPTY;
                                    }
                                } else if (rspCode == Api.ERROR_CODE_NO_ACCESS_KEY) {
                                    errorMsg = context.getString(R.string.api_error_NO_ACCESS_KEY);
                                    detailMessage = ErrorDetails.SRV_CODE + rspCode;
                                } else {
                                    errorMsg = context.getString(R.string.api_error_req_ACCESS_KEY_FAILED);
                                    detailMessage = ErrorDetails.SRV_CODE + rspCode;
                                }
                            } catch (JSONException e) {
                                errorMsg = context.getString(R.string.api_error_rsp_JSONException);
                                QAILog.e(TAG, "reqAccessKey JSONException : " + e.getMessage());
                                e.printStackTrace();
                                detailMessage = e.getMessage();
                            }
                        } else {
                            errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                            QAILog.e(TAG, "reqAccessKey rspBody empty");
                            detailMessage = ErrorDetails.CONTENT_LENGTH_0;
                        }
                    }
                }
            } catch (IOException e) {
                errorMsg = context.getString(R.string.api_error_rsp_JSONException);
                detailMessage = e.getMessage();
                QAILog.e(TAG, "reqAccessKey error : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Throwable t) {
            QAILog.e(TAG, "reqAccessKey error : " + t.getMessage());
            errorMsg = context.getString(R.string.api_error_unknoow);
            if (QAIConfig.isNetworkGood) {
                detailMessage = t.getMessage();
            } else {
                // 网络不行，忽略上报此错误
                detailMessage = "";
            }
            t.printStackTrace();
        }

        if (TextUtils.isEmpty(accessKey)) {
            ApiException apiException = new ApiException(rspCode, errorMsg);
            throw apiException;
        }
        if (!TextUtils.isEmpty(errorMsg) && !TextUtils.isEmpty(detailMessage)) {
//            CountlyEvents.reqAccessKeyErrorMsg(errorMsg, detailMessage);
        }
        return accessKey;
    }

    /**
     * 拦截器  给所有的请求添加消息头
     */
    private static Interceptor sHeaderInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader(HEADER_X_HB_Client_Type, FROM_ANDROID)
                    .build();
            return chain.proceed(request);
        }
    };

    private static HttpLoggingInterceptor sLogInterceptor = new HttpLoggingInterceptor(new QAILog.QAIHttpLogger(TAG))
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient())
                    .baseUrl(createBaseHttpUrl())
//                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient() {
        // log拦截器  打印所有的log

        //设置 请求的缓存
//        File cacheFile = new File(BaseApplication.getInstance().getCacheDir(), CACHE_FOLDER);
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//允许失败重试
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(new RetryAndChangeIpInterceptor(2))//添加失败重试及重定向拦截器
                .addInterceptor(new AccessKeyInterceptor())//添加AccessKey检查拦截器
                .addInterceptor(sHeaderInterceptor);
//                .cache(cache);

        if (SHOW_OKHTTP_LOG) {
            okHttpBuilder.addInterceptor(sLogInterceptor);
        }
        return okHttpBuilder.build();
    }

    /**
     * 错误码：
     * -4 ：服务器异常
     */
    public static PidPubKeyLicence reqPidPubKeyLicence(String sn, int hwid) {
        QAILog.e(TAG, "reqPidPubKeyLicence() called with: sn = [" + sn + "], hwid = [" + hwid + "]");
        if (QAIConfig.ENABLE_CONFIG_API && TextUtils.isEmpty(QAIConfig.txlicense_url)){
            QAILog.e(TAG, "reqPidPubKeyLicence, txlicense_url is empty, waiting");
            return null;
        }

        Context context = applicationContext;
        int rspCode = -4;
        String errorMsg = "";
        String detailMessage = "";
        Response response;
        PidPubKeyLicence pidPubKeyLicence = null;

        try {
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(8, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .addInterceptor(new FailedRetryInterceptor(3))//添加失败重试截器
                    .addInterceptor(sHeaderInterceptor);
            if (SHOW_OKHTTP_LOG) {
                okHttpBuilder.addInterceptor(sLogInterceptor);
            }
            OkHttpClient okHttpClient = okHttpBuilder.build();
            //发送请求获取响应
            try {
                response = okHttpClient.newCall(createLicenceRequest(sn, hwid, 1)).execute();
                //判断请求是否成功
                if (response == null) {
                    QAILog.e(TAG, "reqPidPubKeyLicence response empty");
                    errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                    if (QAIConfig.isNetworkGood) {
                        detailMessage = ErrorDetails.RESPONSE_EMPTY;
                    } else {
                        // 网络不行，忽略上报此错误
                        detailMessage = "";
                    }
                } else if (!response.isSuccessful()) {
                    QAILog.e(TAG, "reqPidPubKeyLicence response failed");
                    errorMsg = context.getString(R.string.api_error_rsp_SERVER_ERROR);
                    detailMessage = ErrorDetails.RESPONSE_CODE + response.code();
                } else {
                    if (!HttpHeaders.hasBody(response)) {
                        QAILog.e(TAG, "reqPidPubKeyLicence rspBody empty");
                        errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                        detailMessage = ErrorDetails.RESPONSE_BODY_EMPTY;
                    } else {
                        //打印服务端返回结果
                        ResponseBody rspBody = response.body();
                        long contentLength = rspBody.contentLength();
                        if (contentLength != 0) {
                            String sBody = rspBody.string();
                            QAILog.v(TAG, "reqPidPubKeyLicence body = : "/* + sBody*/);
                            try {
                                JSONObject rspJson = new JSONObject(sBody);
                                rspCode = rspJson.optInt("code");
                                QAILog.v(TAG, "reqPidPubKeyLicence: rspCode: " + rspCode);
                                if (rspCode == 0) {
                                    Gson gson = new Gson();
                                    pidPubKeyLicence = gson.fromJson(sBody, PidPubKeyLicence.class);
                                } else {
                                    QAILog.e(TAG, "reqPidPubKeyLicence rspCode error = : " + rspCode);
                                    errorMsg = context.getString(R.string.api_error_req_PID_LICENCE_FAILED);
                                    detailMessage = ErrorDetails.SRV_CODE + rspCode;
                                }
                            } catch (JSONException e) {
                                QAILog.e(TAG, "reqPidPubKeyLicence JSONException : " + e.getMessage());
                                errorMsg = context.getString(R.string.api_error_rsp_JSONException);
                                detailMessage = e.getMessage();
                                e.printStackTrace();
                            }
                        } else {
                            QAILog.e(TAG, "reqPidPubKeyLicence content empty");
                            errorMsg = context.getString(R.string.api_error_rsp_CONTENT_EMPTY);
                            detailMessage = ErrorDetails.CONTENT_LENGTH_0;
                        }
                    }
                }
            } catch (IOException e) {
                QAILog.e(TAG, "reqPidPubKeyLicence IOException : " + e.getMessage());
                errorMsg = context.getString(R.string.api_error_rsp_IOException);
                detailMessage = e.getMessage();
                e.printStackTrace();
            }
        } catch (Throwable t) {
            QAILog.e(TAG, "reqPidPubKeyLicence Throwable : " + t.getMessage());
            errorMsg = context.getString(R.string.api_error_unknoow);
            if (QAIConfig.isNetworkGood) {
                detailMessage = t.getMessage();
            } else {
                detailMessage = "";
            }
            t.printStackTrace();
        }

        if (!TextUtils.isEmpty(errorMsg) && !TextUtils.isEmpty(detailMessage)) {
//            CountlyEvents.reqPidLicenceErrorMsg(errorMsg, detailMessage);
        }
        return pidPubKeyLicence;
    }

    private static HttpUrl createBaseHttpUrl(){
        String SCHEME = "https";
        if(!AI_HOST.equals(AI_HOST_PRODUCT)){
            SCHEME = "http";
        }
        return  new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(AI_HOST)
                .build();
    }

    protected static String createBaseHttpUrl(boolean isHttps){
        String SCHEME = "https://";
        if(!AI_HOST.equals(AI_HOST_PRODUCT) || !isHttps){
            SCHEME = "http://";
        }
        return  SCHEME + AI_HOST + "/";
    }

    /**
     * tx license计算接口
     URL: /engine/license/
     请求格式：发送HTTP GET请求
     * @param sn 为设备号
     * @param hwid 用于不同设备(不同设备，pid在腾讯处不同)
     * @return {@link Request}
     */
    private static Request createLicenceRequest(String sn, int hwid, int newpid){
        if (TextUtils.isEmpty(QAIConfig.txlicense_url)) {
            String SCHEME = "https";
            if(!AI_HOST.equals(AI_HOST_PRODUCT)){
                SCHEME = "http";
            }
            HttpUrl.Builder builder = new HttpUrl.Builder()
                    .scheme(SCHEME)
                    .host(AI_HOST)
                    .addEncodedPathSegment(LICENSE_PATH)
                    .addQueryParameter("sn" , sn)
                    .addQueryParameter("hwid" , String.valueOf(hwid));

            if (QAIConfig.MODEL_MAGELLAN_M10 == QAIConfig.qLoveProductVersionNum)
                builder.addQueryParameter("newpid", String.valueOf(newpid));

            return new Request.Builder()
                    .url(builder.build())
                    .get()
                    .build();
        } else {
            QAILog.v(TAG, "createLicenceRequest : get txlicense_url");

            HttpUrl.Builder builder = new Request.Builder()
                    .url(QAIConfig.txlicense_url)
                    .build()
                    .url()
                    .newBuilder()
                    .addQueryParameter("sn" , sn)
                    .addQueryParameter("hwid" , String.valueOf(hwid));

            if (QAIConfig.MODEL_MAGELLAN_M10 == QAIConfig.qLoveProductVersionNum)
                builder.addQueryParameter("newpid", String.valueOf(newpid));

            return new Request.Builder()
                    .url(builder.build())
                    .get()
                    .build();
        }


    }

    /**
     * 请求key的方法
     URL: /engine/key/
     请求格式：发送HTTP POST请求, JSON格式:
         {
         "appid": "2016szjy",
         "sn": "xxxxxxx" //设备号
         }
     * @return {@link Request}
     */
    private static Request createAccessKeyRequest(){
        Request.Builder builder = new Request.Builder();
        if (TextUtils.isEmpty(QAIConfig.accesskey_url)) {
            String SCHEME = "https";
            if(!AI_HOST.equals(AI_HOST_PRODUCT)){
                SCHEME = "http";
            }
            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme(SCHEME)
                    .host(AI_HOST)
                    .addEncodedPathSegment(ACCESS_KEY_PATH)
                    .build();
            builder.url(httpUrl);
        } else {
            QAILog.v(TAG, "createLicenceRequest : accesskey_url : " + QAIConfig.accesskey_url);
            builder.url(QAIConfig.accesskey_url);
        }

        JSONObject reqJson = new JSONObject();
        try {
            reqJson.put("appid", APP_ID);
            reqJson.put("sn", TXOpenSdkWrapper.sn);
        } catch (JSONException e) {
            e.printStackTrace();
            QAILog.e(TAG, "getAccessKey:reqJsonError = " + e.getMessage());
        }
        String postInfoStr = reqJson.toString();
        QAILog.v(TAG, "getAccessKey:data = " + postInfoStr);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postInfoStr);

        return builder
                .post(requestBody)
                .build();
    }


    public static void reqConfigList(Context context, String action) {

        QAILog.d(TAG, "reqConfigList: Enter");
        int rspCode;
        String errorMsg = "";
        String detailMessage = "";
        okhttp3.Response response;

        try {
//            File cacheFile = new File(BaseApplication.getInstance().getCacheDir(), CACHE_FOLDER);
//            Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    .addInterceptor(sHeaderInterceptor)
                    .addInterceptor(new FailedRetryInterceptor(3))//添加失败重试拦截器
                    .connectTimeout(8, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS);
//                    .cache(cache);
            if (SHOW_OKHTTP_LOG) {
                okHttpBuilder.addInterceptor(sLogInterceptor);
            }
            OkHttpClient okHttpClient = okHttpBuilder.build();
            //发送请求获取响应
            try {
                response = okHttpClient.newCall(createConfigListRequest(action)).execute();
                //判断请求是否成功
                if (response == null) {
                    QAILog.e(TAG, "reqConfigList response empty");
                    errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                    if (QAIConfig.isNetworkGood) {
                        detailMessage = ErrorDetails.RESPONSE_EMPTY;
                    } else {
                        // 网络不行，忽略上报此错误
                        detailMessage = "";
                    }
                } else if (!response.isSuccessful()) {
                    QAILog.e(TAG, "reqConfigList response failed");
                    errorMsg = context.getString(R.string.api_error_rsp_SERVER_ERROR);
                    detailMessage = ErrorDetails.RESPONSE_CODE + response.code();
                } else {
                    if (!HttpHeaders.hasBody(response)) {
                        QAILog.e(TAG, "reqConfigList rspBody empty");
                        errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                        detailMessage = ErrorDetails.RESPONSE_BODY_EMPTY;
                    } else {
                        //打印服务端返回结果
                        ResponseBody rspBody = response.body();
                        long contentLength = rspBody.contentLength();
                        if (contentLength != 0) {
                            String sBody = rspBody.string();
                            QAILog.v(TAG, "reqConfigList body = : " /*+ sBody*/);
                            try {
                                JSONObject rspJson = new JSONObject(sBody);
                                rspCode = rspJson.optInt("code",  -100);
                                QAILog.v(TAG, "reqConfigList: rspCode: " + rspCode);
                                QAIConfig.code = rspCode;
                                if (rspCode == 0) {
                                    QAIConfig.engine = rspJson.optString("engine");
                                    QAIConfig.current_env = rspJson.optInt("current_env");
                                    if (QAIConfig.current_env == 0) {
                                        AI_HOST = AI_HOST_PRODUCT;
                                    } else if (QAIConfig.current_env == 1) {
                                        AI_HOST = AI_HOST_DEV;
                                    } else if (QAIConfig.current_env == 2) {
                                        AI_HOST = AI_HOST_TEST;
                                    }
                                    QAIConfig.autoTestDevice = rspJson.optBoolean("autotest_device");
                                    QAIConfig.isCrashCookerEnable = !rspJson.optBoolean("strict_log_uploading"); // 默认值为false，取反值
                                    QAIConfig.isAudioCookerEnable = !rspJson.optBoolean("strict_dump_audio"); // 默认值为false，取反值

                                    if (rspJson.has("url_list")) {
                                        JSONObject urlListObject = rspJson.getJSONObject("url_list");
                                        QAIConfig.txlicense_url = urlListObject.optString("txlicense");
                                        QAIConfig.aiengine_url = urlListObject.optString("aiengine");
                                        QAIConfig.unani_url = urlListObject.optString("unani");
                                        QAIConfig.accesskey_url = urlListObject.optString("accesskey");
                                        QAIConfig.music_url = urlListObject.optString("music");
                                        QAIConfig.voicecmd_url = urlListObject.optString("voicecmd");
                                    }
                                     QAILog.v(TAG, "reqConfigList  :  dumpQAIConfig"/* + QAIConfig.dumpString()*/);
                                } else {
                                    errorMsg = context.getString(R.string.api_error_req_CONFIG_LIST_FAILED);
                                    detailMessage = ErrorDetails.SRV_CODE + rspCode;
                                }
                            } catch (JSONException e) {
                                errorMsg = context.getString(R.string.api_error_rsp_JSONException);
                                detailMessage = e.getMessage();
                                QAILog.e(TAG, "reqConfigList JSONException : " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                            detailMessage = ErrorDetails.CONTENT_LENGTH_0;
                            QAILog.e(TAG, "reqConfigList rspBody empty");
                        }
                    }
                }
            } catch (IOException e) {
                errorMsg = context.getString(R.string.api_error_rsp_JSONException);
                detailMessage = e.getMessage();
                QAILog.e(TAG, "reqConfigList error : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Throwable t) {
            QAILog.e(TAG, "reqConfigList error : " + t.getMessage());
            errorMsg = context.getString(R.string.api_error_unknoow);
            if (QAIConfig.isNetworkGood) {
                detailMessage = t.getMessage();
            } else {
                detailMessage = "";
            }
            t.printStackTrace();
        }

        if (!TextUtils.isEmpty(errorMsg) && !TextUtils.isEmpty(detailMessage)) {
//            CountlyEvents.reqConfigListErrorMsg(errorMsg, detailMessage);
        }
    }

    private static Request createConfigListRequest(String action) {
        String SCHEME = "https";
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(CONFIG_HOST)
                .addEncodedPathSegment(CONFIG_PATH)
                .build();

        JSONObject reqJson = new JSONObject();
        try {
            reqJson.put("sn", TXOpenSdkWrapper.getQloveSN());
            reqJson.put("action", action);
        } catch (JSONException e) {
            e.printStackTrace();
            QAILog.e(TAG, "getConfigList:reqJsonError = " + e.getMessage());
        }
        String postInfoStr = reqJson.toString();
        QAILog.v(TAG, "getConfigList:data = " + postInfoStr);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postInfoStr);

        return new Request.Builder()
                .url(httpUrl)
                .post(requestBody)
                .build();
    }
}
