/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.qloveaicore.api.okhttp;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

//import kinstalk.com.api.Api;
//import kinstalk.com.api.exception.ApiException;
//import kinstalk.com.api.exception.ErrorDetails;
//import kinstalk.com.base.BaseApplication;
//import kinstalk.com.common.CountlyEvents;
//import kinstalk.com.common.QAIConfig;
//import kinstalk.com.qloveaiengine.R;
//import kinstalk.com.utils.QAILog;
//import kinstalk.com.utils.SharePreUtils;
import kinstalk.com.common.QAIConfig;
import kinstalk.com.common.utils.QAILog;
import kinstalk.com.common.utils.SharePreUtils;
import kinstalk.com.qloveaicore.R;
import kinstalk.com.qloveaicore.api.Api;
import kinstalk.com.qloveaicore.api.exception.ApiException;
import kinstalk.com.qloveaicore.api.exception.ErrorDetails;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Knight.Xu on 2017/8/7.
 * Modified by Knight.Xu on 2018/1/18.
 * accessKey 及errorCode相关的拦截处理
 */

public class AccessKeyInterceptor implements Interceptor {

    private static final String TAG = "AI-AccessKeyInterceptor";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public AccessKeyInterceptor() {
    }

    /**
     * 若无access_key值，发送http://xxx.xx/engine/key/获取一个key数值
     * 若处理返回中，JSON的code=-2, 那么为超时失效的key，这个时候需要再次申请key
     * 错误码：
     * -1 ：未带校验access_key，或者未填请求数据
     * -2 ： access_key不正确或者过期
     * -3 : 请求方法不对，比如POST请求，客户端却发了GET请求
     * -4 ： POST数据中，填写的数据不符合要求
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Context context = Api.applicationContext;

        String oldAccessKey = SharePreUtils.getUserToken();
        Request request = chain.request();
        Response response = null;
        String oldUrl = request.url().toString();

        String errorMsg = "";
        String detailMessage = "";

        if (!TextUtils.isEmpty(oldAccessKey)) {
            response = doRequest(chain, request);

            if (response == null) {
                errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                if (QAIConfig.isNetworkGood) {
                    detailMessage = ErrorDetails.RESPONSE_EMPTY;
                } else {
                    // 网络不行，忽略上报此错误
                    detailMessage = "";
                }
                QAILog.e(TAG, "intercept response empty");
            } else if (!response.isSuccessful()) {
                errorMsg = context.getString(R.string.api_error_rsp_SERVER_ERROR);
                QAILog.e(TAG, "intercept response failed");
                detailMessage = ErrorDetails.RESPONSE_CODE + response.code();
            } else {
                if (!HttpHeaders.hasBody(response)) {
                    QAILog.e(TAG, "intercept rspBody empty");
                    errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                    detailMessage = ErrorDetails.RESPONSE_BODY_EMPTY;
                } else if (OkhttpUtils.bodyEncoded(response.headers())) {
                    QAILog.e(TAG, "intercept ：HTTP (encoded body omitted)");
                    errorMsg = context.getString(R.string.api_error_rsp_empty_contentEncoding);
                    detailMessage = ErrorDetails.RSP_EMPTY_CONTENT_ENCODING;
                } else {
                    ResponseBody rspBody = response.body();
                    long contentLength = rspBody.contentLength();
                    BufferedSource source = rspBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    Charset charset = UTF8;
                    MediaType contentType = rspBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }

                    if (!OkhttpUtils.isPlaintext(buffer)) {
                        QAILog.e(TAG, "intercept body omitted)");
                        return response;
                    }

                    if (contentLength != 0) {
                        // String sBody = rspBody.string();
                        String sBody = buffer.clone().readString(charset);
                        QAILog.v(TAG, "intercept sBody: "/* + sBody*/);
                        try {
                            JSONObject json = new JSONObject(sBody);
                            int rspCode = json.optInt("code");
                            QAILog.v(TAG, "intercept sBody: rspCode: " + rspCode);
                            if (rspCode == Api.ERROR_CODE_ACCESS_KEY_FAILED) {
                                try {
                                    String newAccessKey = Api.reqAccessKey();
                                    // 替换新的access Key
//                                    String newUrl = oldUrl.replace(oldAccessKey, newAccessKey);
//                                    Request newRequest = request.newBuilder().url(newUrl).build();
                                    HttpUrl url = request
                                            .url()
                                            .newBuilder()
                                            .setQueryParameter(Api.ACCESS_KEY_PARA, newAccessKey)
                                            .build();
                                    Request newRequest = request
                                            .newBuilder()
                                            .url(url)
                                            .build();
                                    // 换成新的access key进行请求
                                    response = doRequest(chain, newRequest);
                                } catch (ApiException e) {
                                    errorMsg = e.getMessage();
                                    e.printStackTrace();
                                    QAILog.e(TAG, "AccessKeyInterceptor ApiException: " + e.getMessage());
                                }
                            }
                        } catch (JSONException e) {
                            errorMsg = context.getString(R.string.api_error_POST_DATA_ERROR);
                            detailMessage = e.getMessage();
                            e.printStackTrace();
                            QAILog.e(TAG, "intercept JSONException: " + e.getMessage());
                        }
                    } else {
                        errorMsg = context.getString(R.string.api_error_rsp_EMPTY);
                        detailMessage = ErrorDetails.CONTENT_LENGTH_0;
                        QAILog.e(TAG, "intercept rspBody empty");
                    }
                }
            }
        } else {
            try {
                //用新Access Key拼接
//                String newAccessKey = Api.ACCESS_KEYWORD + Api.reqAccessKey();
//                String newUrl = oldUrl.replace(Api.ACCESS_KEYWORD, newAccessKey);
//                Request newRequest = request.newBuilder().url(newUrl).build();
                String newAccessKey = Api.reqAccessKey();
                HttpUrl url = request
                        .url()
                        .newBuilder()
                        .setQueryParameter(Api.ACCESS_KEY_PARA, newAccessKey)
                        .build();
                Request newRequest = request
                        .newBuilder()
                        .url(url)
                        .build();
                // 拼接新access key后进行请求
                response = doRequest(chain, newRequest);
            } catch (ApiException e) {
                errorMsg = e.getMessage();
                e.printStackTrace();
                QAILog.e(TAG, "AccessKeyInterceptor intercept: " + e.getMessage());
            }
        }

        if (!TextUtils.isEmpty(errorMsg) && !TextUtils.isEmpty(detailMessage)) {
            //ToastUtils.showShort(errorMsg);
//            CountlyEvents.accessKeyInterceptorErrorMsg(errorMsg, detailMessage);
            QAILog.d(TAG, "errorMsg " + errorMsg);
        }
        return response;
    }

    private Response doRequest(Chain chain, Request request) {
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            QAILog.e(e.toString());
        }
        return response;
    }
}
