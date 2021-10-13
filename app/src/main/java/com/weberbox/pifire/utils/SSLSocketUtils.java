package com.weberbox.pifire.utils;

import android.annotation.SuppressLint;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import okhttp3.OkHttpClient;
import timber.log.Timber;

public class SSLSocketUtils {

    @SuppressLint("CustomX509TrustManager")
    @SuppressWarnings("unused")
    public static OkHttpClient getOkHttpClient(String url) {

        try {

            URI uri = new URI(url);
            String serverHost = uri.getHost();


            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());

            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.hostnameVerifier((hostname, session) -> serverHost.equalsIgnoreCase(hostname));

            builder.sslSocketFactory(sc.getSocketFactory(), new X509TrustManager() {
                @Override
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            });

            return builder.build();

        } catch (NoSuchAlgorithmException | URISyntaxException | KeyManagementException e) {
            Timber.w(e, "Certificate exception");
        }

        return null;
    }

    public static void set(String hostname, IO.Options options) {
        OkHttpClient okHttpClient = getOkHttpClient(hostname);
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
        IO.setDefaultOkHttpCallFactory(okHttpClient);
        options.callFactory = okHttpClient;
        options.webSocketFactory = okHttpClient;
    }
}