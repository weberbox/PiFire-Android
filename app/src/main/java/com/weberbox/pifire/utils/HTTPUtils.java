package com.weberbox.pifire.utils;

import android.content.Context;

import com.weberbox.pifire.model.local.ExtraHeadersModel;

import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HTTPUtils {

    private static OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    public static void createHttpGet(Context context, String url, Callback callback) {
        OkHttpClient client = HTTPUtils.createHttpClient();
        Request request = HTTPUtils.createHttpRequest(context, url);
        client.newCall(request).enqueue(callback);
    }

    public static void createHttpPost(Context context, String url, String json, Callback callback) {
        OkHttpClient client = HTTPUtils.createHttpClient();
        RequestBody body = RequestBody.create(json,
                MediaType.parse("application/json; charset=utf-8"));
        Request request = HTTPUtils.createHttpRequest(context, url, body);
        Request request_body = request.newBuilder().post(body).build();
        client.newCall(request_body).enqueue(callback);
    }

    public static Request createHttpRequest(Context context, String url) {
        return createHttpRequest(context, url, null);
    }

    private static Request createHttpRequest(Context context, String url, RequestBody body) {
        String credentials = SecurityUtils.getCredentials(context);
        String headers = SecurityUtils.getExtraHeaders(context);

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
        if (body != null) {
            builder.put(body);
        }
        return builder.url(url).build();
    }

    public static String getReasonPhrase(int statusCode) {
        return switch (statusCode) {
            case (200) -> "OK";
            case (201) -> "Created";
            case (202) -> "Accepted";
            case (203) -> "Non Authoritative Information";
            case (204) -> "No Content";
            case (205) -> "Reset Content";
            case (206) -> "Partial Content";
            case (207) -> "Partial Update OK";
            case (300) -> "Multiple Choices";
            case (301) -> "Moved Permanently";
            case (302) -> "Moved Temporarily";
            case (303) -> "See Other";
            case (304) -> "Not Modified";
            case (305) -> "Use Proxy";
            case (307) -> "Temporary Redirect";
            case (400) -> "Bad Request";
            case (401) -> "Unauthorized";
            case (402) -> "Payment Required";
            case (403) -> "Forbidden";
            case (404) -> "Not Found";
            case (405) -> "Method Not Allowed";
            case (406) -> "Not Acceptable";
            case (407) -> "Proxy Authentication Required";
            case (408) -> "Request Timeout";
            case (409) -> "Conflict";
            case (410) -> "Gone";
            case (411) -> "Length Required";
            case (412) -> "Precondition Failed";
            case (413) -> "Request Entity Too Large";
            case (414) -> "Request-URI Too Long";
            case (415) -> "Unsupported Media Type";
            case (416) -> "Requested Range Not Satisfiable";
            case (417) -> "Expectation Failed";
            case (418) -> "Re-authentication Required";
            case (419) -> "Proxy Re-authentication Required";
            case (422) -> "Unprocessable Entity";
            case (423) -> "Locked";
            case (424) -> "Failed Dependency";
            case (500) -> "Server Error";
            case (501) -> "Not Implemented";
            case (502) -> "Bad Gateway";
            case (503) -> "Service Unavailable";
            case (504) -> "Gateway Timeout";
            case (505) -> "HTTP Version Not Supported";
            case (507) -> "Insufficient Storage";
            default -> "Unknown";
        };
    }
}
