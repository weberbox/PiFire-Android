package com.weberbox.pifire.common.presentation.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.json.Json

inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }
    }

    override fun parseValue(value: String): T {
        return json.decodeFromString(value)
    }

    override fun serializeAsValue(value: T): String {
        return Uri.encode(json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putParcelable(key, value)
    }
}

@Suppress("unused")
inline fun <reified T : Parcelable> parcelableListType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<List<T>>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String): List<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelableArrayList(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelableArrayList(key)
        }
    }

    override fun parseValue(value: String): List<T> {
        return json.decodeFromString(value)
    }

    override fun serializeAsValue(value: List<T>): String {
        return json.encodeToString(value)
    }

    override fun put(bundle: Bundle, key: String, value: List<T>) {
        bundle.putParcelableArrayList(key, ArrayList(value))
    }
}