package com.weberbox.pifire.utils;

import com.weberbox.pifire.model.local.ExtraHeadersModel;

import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HTTPUtils {

    public static OkHttpClient createHttpClient(boolean redirects, boolean sslRedirects) {
        return new OkHttpClient.Builder()
                .followRedirects(redirects)
                .followSslRedirects(sslRedirects)
                .build();
    }

    public static void createHttpGet(String url, String credentials, String extraHeaders,
                                     Callback callback) {
        OkHttpClient client = HTTPUtils.createHttpClient(true, true);
        Request request = HTTPUtils.createHttpRequest(url, credentials, extraHeaders);
        client.newCall(request).enqueue(callback);
    }

    public static Request createHttpRequest(String url, String credentials, String headers) {
        Request.Builder builder = new Request.Builder();
        if (credentials != null) {
            builder.header("Authorization", credentials);
        }
        if (headers != null) {
            ArrayList<ExtraHeadersModel> extraHeaders = ExtraHeadersModel.parseJSON(headers);
            for (ExtraHeadersModel header : extraHeaders) {
                builder.header(header.getHeaderKey(), header.getHeaderValue());
            }
        }
        return builder.url(url).build();
    }
}
