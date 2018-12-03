/*
 * Copyright (c) 2017. Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

package kinstalk.com.qloveaicore.api.okhttp;

import java.io.IOException;

//import kinstalk.com.utils.QAILog;
import kinstalk.com.common.utils.QAILog;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Knight.Xu on 2017/5/12.
 */
public class RetryAndChangeIpInterceptor implements Interceptor {
    int RetryCount = 3;
//    String FirstIP;
//    List<String> SERVERS;

    public RetryAndChangeIpInterceptor() {
//        FirstIP = firsrIP;
//        SERVERS = sERVERS;
        RetryCount = 3;
    }

    public RetryAndChangeIpInterceptor(/*String firsrIP, List<String> sERVERS,*/ int tryCount) {
//        FirstIP = firsrIP;
//        SERVERS = sERVERS;
        RetryCount = tryCount;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // try the request
        Response response = doRequest(chain, request);
        int tryCount = 0;
        String url = request.url().toString();
        while (response == null && tryCount <= RetryCount) {
//            url = switchServer(url);
            Request newRequest = request.newBuilder().url(url).build();
            QAILog.d("intercept", "Request is not successful - " + tryCount);
            tryCount++;
            // retry the request
            response = doRequest(chain, newRequest);
        }
        if (response == null) {
            throw new IOException();
        }
        return response;
    }

    private Response doRequest(Chain chain, Request request) {
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
        }
        return response;
    }

//    private String switchServer(String url) {
//        String newUrlString = url;
//        if (url.contains(FirstIP)) {
//            for (String server : SERVERS) {
//                if (!FirstIP.equals(server)) {
//                    newUrlString = url.replace(FirstIP, server);
//                    break;
//                }
//            }
//        } else {
//            for (String server : SERVERS) {
//                if (url.contains(server)) {
//                    newUrlString = url.replace(server, FirstIP);
//                    break;
//                }
//            }
//        }
//        return newUrlString;
//    }

}
