package com.weberbox.pifire.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

import timber.log.Timber;

public class SecurityUtils {

    private static final String PREFS_NAME = "_encrypted_prefs";

    public static SharedPreferences getEncryptedSharedPreferences(Context context)
            throws GeneralSecurityException, IOException {
        SharedPreferences sharedPreferences;
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        sharedPreferences = EncryptedSharedPreferences.create(
                context.getPackageName() + PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        return sharedPreferences;
    }

    public static boolean encrypt(Context context, int key, String password) {
        try {
            getEncryptedSharedPreferences(context).edit().putString(context.getString(key),
                    password).apply();
        } catch (GeneralSecurityException | IOException e) {
            Timber.e(e, "Error encrypting password");
            return false;
        }
        return true;
    }

    public static String decrypt(Context context, int key) {
        String decryptedPassword;
        try {
            decryptedPassword = getEncryptedSharedPreferences(context).getString(
                    context.getString(key), "");
        } catch (GeneralSecurityException | IOException e) {
            Timber.e(e, "Error decrypting password");
            decryptedPassword = "Error decrypting password";
        }
        return decryptedPassword;
    }
}