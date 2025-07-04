package com.weberbox.pifire.info.presentation.screens

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.info.presentation.contract.ModulesContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PipModulesDetailViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder
) : BaseViewModel<ModulesContract.Event, ModulesContract.State, ModulesContract.Effect>() {

    init {
        collectModulesList()
    }

    override fun setInitialState() = ModulesContract.State(
        pipModules = emptyList(),
        isInitialLoading = true,
        isRefreshing = false,
        isDataError = false
    )

    override fun handleEvents(event: ModulesContract.Event) {
        // Currently none
    }

    private fun collectModulesList() {
        viewModelScope.launch(Dispatchers.IO) {
            sessionStateHolder.infoDataState.collect { data ->
                withContext(Dispatchers.Main) {
                    data?.pipList.also { modules ->
                        setState {
                            copy(
                                pipModules = modules.orEmpty(),
                                isInitialLoading = false
                            )
                        }
                    } ?: setState {
                        copy(
                            isInitialLoading = false,
                            isDataError = true
                        )
                    }
                }
            }
        }
    }
}