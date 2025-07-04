package com.weberbox.pifire.info.presentation.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.screens.DetailsScreen
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.component.PipItem
import com.weberbox.pifire.info.presentation.component.buildModules
import com.weberbox.pifire.info.presentation.contract.ModulesContract

@Composable
fun PipModulesDetailsScreen(
    navController: NavHostController,
    viewModel: PipModulesDetailViewModel = hiltViewModel()
) {
    PipModulesDetailsContent(
        state = viewModel.viewState.value,
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is ModulesContract.Effect.Navigation.Back ->
                    navController.popBackStack()
            }
        }
    )
}

@Composable
private fun PipModulesDetailsContent(
    state: ModulesContract.State,
    onNavigationRequested: (ModulesContract.Effect.Navigation) -> Unit
) {
    AnimatedContent(
        targetState = state,
        contentKey = { it.isInitialLoading or it.isDataError }
    ) { state ->
        when {
            state.isInitialLoading -> InitialLoadingProgress()
            state.isDataError -> DataError {
                onNavigationRequested(ModulesContract.Effect.Navigation.Back)
            }
            else -> {
                DetailsScreen(
                    title = stringResource(R.string.info_pip_modules),
                    onNavigate = {
                        onNavigationRequested(ModulesContract.Effect.Navigation.Back)
                    }
                ) {
                    items(items = state.pipModules) { module ->
                        PipItem(
                            modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
                            module = module,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PipModulesDetailsScreenPreview() {
    PiFireTheme {
        Surface {
            PipModulesDetailsContent(
                state = ModulesContract.State(
                    pipModules = buildModules(),
                    isInitialLoading = false,
                    isRefreshing = false,
                    isDataError = false
                ),
                onNavigationRequested = {}
            )
        }
    }
}