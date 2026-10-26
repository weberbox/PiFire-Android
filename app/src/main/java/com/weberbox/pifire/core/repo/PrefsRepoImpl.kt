package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.data.pref.Prefs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PrefsRepoImpl @Inject constructor(
    private val prefs: Prefs,
) : PrefsRepo {

    override fun <T> get(pref: Pref<T>): T = prefs.get(pref)

    override fun <T> set(pref: Pref<T>, value: T) {
        prefs.set(pref, value)
    }

    override fun <T> prefFlow(pref: Pref<T>): Flow<T> = prefs.collectPrefsFlow(pref)
}
