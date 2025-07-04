package com.weberbox.pifire.core.singleton

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.weberbox.pifire.common.presentation.model.AppTheme
import com.weberbox.pifire.settings.data.model.local.Pref
import com.weberbox.pifire.settings.data.util.Crypto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.util.Base64
import javax.inject.Inject

class Prefs @Inject constructor(
    @ApplicationContext appContext: Context
) {

    private var sharedPrefs: SharedPreferences = appContext.getSharedPreferences(
        appContext.packageName + DEFAULT_SUFFIX,
        Context.MODE_PRIVATE
    )

    init {
        // Clear prior version prefs to avoid issues with v3.0.0+
        if (sharedPrefs.getBoolean("pref_clear_prefs", true)) {
            sharedPrefs.edit { clear() }
            sharedPrefs.edit { putBoolean("pref_clear_prefs", false) }
        }
    }

    fun <T> collectPrefsFlow(pref: Pref<T>): Flow<T> {
        return sharedPrefs.prefsFlow(pref)
    }

    fun <T> get(pref: Pref<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (val defValue = pref.defaultValue) {
            is Boolean -> sharedPrefs.getBoolean(pref.key, defValue) as T
            is Int -> sharedPrefs.getInt(pref.key, defValue) as T
            is Long -> sharedPrefs.getLong(pref.key, defValue) as T
            is Double -> stringToDouble(
                sharedPrefs.getString(pref.key, doubleToString(defValue)) ?: "0.0"
            ) as T

            is String -> {
                if (pref.isEncrypted) {
                    decrypt(sharedPrefs.getString(pref.key, defValue)) as T
                } else {
                    sharedPrefs.getString(pref.key, defValue) as T
                }
            }

            is AppTheme ->
                AppTheme.from(sharedPrefs.getString(pref.key, defValue.type)) as T

            else -> {
                defValue
            }
        }
    }

    fun <T> set(pref: Pref<T>, value: T?) {
        sharedPrefs.edit {
            when (value) {
                is Boolean -> putBoolean(pref.key, value)
                is Int -> putInt(pref.key, value)
                is Long -> putLong(pref.key, value)
                is Double -> putString(pref.key, doubleToString(value))
                is String -> {
                    if (pref.isEncrypted) {
                        putString(pref.key, encrypt(value))
                    } else {
                        putString(pref.key, value)
                    }
                }

                is AppTheme -> putString(pref.key, value.type)
            }
        }
    }

    fun <T> clear(pref: Pref<T>) {
        sharedPrefs.edit {
            remove(pref.key)
        }
    }

    private fun encrypt(value: String): String {
        if (value.isNotBlank()) {
            try {
                val bytes = value.toByteArray()
                val encrypted = Crypto.encrypt(bytes)
                return Base64.getEncoder().encodeToString(encrypted)
            } catch (e: Exception) {
                Timber.Forest.e(e, "Crypto Encrypt Exception")
            }
        }
        return String()
    }

    private fun decrypt(value: String?): String {
        if (!value.isNullOrBlank()) {
            try {
                val bytes = Base64.getDecoder().decode(value)
                return Crypto.decrypt(bytes).decodeToString()
            } catch (e: Exception) {
                Timber.Forest.e(e, "Crypto Decrypt Exception")
            }
        }
        return String()
    }

    private fun doubleToString(double: Double): String {
        return double.toString()
    }

    private fun stringToDouble(string: String): Double {
        return try {
            string.toDouble()
        } catch (_: NumberFormatException) {
            0.0
        }
    }

    private fun <T> SharedPreferences.prefsFlow(pref: Pref<T>): Flow<T> =
        callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
                if (k == pref.key) {
                    trySend(get(pref))
                }
            }
            registerOnSharedPreferenceChangeListener(listener)
            awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
        }.onStart {
            emit(get(pref))
        }

    companion object {
        private const val DEFAULT_SUFFIX: String = "_preferences"
    }
}