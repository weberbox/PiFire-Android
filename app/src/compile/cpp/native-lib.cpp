#include <jni.h>
#include <string>

#define xstr(s) str(s)
#define str(s) #s

extern "C" JNIEXPORT jstring JNICALL
Java_com_weberbox_pifire_secure_SecureCore_getAcraUrlJNI(
        JNIEnv *env,
        jobject /* this */) {
    return env->NewStringUTF(xstr(ACRA_URL));
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_weberbox_pifire_secure_SecureCore_getAcraLoginJNI(
        JNIEnv *env,
        jobject /* this */) {
    return env->NewStringUTF(xstr(ACRA_LOGIN));
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_weberbox_pifire_secure_SecureCore_getAcraAuthJNI(
        JNIEnv *env,
        jobject /* this */) {
    return env->NewStringUTF(xstr(ACRA_AUTH));
}