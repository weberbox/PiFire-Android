package com.weberbox.pifire.setup.presentation.screens

import androidx.lifecycle.ViewModel
import com.weberbox.pifire.setup.data.util.ServerDataCache
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