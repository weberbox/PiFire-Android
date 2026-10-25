package com.weberbox.pifire.feature.events.presentation.screen

import app.cash.turbine.test
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.events.EventsData.Events.Event
import com.weberbox.pifire.feature.events.data.repo.EventsRepo
import com.weberbox.pifire.feature.events.presentation.contract.EventsContract
import com.weberbox.pifire.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EventsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<EventsRepo>()
    private val live = listOf(Event(id = "live"))
    private val cached = listOf(Event(id = "cached"))

    private fun viewModel(): EventsViewModel {
        coEvery { repo.listenEventsData() } returns emptyFlow()
        return EventsViewModel(eventsRepo = repo)
    }

    @Test
    fun `a successful load shows the events`() = runTest {
        coEvery { repo.getEvents() } returns Result.Success(live)
        val state = viewModel().viewState.value
        assertEquals(live, state.eventsList)
        assertFalse(state.isInitialLoading)
        assertFalse(state.isDataError)
    }

    @Test
    fun `a failed first load falls back to the cache and says so`() = runTest {
        coEvery { repo.getEvents() } returns Result.Error(DataError.Network.SOCKET_TIMEOUT)
        coEvery { repo.getCachedData() } returns Result.Success(cached)
        val viewModel = viewModel()
        assertEquals(cached, viewModel.viewState.value.eventsList)
        assertFalse(viewModel.viewState.value.isDataError)
        viewModel.effect.test {
            assertTrue(awaitItem() is EventsContract.Effect.Notification)
        }
    }

    @Test
    fun `no data and no cache is a data error, with loading done`() = runTest {
        coEvery { repo.getEvents() } returns Result.Error(DataError.Network.SOCKET_TIMEOUT)
        coEvery { repo.getCachedData() } returns Result.Error(DataError.Local.NO_CACHED_DATA)
        val state = viewModel().viewState.value
        assertTrue(state.isDataError)
        assertFalse(state.isInitialLoading)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `a failed refresh notifies and stops refreshing without using the cache`() = runTest {
        coEvery { repo.getEvents() } returns Result.Success(live)
        val viewModel = viewModel()
        coEvery { repo.getEvents() } returns Result.Error(DataError.Network.SOCKET_TIMEOUT)

        viewModel.setEvent(EventsContract.Event.Refresh)

        viewModel.effect.test {
            assertTrue(awaitItem() is EventsContract.Effect.Notification)
        }
        assertFalse(viewModel.viewState.value.isRefreshing)
        assertEquals(live, viewModel.viewState.value.eventsList)
        coVerify(exactly = 0) { repo.getCachedData() }
    }

    @Test
    fun `live updates replace the list and clear a data error`() = runTest {
        coEvery { repo.getEvents() } returns Result.Error(DataError.Network.SOCKET_TIMEOUT)
        coEvery { repo.getCachedData() } returns Result.Error(DataError.Local.NO_CACHED_DATA)
        coEvery { repo.listenEventsData() } returns flowOf(Result.Success(live))
        val state = EventsViewModel(eventsRepo = repo).viewState.value
        assertEquals(live, state.eventsList)
        assertFalse(state.isDataError)
    }
}
