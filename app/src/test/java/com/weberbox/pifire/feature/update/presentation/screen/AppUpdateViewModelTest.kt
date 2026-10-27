package com.weberbox.pifire.feature.update.presentation.screen

import app.cash.turbine.test
import com.weberbox.pifire.feature.update.data.model.AppUpdatePolicy
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import com.weberbox.pifire.feature.update.data.model.UpdateSource
import com.weberbox.pifire.feature.update.data.repo.AppUpdateRepo
import com.weberbox.pifire.feature.update.presentation.contract.AppUpdateContract
import com.weberbox.pifire.feature.update.presentation.model.UpdateSheet
import com.weberbox.pifire.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppUpdateViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val progress = MutableStateFlow<InstallProgress>(InstallProgress.Idle)
    private val repo = mockk<AppUpdateRepo>(relaxed = true) {
        every { progress } returns this@AppUpdateViewModelTest.progress
        every { source } returns UpdateSource.PLAY_STORE
        every { isSnoozed(any()) } returns false
    }

    private fun viewModel() = AppUpdateViewModel(appUpdateRepo = repo)

    private fun givenPolicy(isRequired: Boolean, update: AvailableUpdate? = AvailableUpdate(versionCode = 120)) {
        val policy = AppUpdatePolicy(latestVersionCode = 120, isRequired = isRequired, message = "Notes")
        coEvery { repo.fetchPolicy() } returns policy
        coEvery { repo.findUpdate(policy) } returns update
    }

    @Test
    fun `up to date shows nothing`() = runTest {
        coEvery { repo.fetchPolicy() } returns null
        val state = viewModel().viewState.value
        assertFalse(state.isRequired)
        assertNull(state.sheet)
        assertFalse(state.isChecking)
    }

    @Test
    fun `a newer optional version is offered`() = runTest {
        givenPolicy(isRequired = false)
        val state = viewModel().viewState.value
        assertEquals(UpdateSheet.OFFER, state.sheet)
        assertEquals("Notes", state.message)
    }

    @Test
    fun `a snoozed version isn't offered`() = runTest {
        givenPolicy(isRequired = false)
        every { repo.isSnoozed(120) } returns true
        assertNull(viewModel().viewState.value.sheet)
    }

    @Test
    fun `a required update blocks the app instead of offering a sheet`() = runTest {
        givenPolicy(isRequired = true)
        val state = viewModel().viewState.value
        assertTrue(state.isRequired)
        assertNull(state.sheet)
    }

    @Test
    fun `putting off the offer snoozes that version`() = runTest {
        givenPolicy(isRequired = false)
        val viewModel = viewModel()
        viewModel.setEvent(AppUpdateContract.Event.OnSheetDismissed)
        verify { repo.snooze(versionCode = 120) }
        assertNull(viewModel.viewState.value.sheet)
    }

    @Test
    fun `a required update starts Play's immediate flow`() = runTest {
        givenPolicy(isRequired = true)
        coEvery { repo.startUpdate(immediate = true) } returns true
        viewModel().setEvent(AppUpdateContract.Event.OnUpdateClicked)
        coVerify { repo.startUpdate(immediate = true) }
    }

    @Test
    fun `when Play can't start the update its listing opens`() = runTest {
        givenPolicy(isRequired = false)
        coEvery { repo.startUpdate(immediate = false) } returns false
        val viewModel = viewModel()
        viewModel.effect.test {
            viewModel.setEvent(AppUpdateContract.Event.OnUpdateClicked)
            assertEquals(AppUpdateContract.Effect.OpenStoreListing, awaitItem())
        }
    }

    @Test
    fun `the github flavor opens the release page`() = runTest {
        every { repo.source } returns UpdateSource.GITHUB
        givenPolicy(isRequired = true)
        val viewModel = viewModel()
        viewModel.effect.test {
            viewModel.setEvent(AppUpdateContract.Event.OnUpdateClicked)
            assertEquals(AppUpdateContract.Effect.OpenReleasePage, awaitItem())
        }
        coVerify(exactly = 0) { repo.startUpdate(any()) }
    }

    @Test
    fun `a finished download offers the restart, and hidden progress stays hidden`() = runTest {
        givenPolicy(isRequired = false)
        val viewModel = viewModel()
        progress.value = InstallProgress.Downloading(fraction = 0.2f)
        assertEquals(UpdateSheet.DOWNLOADING, viewModel.viewState.value.sheet)
        viewModel.setEvent(AppUpdateContract.Event.OnSheetDismissed)
        progress.value = InstallProgress.Downloading(fraction = 0.6f)
        assertNull(viewModel.viewState.value.sheet)
        progress.value = InstallProgress.Downloaded
        assertEquals(UpdateSheet.READY, viewModel.viewState.value.sheet)
    }
}
