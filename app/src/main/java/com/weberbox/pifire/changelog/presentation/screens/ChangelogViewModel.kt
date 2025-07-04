package com.weberbox.pifire.changelog.presentation.screens

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.changelog.data.repo.ChangelogRepo
import com.weberbox.pifire.changelog.presentation.contract.ChangelogContract
import com.weberbox.pifire.changelog.presentation.model.ChangelogData
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChangelogViewModel @Inject constructor(
    private val changelogRepo: ChangelogRepo
) : BaseViewModel<ChangelogContract.Event, ChangelogContract.State, ChangelogContract.Effect>() {

    init {
        getChangelogData()
    }

    override fun setInitialState() = ChangelogContract.State(
        changelogData = ChangelogData(),
        isInitialLoading = true,
        isDataError = false
    )

    override fun handleEvents(event: ChangelogContract.Event) {}

    private fun getChangelogData() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = changelogRepo.getChangelogData()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        setEffect {
                            ChangelogContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true
                            )
                        }
                    }


                    is Result.Success -> {
                        setState {
                            copy(
                                changelogData = result.data,
                                isInitialLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}