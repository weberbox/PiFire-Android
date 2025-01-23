package com.weberbox.pifire.utils;

import com.weberbox.pifire.model.local.ExtraHeadersModel;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HTTPUtils {

    public static OkHttpClient createHttpClient(boolean redirects, boolean sslRedirects) {
        return new OkHttpClient.Builder()
                .followRedirects(redirects)
                .followSslRedirects(sslRedirects)
                .build();
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
