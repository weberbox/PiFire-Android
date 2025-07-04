package com.weberbox.pifire.info.presentation.screens

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.info.data.repo.InfoRepo
import com.weberbox.pifire.info.presentation.contract.InfoContract
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import com.weberbox.pifire.info.presentation.model.Licenses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val infoRepo: InfoRepo
) : BaseViewModel<InfoContract.Event, InfoContract.State, InfoContract.Effect>() {

    init {
        getInitialData()
    }

    override fun setInitialState() = InfoContract.State(
        info = Info(),
        licenseData = Licenses(),
        isInfoLoading = true,
        isLicencesLoading = true,
        isRefreshing = false,
        isDataError = false
    )

    override fun handleEvents(event: InfoContract.Event) {
        when (event) {
            is InfoContract.Event.BackButtonClicked ->
                setEffect { InfoContract.Effect.Navigation.Back }

            is InfoContract.Event.Refresh -> getInfoData(true)
            is InfoContract.Event.PipModulesViewAll ->
                setEffect {
                    InfoContract.Effect.Navigation.NavRoute(
                        route = NavGraph.InfoDest.PipModulesDetails
                    )
                }
        }
    }

    private fun getInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            getInfoData(false)
            getLicences()
        }
    }

    private fun getInfoData(forced: Boolean) {
        if (forced) setState { copy(isRefreshing = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = infoRepo.getInfo()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        if (forced) {
                            setEffect {
                                InfoContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true
                                )
                            }
                            setState { copy(isInfoLoading = false, isRefreshing = false) }
                        } else {
                            getCachedData()
                        }
                    }

                    is Result.Success -> {
                        setState {
                            copy(
                                info = result.data,
                                isInfoLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getCachedData() {
        val result = infoRepo.getCachedData()
        withContext(Dispatchers.Main) {
            when (result) {
                is Result.Error -> {
                    setState {
                        copy(
                            info = Info(),
                            isInfoLoading = false,
                            isLicencesLoading = false,
                            isRefreshing = false,
                            isDataError = true
                        )
                    }
                }

                is Result.Success -> {
                    setState { copy(info = result.data, isInfoLoading = false) }
                    setEffect {
                        InfoContract.Effect.Notification(
                            text = UiText(R.string.alerter_cached_results),
                            error = true
                        )
                    }
                }
            }
        }
    }

    private fun getLicences() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = infoRepo.getLicences()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        // Should never happen but set isLicencesLoading false just in case
                        setState { copy(licenseData = Licenses(), isLicencesLoading = false) }
                    }

                    is Result.Success -> {
                        setState { copy(licenseData = result.data, isLicencesLoading = false) }
                    }
                }
            }
        }
    }
}