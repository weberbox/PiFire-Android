package com.weberbox.pifire.pellets.presentation.screens

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
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
class SharedDetailsViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val pelletsRepo: PelletsRepo,
    private val pelletsApi: PelletsApi
) : BaseViewModel<PelletsContract.Event, PelletsContract.State, PelletsContract.Effect>() {

    init {
        collectConnectedState()
        collectDataState()
    }

    override fun setInitialState() = PelletsContract.State(
        pellets = Pellets(),
        isInitialLoading = true,
        isConnected = true,
        isLoading = false,
        isDataError = false,
        isRefreshing = false
    )

    override fun handleEvents(event: PelletsContract.Event) {
        when (event) {
            is PelletsContract.Event.Refresh -> getPelletsData()
            is PelletsContract.Event.SendEvent -> sendPelletsEvent(event.event)

            // Shouldn't make it here (events handled in UI for dialogs etc)
            else -> {}
        }
    }

    private fun sendPelletsEvent(event: PelletsEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            setLoadingStateTrue()
            handleResult(
                when (event) {
                    is PelletsEvent.DeleteBrand -> pelletsApi.deletePelletBrand(event.brand)
                    is PelletsEvent.DeleteWood -> pelletsApi.deletePelletWood(event.wood)
                    is PelletsEvent.DeleteProfile -> pelletsApi.deletePelletProfile(event.profile)
                    is PelletsEvent.EditProfile -> pelletsApi.editPelletProfile(event.profile)
                    is PelletsEvent.LoadProfile -> pelletsApi.loadPelletProfile(event.profile)
                    is PelletsEvent.DeleteLog -> pelletsApi.deletePelletLog(event.logDate)

                    // Only above events supported shouldn't make it here
                    else -> Result.Success(Unit)
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

    private fun collectDataState() {
        viewModelScope.launch {
            pelletsRepo.getPelletsDataState().collect { data ->
                if (data != null) {
                    setState {
                        copy(
                            pellets = data,
                            isInitialLoading = false,
                            isRefreshing = false,
                            isDataError = false,
                            isLoading = false
                        )
                    }
                } else {
                    // Shouldn't be null but just in case
                    setState {
                        copy(
                            isInitialLoading = false,
                            isRefreshing = false,
                            isLoading = false,
                            isDataError = true
                        )
                    }
                }
            }
        }
    }

    private fun getPelletsData() {
        setState { copy(isRefreshing = true) }
        viewModelScope.launch {
            val result = pelletsRepo.getPellets()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
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
                                isRefreshing = false,
                                isDataError = false
                            )
                        }
                    }

                    is Result.Success -> {
                        setState {
                            copy(
                                pellets = result.data,
                                isInitialLoading = false,
                                isLoading = false,
                                isRefreshing = false,
                                isDataError = false
                            )
                        }
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
                    // collectDataState clears loading
                }
            }
        }
    }

    private suspend fun setLoadingStateTrue() {
        withContext(Dispatchers.Main) {
            setState { copy(isLoading = true) }
        }
    }
}