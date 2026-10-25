package com.weberbox.pifire.feature.setup.presentation.screen

import androidx.lifecycle.ViewModel
import com.weberbox.pifire.feature.setup.data.util.ServerDataCache
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val serverDataCache: ServerDataCache
) : ViewModel() {

    fun clearServerDataCache() {
        serverDataCache.clearServerData()
    }
}