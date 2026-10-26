package com.weberbox.pifire.feature.setup.presentation.screen

import app.cash.turbine.test
import com.weberbox.pifire.feature.setup.data.util.ServerDataCache
import com.weberbox.pifire.feature.setup.presentation.contract.SetupContract
import com.weberbox.pifire.navigation.NavGraph
import com.weberbox.pifire.testing.MainDispatcherRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SetupViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val serverDataCache = mockk<ServerDataCache>(relaxed = true)

    @Test
    fun `exit clears the setup cache and goes back to the server list`() = runTest {
        val viewModel = SetupViewModel(serverDataCache = serverDataCache)

        viewModel.setEvent(SetupContract.Event.Exit)

        viewModel.effect.test {
            assertEquals(
                SetupContract.Effect.Navigation.NavRoute(
                    route = NavGraph.LandingDest.Landing(false),
                    popUpToRoute = NavGraph.ServerSetup,
                ),
                awaitItem(),
            )
        }
        verify { serverDataCache.clearServerData() }
    }
}
