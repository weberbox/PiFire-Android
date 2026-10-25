package com.weberbox.pifire.feature.changelog.presentation.screen

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.feature.changelog.data.model.ChangelogData
import com.weberbox.pifire.feature.changelog.data.repo.ChangelogRepo
import com.weberbox.pifire.feature.changelog.presentation.contract.ChangelogContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChangelogViewModel @Inject constructor(
    private val changelogRepo: ChangelogRepo,
) : BaseViewModel<ChangelogContract.Event, ChangelogContract.State, ChangelogContract.Effect>() {

    init {
        getChangelogData()
    }

    override fun setInitialState() = ChangelogContract.State(
        changelogData = ChangelogData(),
        isInitialLoading = true,
        isDataError = false,
    )

    override fun handleEvents(event: ChangelogContract.Event) {}

    private fun getChangelogData() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = changelogRepo.getChangelogData()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        setState { copy(isInitialLoading = false, isDataError = true) }
                        setEffect {
                            ChangelogContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true,
                            )
                        }
                    }

                    is Result.Success -> {
                        setState {
                            copy(
                                changelogData = result.data,
                                isInitialLoading = false,
                                isDataError = false,
                            )
                        }
                    }
                }
            }
        }
    }
}
