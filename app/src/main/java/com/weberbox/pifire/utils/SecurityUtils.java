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
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        return EncryptedSharedPreferences.create(
                context.getPackageName() + PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public static boolean encrypt(Context context, int key, String preference) {
        try {
            getEncryptedSharedPreferences(context).edit().putString(context.getString(key),
                    preference).apply();
        } catch (GeneralSecurityException | IOException e) {
            Timber.e(e, "Error encrypting preference");
            return false;
        }
        return true;
    }

    public static String decrypt(Context context, int key) {
        String decryptedPreference;
        try {
            decryptedPreference = getEncryptedSharedPreferences(context).getString(
                    context.getString(key), "");
        } catch (GeneralSecurityException | IOException e) {
            Timber.e(e, "Error decrypting preference");
            decryptedPreference = "Error decrypting preference";
        }
        return decryptedPreference;
    }
}