package com.weberbox.pifire.secure;


public final class SecureCore {
    private static final String TAG = SecureCore.class.getSimpleName();

    static {
        System.loadLibrary("pifire");
    }

    private static native String getAcraUrlJNI();

    private static native String getAcraLoginJNI();

    private static native String getAcraAuthJNI();


    public static String getAcraUrl() {
        return getAcraUrlJNI();
    }

    public static String getAcraLogin() {
        return getAcraLoginJNI();
    }

    public static String getAcraAuth() {
        return getAcraAuthJNI();
    }
}