package com.weberbox.pifire.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.Credentials;
import timber.log.Timber;

public class SecurityUtils {

    private static final String PREFS_NAME = "_encrypted_prefs";

    public static String getUsername(Context context) {
        return decrypt(context, R.string.prefs_server_basic_auth_user);
    }

    public static String getPassword(Context context) {
        return decrypt(context, R.string.prefs_server_basic_auth_password);
    }

    public static String getHeaders(Context context) {
        return decrypt(context, R.string.prefs_server_headers);
    }

    public static boolean setUsername(Context context, String username) {
        return encrypt(context, R.string.prefs_server_basic_auth_user, username);
    }

    public static boolean setPassword(Context context, String password) {
        return encrypt(context, R.string.prefs_server_basic_auth_password, password);
    }

    public static boolean setHeaders(Context context, String headers) {
        return encrypt(context, R.string.prefs_server_headers, headers);
    }

    public static String getCredentials(Context context) {
        if (Prefs.getBoolean(context.getString(R.string.prefs_server_basic_auth), false)) {
            String username = decrypt(context,
                    R.string.prefs_server_basic_auth_user);
            String password = decrypt(context,
                    R.string.prefs_server_basic_auth_password);
            return Credentials.basic(username, password);
        } else {
            return null;
        }
    }

    public static String getExtraHeaders(Context context) {
        if (Prefs.getBoolean(context.getString(R.string.prefs_server_extra_headers), false)) {
            return decrypt(context, R.string.prefs_server_headers);
        } else {
            return null;
        }
    }

    private static boolean encrypt(Context context, int key, String preference) {
        try {
            getEncryptedSharedPreferences(context).edit().putString(context.getString(key),
                    preference).apply();
        } catch (GeneralSecurityException | IOException e) {
            Timber.e(e, "Error encrypting preference");
            return false;
        }
        return true;
    }

    private static String decrypt(Context context, int key) {
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

    private static SharedPreferences getEncryptedSharedPreferences(Context context)
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
}