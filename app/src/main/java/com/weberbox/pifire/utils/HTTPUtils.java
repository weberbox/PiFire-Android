package com.weberbox.pifire.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HTTPUtils {

    public static OkHttpClient createHttpClient(boolean redirects, boolean sslRedirects) {
        return new OkHttpClient.Builder()
                .followRedirects(redirects)
                .followSslRedirects(sslRedirects)
                .build();
    }

    public static Request createHttpRequest(String url, String credentials) {
        Request request;
        if (credentials != null) {
            request = new Request.Builder()
                    .header("Authorization", credentials).url(url).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        return request;
    }
}
