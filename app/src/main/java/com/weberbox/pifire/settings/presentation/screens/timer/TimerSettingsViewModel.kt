package com.weberbox.pifire.settings.presentation.screens.timer

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.settings.data.model.remote.Startup.SmartStart.SSProfile
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.contract.TimerContract
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.model.SmartStart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TimerSettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : BaseViewModel<TimerContract.Event, TimerContract.State, TimerContract.Effect>() {

    init {
        collectServerData()
    }

    override fun setInitialState() = TimerContract.State(
        serverData = Server(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: TimerContract.Event) {
        toggleLoading(true)
        when (event) {
            is TimerContract.Event.SetAutoPowerOffEnabled ->
                launchAndHandle { settingsRepo.setAutoPowerOffEnabled(event.enabled) }

            is TimerContract.Event.SetPrimeOnStartup ->
                launchAndHandle { settingsRepo.setPrimeOnStartup(event.amount) }

            is TimerContract.Event.SetShutdownDuration ->
                launchAndHandle { settingsRepo.setShutdownDuration(event.duration) }

            is TimerContract.Event.SetSmartStartEnabled ->
                launchAndHandle { settingsRepo.setSmartStartEnabled(event.enabled) }

            is TimerContract.Event.SetSmartStartExitTemp ->
                launchAndHandle { settingsRepo.setSmartStartExitTemp(event.temp) }

            is TimerContract.Event.SetStartExitTemp ->
                launchAndHandle { settingsRepo.setStartExitTemp(event.temp) }

            is TimerContract.Event.SetStartToHoldPrompt ->
                launchAndHandle { settingsRepo.setStartToHoldPrompt(event.enabled) }

            is TimerContract.Event.SetStartToMode ->
                launchAndHandle { settingsRepo.setStartToMode(event.mode) }

            is TimerContract.Event.SetStartToModeTemp ->
                launchAndHandle { settingsRepo.setStartToModeTemp(event.temp) }

            is TimerContract.Event.SetStartupDuration ->
                launchAndHandle { settingsRepo.setStartupDuration(event.duration) }

            is TimerContract.Event.DeleteSmartStartItem ->
                deleteSmartStartItem(event.smartStartItems)

            is TimerContract.Event.SetSmartStartItem ->
                setSmartStartItem(
                    index = event.index,
                    temp = event.temp,
                    startUp = event.startUp,
                    augerOn = event.augerOn,
                    pMode = event.pMode,
                    smartStartItems = event.smartStartItems
                )
        }
    }

    private fun collectServerData() {
        viewModelScope.launch {
            settingsRepo.getCurrentServerFlow().collect { server ->
                setState {
                    copy(
                        serverData = server,
                        isInitialLoading = false
                    )
                }
            }
        }
    }

    private fun launchAndHandle(block: suspend () -> Result<Server, DataError>) {
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(block())
        }
    }

    private fun setSmartStartItem(
        index: Int,
        temp: Int,
        startUp: Int,
        augerOn: Int,
        pMode: Int,
        smartStartItems: List<SmartStart>
    ) {
        val tempsList = smartStartItems.map { item ->
            item.temp
        }.toMutableList()
        val profilesList = smartStartItems.map { item ->
            SSProfile(startUpTime = item.startUp, augerOnTime = item.augerOn, pMode = item.pMode)
        }.toMutableList()

        if (index != -1) {
            tempsList[index] = temp
            tempsList.removeAt(tempsList.lastIndex)
            profilesList[index] = SSProfile(
                startUpTime = startUp,
                augerOnTime = augerOn,
                pMode = pMode
            )
        } else {
            tempsList[tempsList.lastIndex] = temp
            profilesList.add(
                SSProfile(
                    startUpTime = startUp,
                    augerOnTime = augerOn,
                    pMode = pMode
                )
            )
        }
        val temps = tempsList.sorted()
        val profiles = profilesList.sortedBy { it.pMode }.toMutableList()
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setSmartStartTable(temps, profiles))
        }
    }

    private fun deleteSmartStartItem(smartStartItems: List<SmartStart>) {
        val temps = smartStartItems.map { item ->
            item.temp
        }
            .dropLast(2)
            .sorted()
        val profiles = smartStartItems.map { item ->
            SSProfile(startUpTime = item.startUp, augerOnTime = item.augerOn, pMode = item.pMode)
        }
            .dropLast(1)

        viewModelScope.launch(Dispatchers.IO) {
            handleResult(settingsRepo.setSmartStartTable(temps, profiles))
        }
    }

    private suspend fun handleResult(result: Result<Server, DataError>) {
        toggleLoading(false)
        when (result) {
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    setEffect {
                        TimerContract.Effect.Notification(
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
                            serverData = result.data,
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