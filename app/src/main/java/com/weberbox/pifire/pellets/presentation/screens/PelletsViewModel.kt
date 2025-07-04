package com.weberbox.pifire.pellets.presentation.screens

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.pellets.data.api.PelletsApi
import com.weberbox.pifire.pellets.data.repo.PelletsRepo
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets
import com.weberbox.pifire.pellets.presentation.model.PelletsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PelletsViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val pelletsRepo: PelletsRepo,
    private val pelletsApi: PelletsApi
) : BaseViewModel<PelletsContract.Event, PelletsContract.State, PelletsContract.Effect>() {

    init {
        collectConnectedState()
        getPelletsData(false)
        listenPelletsData()
    }

    override fun setInitialState() = PelletsContract.State(
        pellets = Pellets(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false,
        isRefreshing = false,
        isConnected = false
    )

    override fun handleEvents(event: PelletsContract.Event) {
        when (event) {
            is PelletsContract.Event.Refresh -> getPelletsData(true)
            is PelletsContract.Event.SendEvent -> sendPelletsEvent(event.event)

            // Should not make it here (events handled in UI for dialogs etc)
            else -> {}
        }
    }

    private fun sendPelletsEvent(event: PelletsEvent) {
        setLoadingStateTrue()
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(
                when (event) {
                    is PelletsEvent.HopperCheck -> pelletsApi.getPelletLevel()
                    is PelletsEvent.AddBrand -> pelletsApi.addPelletBrand(event.brand)
                    is PelletsEvent.AddProfile -> pelletsApi.addPelletProfile(
                        event.profile,
                        event.load
                    )

                    is PelletsEvent.AddWood -> pelletsApi.addPelletWood(event.wood)
                    is PelletsEvent.DeleteBrand -> pelletsApi.deletePelletBrand(event.brand)
                    is PelletsEvent.DeleteLog -> pelletsApi.deletePelletLog(event.logDate)
                    is PelletsEvent.DeleteProfile -> pelletsApi.deletePelletProfile(event.profile)
                    is PelletsEvent.DeleteWood -> pelletsApi.deletePelletWood(event.wood)
                    is PelletsEvent.EditProfile -> pelletsApi.editPelletProfile(event.profile)
                    is PelletsEvent.LoadProfile -> pelletsApi.loadPelletProfile(event.profile)
                }
            )
        }
    }

    private fun collectConnectedState() {
        viewModelScope.launch {
            sessionStateHolder.isConnectedState.collect { connected ->
                setState {
                    copy(
                        isConnected = connected
                    )
                }
            }
        }
    }

    private fun listenPelletsData() {
        viewModelScope.launch {
            pelletsRepo.listenPelletsData().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Error -> {
                            if (result.error != DataError.Network.SOCKET_ERROR) {
                                setEffect {
                                    PelletsContract.Effect.Notification(
                                        text = result.error.asUiText(),
                                        error = true
                                    )
                                }
                            }
                            setState { copy(isRefreshing = false, isLoading = false) }
                        }

                        is Result.Success -> {
                            setState {
                                copy(
                                    pellets = result.data,
                                    isInitialLoading = false,
                                    isRefreshing = false,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getPelletsData(forced: Boolean) {
        if (forced) setState { copy(isRefreshing = true) }
        viewModelScope.launch {
            when (val result = pelletsRepo.getPellets()) {
                is Result.Error -> {
                    if (forced) {
                        withContext(Dispatchers.Main) {
                            setEffect {
                                PelletsContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true
                                )
                            }
                            setState {
                                copy(
                                    isInitialLoading = false,
                                    isLoading = false,
                                    isRefreshing = false
                                )
                            }
                        }
                    } else {
                        getCachedData()
                    }
                }

                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        setState {
                            copy(
                                pellets = result.data,
                                isInitialLoading = false,
                                isLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getCachedData() {
        val result = pelletsRepo.getCachedData()
        withContext(Dispatchers.Main) {
            when (result) {
                is Result.Error -> {
                    setState {
                        copy(
                            pellets = Pellets(),
                            isInitialLoading = false,
                            isRefreshing = false,
                            isDataError = true
                        )
                    }
                }

                is Result.Success -> {
                    setState {
                        copy(
                            pellets = result.data,
                            isInitialLoading = false,
                            isRefreshing = false
                        )
                    }
                    setEffect {
                        PelletsContract.Effect.Notification(
                            text = UiText(R.string.alerter_cached_results),
                            error = true
                        )
                    }
                }
            }
        }
    }

    private suspend fun handleResult(result: Result<Unit, DataError>) {
        withContext(Dispatchers.Main) {
            when (result) {
                is Result.Error -> {
                    if (result.error != DataError.Network.SOCKET_ERROR) {
                        setEffect {
                            PelletsContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true
                            )
                        }
                    }
                    setState { copy(isRefreshing = false, isLoading = false) }
                }

                is Result.Success -> {
                    // listenPelletsData clears loading
                }
            }
        }
    }

    private fun setLoadingStateTrue() {
        setState {
            copy(
                isLoading = true
            )
        }
    }
}