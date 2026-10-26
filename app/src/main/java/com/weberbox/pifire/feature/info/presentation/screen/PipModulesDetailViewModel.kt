package com.weberbox.pifire.feature.info.presentation.screen

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.feature.info.presentation.contract.ModulesContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PipModulesDetailViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
) : BaseViewModel<ModulesContract.Event, ModulesContract.State, ModulesContract.Effect>() {

    init {
        collectModulesList()
    }

    override fun setInitialState() = ModulesContract.State(
        pipModules = emptyList(),
        isInitialLoading = true,
        isRefreshing = false,
        isDataError = false,
    )

    override fun handleEvents(event: ModulesContract.Event) {
        // Currently none
    }

    private fun collectModulesList() {
        viewModelScope.launch {
            sessionStateHolder.infoDataState.collect { data ->
                data?.pipList.also { modules ->
                    setState {
                        copy(
                            pipModules = modules.orEmpty(),
                            isInitialLoading = false,
                        )
                    }
                } ?: setState {
                    copy(
                        isInitialLoading = false,
                        isDataError = true,
                    )
                }
            }
        }
    }
}
