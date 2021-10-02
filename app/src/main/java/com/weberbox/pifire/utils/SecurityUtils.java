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

    public static SharedPreferences getEncryptedSharedPreferences(Context context) throws GeneralSecurityException, IOException {
        SharedPreferences sharedPreferences = null;
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
        SharedPreferences sharedPreferences = null;
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    context.getPackageName() + PREFS_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            sharedPreferences.edit().putString(context.getString(key), password).apply();
        } catch (GeneralSecurityException | IOException e) {
            Timber.w(e,"Error encrypting password: %s", e.getMessage());
            return false;
        }

        return true;
    }

    public static String decrypt(Context context, int key) {
        SharedPreferences sharedPreferences = null;
        String masterKeyAlias = null;
        String decryptedPassword = "";
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    context.getPackageName() + PREFS_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            decryptedPassword = sharedPreferences.getString(context.getString(key), "");
        } catch (GeneralSecurityException | IOException e) {
            Timber.w(e, "Error decrypting password: %s", e.getMessage());
            decryptedPassword = "Error decrypting password";
        }

        return decryptedPassword;
    }
}