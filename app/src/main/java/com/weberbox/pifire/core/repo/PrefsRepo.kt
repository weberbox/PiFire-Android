package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.data.pref.Pref
import kotlinx.coroutines.flow.Flow

/** App preferences for ViewModels, which don't use `Prefs` directly. */
interface PrefsRepo {
    fun <T> get(pref: Pref<T>): T
    fun <T> set(pref: Pref<T>, value: T)
    fun <T> prefFlow(pref: Pref<T>): Flow<T>
}
