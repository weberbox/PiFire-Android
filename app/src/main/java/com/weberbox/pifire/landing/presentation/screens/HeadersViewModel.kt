package com.weberbox.pifire.landing.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.landing.presentation.contract.HeadersContract
import com.weberbox.pifire.settings.data.model.local.HeadersData.Headers.ExtraHeader
import com.weberbox.pifire.settings.data.util.HeadersManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeadersViewModel @Inject constructor(
    private val headersManager: HeadersManager,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<HeadersContract.Event, HeadersContract.State, HeadersContract.Effect>() {
    private var currentUuid by mutableStateOf("")

    init {
        currentUuid = savedStateHandle.toRoute<NavGraph.LandingDest.HeaderSettings>().uuid
        collectHeadersData()
    }

    override fun setInitialState() = HeadersContract.State(
        extraHeaders = emptyList(),
        isInitialLoading = true,
        isDataError = false
    )

    override fun handleEvents(event: HeadersContract.Event) {
        when (event) {
            is HeadersContract.Event.DeleteHeader -> deleteHeader(event.header)
            is HeadersContract.Event.UpdateHeader -> updateHeader(event.header)
        }
    }

    private fun collectHeadersData() {
        viewModelScope.launch {
            headersManager.getExtraHeadersFlow(currentUuid).collect { headers ->
                setState {
                    copy(
                        extraHeaders = headers,
                        isInitialLoading = false
                    )
                }
            }
        }
    }

    private fun deleteHeader(extraHeader: ExtraHeader) {
        viewModelScope.launch {
            if (currentUuid.isNotBlank()) {
                headersManager.deleteExtraHeader(
                    uuid = currentUuid,
                    header = extraHeader
                )
            }
        }
    }

    private fun updateHeader(extraHeader: ExtraHeader) {
        viewModelScope.launch {
            if (currentUuid.isNotBlank()) {
                headersManager.addExtraHeader(
                    uuid = currentUuid,
                    header = extraHeader
                )
            }
        }
    }
}