/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.qloveaicore.api.okhttp;

/**
 * Created by Knight.Xu on 2017/8/7.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

//import kinstalk.com.utils.QAILog;
import kinstalk.com.common.utils.QAILog;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * errorCode相关的拦截处理
 */
public class FailedRetryInterceptor implements Interceptor {

    private static final String TAG = "AI-FailedRetryInterceptor";

    private static final Charset UTF8 = Charset.forName("UTF-8");
    int RetryCount = 3;

    public FailedRetryInterceptor() {
        RetryCount = 3;
    }

    public FailedRetryInterceptor(int tryCount) {
        RetryCount = tryCount;
    }

    /**
     * 错误码：
     * 0 成功
     * 其他 ： 服务器异常
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        Response response = doRequest(chain, request);
        int tryCount = 0;
        boolean isSuccess = isSuccessCode(response);
        while (!isSuccess && tryCount <= RetryCount) {
            Request newRequest = request.newBuilder().url(url).build();
            QAILog.d("intercept", "Request is not successful - " + tryCount);
            tryCount++;
            response = doRequest(chain, newRequest);
            isSuccess = isSuccessCode(response);
        }
        return response;
    }

    private boolean isSuccessCode(Response response) throws IOException {
        boolean isSuccess = false;

        if (response == null) {
            QAILog.e(TAG, "intercept response empty");
        } else if (!response.isSuccessful()) {
            QAILog.e(TAG, "intercept response failed");
        } else {
            if (!HttpHeaders.hasBody(response)) {
                QAILog.e(TAG, "intercept rspBody empty");
            } else if (OkhttpUtils.bodyEncoded(response.headers())) {
                QAILog.e(TAG, "intercept ：HTTP (encoded body omitted)");
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
                    QAILog.e(TAG, "intercept body omitted");
//                    return response;
                    isSuccess = true;
                }

                if (contentLength != 0) {
                    // String sBody = rspBody.string();
                    String sBody = buffer.clone().readString(charset);
                    QAILog.v(TAG, "intercept sBody: not empty"/* + sBody*/);
                    try {
                        JSONObject json = new JSONObject(sBody);
                        int rspCode = json.optInt("code");
                        if (rspCode == 0) {
                            isSuccess = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        QAILog.e(TAG, "intercept JSONException: " + e.getMessage());
                    }
                } else {
                    QAILog.e(TAG, "intercept rspBody empty");
                }
            }
        }

        return isSuccess;
    }

    private Response doRequest(Chain chain, Request request) {
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            QAILog.e(TAG, e.toString());
        }
        return response;
    }
}
