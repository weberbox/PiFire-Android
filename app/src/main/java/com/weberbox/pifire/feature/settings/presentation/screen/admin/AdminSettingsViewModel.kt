package com.weberbox.pifire.feature.settings.presentation.screen.admin

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.settings.presentation.contract.AdminContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdminSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
): BaseViewModel<AdminContract.Event, AdminContract.State, AdminContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = AdminContract.State(
        adminDebug = false,
        bootToMonitor = false,
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: AdminContract.Event) {
        toggleLoading(true)
        when (event) {
            is AdminContract.Event.DeleteEvents -> deleteEvents()
            is AdminContract.Event.DeleteHistory -> deleteHistory()
            is AdminContract.Event.DeletePellets -> deletePellets()
            is AdminContract.Event.DeletePelletsLog -> deletePelletsLog()
            is AdminContract.Event.FactoryReset -> factoryReset()
            is AdminContract.Event.RebootSystem -> rebootSystem()
            is AdminContract.Event.RestartControl -> restartControl()
            is AdminContract.Event.RestartWebApp -> restartWebApp()
            is AdminContract.Event.RestartSupervisor -> restartSupervisor()
            is AdminContract.Event.ShutdownSystem -> shutdownSystem()
            is AdminContract.Event.SetBootToMonitor -> setBootToMonitor(event.enabled)
            is AdminContract.Event.SetDebugMode -> setDebugMode(event.enabled)
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            settingsRepo.getCurrentServerFlow().collect { server ->
                setState {
                    copy(
                        adminDebug = server.settings.adminDebug,
                        bootToMonitor = server.settings.bootToMonitor,
                        isInitialLoading = false
                    )
                }
            }
        }
    }

    private fun deleteHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.deleteHistory())
        }
    }

    private fun deleteEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.deleteEvents())
        }
    }

    private fun deletePelletsLog() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.deletePelletLogs())
        }
    }

    private fun deletePellets() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.deletePellets())
        }
    }

    private fun factoryReset() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.factoryReset())
        }
    }

    private fun restartControl() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.restartControl())
        }
    }

    private fun restartWebApp() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.restartWebApp())
        }
    }

    private fun restartSupervisor() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.restartSupervisor())
        }
    }

    private fun rebootSystem() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.rebootSystem())
        }
    }

    private fun shutdownSystem() {
        viewModelScope.launch(Dispatchers.IO) {
            handleAdminResult(settingsRepo.shutdownSystem())
        }
    }

    private fun setDebugMode(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setDebugMode(enabled))
        }
    }

    private fun setBootToMonitor(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setBootToMonitor(enabled))
        }
    }

    private suspend fun handleAdminResult(result: Result<Unit, DataError>) {
        toggleLoading(false)
        withContext(Dispatchers.Main) {
            when (result) {
                is Result.Error -> {
                    setEffect {
                        AdminContract.Effect.Notification(
                            text = result.error.asUiText(),
                            error = true
                        )
                    }
                }

                is Result.Success -> {
                    setEffect {
                        AdminContract.Effect.Notification(
                            text = UiText(R.string.alerter_admin_action_completed),
                            error = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        AdminContract.Effect.Notification(
                            text = result.error.asUiText(),
                            error = true
                        )
                    }
                }
            }

            is Result.Success -> {
                settingsRepo.updateServerSettings(result.data)
                withContext(Dispatchers.Main) {
                    setState {
                        copy(
                            adminDebug = result.data.settings.adminDebug,
                            bootToMonitor = result.data.settings.bootToMonitor,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        setState {
            copy(
                isLoading = loading
            )
        }
    }
}