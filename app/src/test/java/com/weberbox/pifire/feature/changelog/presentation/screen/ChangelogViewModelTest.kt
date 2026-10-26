package com.weberbox.pifire.feature.changelog.presentation.screen

import app.cash.turbine.test
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.feature.changelog.data.model.ChangelogData
import com.weberbox.pifire.feature.changelog.data.repo.ChangelogRepo
import com.weberbox.pifire.feature.changelog.presentation.contract.ChangelogContract
import com.weberbox.pifire.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ChangelogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<ChangelogRepo>()

    @Test
    fun `a loaded changelog is shown`() = runTest {
        val data = ChangelogData(changelog = listOf(ChangelogData.Changelog(version = "3.4.0")))
        coEvery { repo.getChangelogData() } returns Result.Success(data)
        val state = ChangelogViewModel(changelogRepo = repo).viewState.value
        assertEquals(data, state.changelogData)
        assertFalse(state.isInitialLoading)
        assertFalse(state.isDataError)
    }

    @Test
    fun `a failed load ends loading, flags the error and notifies`() = runTest {
        coEvery { repo.getChangelogData() } returns Result.Error(DataError.Local.JSON_ERROR)
        val viewModel = ChangelogViewModel(changelogRepo = repo)
        assertFalse(viewModel.viewState.value.isInitialLoading)
        assertTrue(viewModel.viewState.value.isDataError)
        viewModel.effect.test {
            assertTrue(awaitItem() is ChangelogContract.Effect.Notification)
        }
    }
}
