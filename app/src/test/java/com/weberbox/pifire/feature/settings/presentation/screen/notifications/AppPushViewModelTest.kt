package com.weberbox.pifire.feature.settings.presentation.screen.notifications

import app.cash.turbine.test
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.push.PushDevice
import com.weberbox.pifire.core.repo.PushRepo
import com.weberbox.pifire.feature.settings.presentation.contract.AppPushContract
import com.weberbox.pifire.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppPushViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<PushRepo>()
    private val pixel = PushDevice(id = "a1", deviceName = "Pixel", appVersion = "3.4.0", registered = 1, isThisPhone = true)
    private val galaxy = PushDevice(id = "b2", deviceName = "Galaxy", appVersion = "3.4.0", registered = 2, isThisPhone = false)

    @Test
    fun `loads the server's phones`() = runTest {
        coEvery { repo.getServerDevices() } returns Result.Success(listOf(pixel, galaxy))

        val state = AppPushViewModel(pushRepo = repo).viewState.value

        assertEquals(listOf(pixel, galaxy), state.devices)
        assertFalse(state.isInitialLoading)
        assertFalse(state.isLoading)
        assertFalse(state.isDataError)
    }

    @Test
    fun `a failed first load is a data error with loading done`() = runTest {
        coEvery { repo.getServerDevices() } returns Result.Error(DataError.Network.SOCKET_ERROR)

        val viewModel = AppPushViewModel(pushRepo = repo)

        assertTrue(viewModel.viewState.value.isDataError)
        assertFalse(viewModel.viewState.value.isInitialLoading)
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `a failed refresh keeps the list and reports the error`() = runTest {
        coEvery { repo.getServerDevices() } returns Result.Success(listOf(pixel))
        val viewModel = AppPushViewModel(pushRepo = repo)
        coEvery { repo.getServerDevices() } returns Result.Error(DataError.Network.SOCKET_ERROR)

        viewModel.effect.test {
            viewModel.setEvent(AppPushContract.Event.Refresh)
            assertTrue((awaitItem() as AppPushContract.Effect.Notification).error)
        }
        assertEquals(listOf(pixel), viewModel.viewState.value.devices)
        assertFalse(viewModel.viewState.value.isDataError)
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `deleting a phone removes it and reloads the list`() = runTest {
        coEvery { repo.getServerDevices() } returns Result.Success(listOf(pixel, galaxy))
        val viewModel = AppPushViewModel(pushRepo = repo)
        coEvery { repo.removeServerDevice("b2") } returns Result.Success(Unit)
        coEvery { repo.getServerDevices() } returns Result.Success(listOf(pixel))

        viewModel.effect.test {
            viewModel.setEvent(AppPushContract.Event.DeleteDevice("b2"))
            assertFalse((awaitItem() as AppPushContract.Effect.Notification).error)
        }
        coVerify { repo.removeServerDevice("b2") }
        assertEquals(listOf(pixel), viewModel.viewState.value.devices)
    }

    @Test
    fun `a failed delete keeps the list and ends loading`() = runTest {
        coEvery { repo.getServerDevices() } returns Result.Success(listOf(pixel, galaxy))
        val viewModel = AppPushViewModel(pushRepo = repo)
        coEvery { repo.removeServerDevice(any()) } returns Result.Error(DataError.Server("unknown_device"))

        viewModel.effect.test {
            viewModel.setEvent(AppPushContract.Event.DeleteDevice("b2"))
            assertTrue((awaitItem() as AppPushContract.Effect.Notification).error)
        }
        assertEquals(listOf(pixel, galaxy), viewModel.viewState.value.devices)
        assertFalse(viewModel.viewState.value.isLoading)
    }
}
