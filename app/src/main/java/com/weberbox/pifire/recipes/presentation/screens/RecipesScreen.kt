package com.weberbox.pifire.recipes.presentation.screens

import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.EmptyItems
import com.weberbox.pifire.common.presentation.component.HazeSearchAppBar
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.PullToRefresh
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.CachedDataError
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.clearBackStackArg
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.common.presentation.util.getBackStackArg
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.recipes.presentation.component.RecipeItem
import com.weberbox.pifire.recipes.presentation.component.RecipesInputField
import com.weberbox.pifire.recipes.presentation.component.RecipesSearchResults
import com.weberbox.pifire.recipes.presentation.contract.RecipesContract
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Asset
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.MetaData
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Step
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun RecipesScreenDestination(
    navController: NavHostController,
    viewModel: RecipesViewModel = hiltViewModel(),
) {
    navController.getBackStackArg<Boolean>("refresh")?.also { refresh ->
        if (refresh) {
            viewModel.setEvent(RecipesContract.Event.Refresh)
            navController.clearBackStackArg<Boolean>("refresh")
        }
    }

    RecipesScreen(
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            when (navigationEffect) {
                is RecipesContract.Effect.Navigation.Back ->
                    navController.popBackStack()

                is RecipesContract.Effect.Navigation.RecipeDetails ->
                    navController.safeNavigate(
                        NavGraph.RecipesDest.Details(
                            filename = navigationEffect.filename,
                            step = -1
                        )
                    )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
private fun RecipesScreen(
    state: RecipesContract.State,
    effectFlow: Flow<RecipesContract.Effect>?,
    onEventSent: (event: RecipesContract.Event) -> Unit,
    onNavigationRequested: (RecipesContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val windowInsets = WindowInsets.safeDrawing
    val hazeState = rememberHazeState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is RecipesContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is RecipesContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }
            }
        }?.collect()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HazeSearchAppBar(
                searchBarState = searchBarState,
                hazeState = hazeState,
                searchResults = {
                    RecipesSearchResults(
                        recipeData = state.recipesList,
                        searchQuery = textFieldState.text,
                        onResultClick = { result ->
                            textFieldState.setTextAndPlaceCursorAtEnd(result)
                            searchQuery = result
                        }
                    )
                },
                inputField = {
                    RecipesInputField(
                        searchBarState = searchBarState,
                        textFieldState = textFieldState,
                        hazeState = hazeState,
                        searchStyle = HazeMaterials.ultraThin(
                            MaterialTheme.colorScheme.surfaceContainerHigh).copy(noiseFactor = 0f),
                        onNavigate = {
                            onNavigationRequested(RecipesContract.Effect.Navigation.Back)
                        },
                        onSearch = { result ->
                            scope.launch {
                                searchBarState.animateToCollapsed()
                                searchQuery = result
                            }
                        },
                        onSearchClear = {
                            textFieldState.clearText()
                            searchQuery = ""
                        }
                    )
                }
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets,
    ) { contentPadding ->
        AnimatedContent(
            targetState = state,
            contentKey = { it.isInitialLoading or it.isDataError }
        ) { state ->
            when {
                state.isInitialLoading -> InitialLoadingProgress()
                state.isDataError -> CachedDataError {
                    onEventSent(RecipesContract.Event.Refresh)
                }

                else -> {
                    val filteredList = remember(searchQuery, state.recipesList) {
                        state.recipesList.filter { item ->
                            item.metadata.title.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    PullToRefresh(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onEventSent(RecipesContract.Event.Refresh) },
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding,
                    ) {
                        LazyColumn(
                            contentPadding = contentPadding,
                            verticalArrangement = Arrangement.spacedBy(
                                MaterialTheme.spacing.extraSmallOne
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .hazeSource(state = hazeState)
                                .padding(
                                    horizontal = MaterialTheme.spacing.smallOne,
                                    vertical = MaterialTheme.spacing.extraSmallOne
                                )
                                .fillMaxSize(),
                        ) {
                            itemsIndexed(filteredList) { _, item ->
                                RecipeItem(
                                    details = item,
                                    onRecipeClick = {
                                        onNavigationRequested(
                                            RecipesContract.Effect.Navigation.RecipeDetails(
                                                filename = item.recipeFilename
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = filteredList.isEmpty(),
                        enter = fadeEnterTransition(),
                        exit = fadeExitTransition()
                    ) {
                        EmptyItems(
                            title = stringResource(R.string.error_no_results_recipes_title),
                            summary = stringResource(R.string.error_no_results_recipes_summary),
                            icon = Icons.Filled.NoFood
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
//@DeviceSizePreviews
private fun RecipesScreenPreview() {
    PiFireTheme {
        Surface {
            RecipesScreen(
                state = RecipesContract.State(
                    recipesList = buildRecipe(),
                    isInitialLoading = false,
                    isRefreshing = false,
                    isDataError = false

                ),
                effectFlow = null,
                onEventSent = {},
                onNavigationRequested = {}
            )
        }
    }
}

internal fun buildRecipe(): List<Recipe> {
    return List(5) {
        Recipe(
            assets = listOf(
                Asset(
                    filename = "3f2dc267-aa27-11ed-a7f4-97f849ebe8ec.jpg",
                    id = "3f2dc267-aa27-11ed-a7f4-97f849ebe8ec",
                    encodedThumb = recipeThumb,
                    encodedImage = recipeThumb
                ),
                Asset(
                    filename = "3f2dc267-aa27-11ed-a7f4-97f849ebe8ec.jpg",
                    id = "3f2dc267-aa27-11ed-a7f4-97f849ebe8ec",
                    encodedThumb = recipeThumb,
                    encodedImage = recipeThumb
                )
            ),
            metadata = MetaData(
                author = "James Weber",
                rating = 3,
                title = "Simple Pulled Pork",
                cookTime = 360,
                prepTime = 240,
                difficulty = "Easy",
                description = "This is a description",
                thumbnail = "3f2dc267-aa27-11ed-a7f4-97f849ebe8ec.jpg",
                image = "3f2dc267-aa27-11ed-a7f4-97f849ebe8ec.jpg"
            ),
            recipeFilename = "Test",
            steps = listOf(
                Step(
                    holdTemp = 0,
                    message = "",
                    mode = "Startup",
                    notify = false,
                    pause = false,
                    timer = 0,
                    triggerTemps = Step.TriggerTemps()
                ),
                Step(
                    holdTemp = 420,
                    message = "Time to wrap the pork and return it to the smoker.",
                    mode = "Startup",
                    notify = false,
                    pause = false,
                    timer = 0,
                    triggerTemps = Step.TriggerTemps()
                ),
            )
        )
    }
}

internal const val recipeThumb =
    "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofH" +
            "h0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIy" +
            "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCACAAIADASIAAhEBAxEB/8QA" +
            "HwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUE" +
            "GE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmN" +
            "kZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NX" +
            "W19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtRE" +
            "AAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRo" +
            "mJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoq" +
            "OkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRA" +
            "xEAPwDB1LRptJ17ybovcR+aqTSRZRVZuMFgc8n09PerljpbvujnOVWYLvhJYEdj+Wag1XVZLm+lnm80RSSl5" +
            "grB0xt2qwxzkH1Het3RdYbQ7W7k2xzWcyZJc4KnjBx6nPFcpqaWqWVhF4YECLFGYXLNJFgn5sfdIxgY6g/1r" +
            "B02yTVWWGyiQ7YZd8GCd0f3v546+orN8W+Nbe4/s422iXH2cuWkaV9qzEAEDgdBjJ9a53w74612y1d71fLe" +
            "KWRmkRl4GeoH6GnZ7gju/wCyLiz0Y3qFEiclEDDkADPOPpn9K4aG6hgvomGpy4j2kwhSVBPXbngEHpVO91S" +
            "/1Ngbm5eSNclYwfkUk8gD86gVYvKBUsuPwzUcpdrHpK63pMVm9pY6jZQySg77k2rGUHvk8Dnp/wDrq3oV9" +
            "batFsa4eeeIDgoDG4zy23nBHbuc9xXk8meQAeR61IskkW2SJzGyjhgSCPxo5QselS6PFJJJGEknRQ0RMUZx" +
            "MpyMY42levAOaa3he2bSw9jZXs0q7QHYGMc8AA+vPfpXEWfi/X9Il22mp3HlkqzRO29W24xkGu/0j4v2RiiT" +
            "VtIkkkRMZhwIy3I3bevQn86iMGtxyfYuzWmuT6U0rI0c6kZZ8uzbiRt2ng45wffPNZEugCxiQWd48kJKFkg" +
            "QFlUr95R/F054GD7V1zeKLTX7FILCZdynzMFcoigDG4nnOev0pNH06DWZJdRd4vta7VL2YKIjDPHJ5BGPyNU" +
            "lYV+55SmnFrq80q6bz43XdBLuH+sUbl+b3GQcdzUa6dbRSWmuSKsmnwQPLNbScnzAcCM54OWI/WvUtR0OHU4" +
            "I5r2a1hl85t8zINpRQ3KjPYHHvx6VyVzpJisJrO5tomgtxJc3IikZRGGcBMHuCMHqepqotoT1LWkeILl3tI3" +
            "KyQy/KS21Sg6AjHuMc84Ga6gSZL+RMGwcN5bZx9a88k12xl0qXT0hFqnmLILgfO4xwR68+pz0p982+NGl/eGy" +
            "2p9ptpfKuGXry3G7jgZHFaKRLQ7Rrea7Bjt7aS4dpPkVQZOM54HbBHU9jXX3Vpb6hpLPczxCaSPy2CxhRGw5" +
            "A6DByOv61ykusaTpOkQNvuA0znYqDJVc9SQcAn6Vh6x4re8gmZ5pI5pIFWLB4K5OWx64wPzrMdiDXdWg1drO" +
            "1sopYLSxjePBk3Zdj87A45zxgduapx2QaH5cgnGAT2rFlvJrS0RQyIT90IuMfhVvTra9uP8AW3jKVBbywefW" +
            "qa0uXFF+SPycH7rD260yNoxLgEZJ6DnFXVjktYMSozoMkt95cZ6g9R+NMNmyMCqg5BbjqKSHLciaIPh2Krnj" +
            "1BomUREiRup7dPf8aJtzyCM7m2kMQB27D3qKYb2DgE46r2yetK1wvYgYRK7Fdx+vNRZyMAndn0/zxU/DrgkH" +
            "07VEFw25TtIPX29KpEk9jqN7pV4slvIydmx0bBzhh3rs/B3jqXT9Xg07ylWwvJAlw7ncwyCBt9Mbq4WTLgn" +
            "v04XrioYuJdysylTng4I+lK10DPoi31VbjToNPa3NxtuPLieRBwQD8vPsvXv1rktXGqXcF1PcQoY0ykUNm+4" +
            "qRnh8ZyoA6etV/hv4paWW6sb+VZnQLNCJRxhAQRngk8g49M162tvpENnJHBax28jtmRkVQR3yccfh9aIroQ3" +
            "Y8ETy3iiS4lIUtuBHQbumf0qwHeV/sVwwjgVljLAblwDlm69Nvv2r0nxL8P012/nm0w2ttHKkbBi2wI67skK" +
            "BxnIrCk8C6lqCvLY/Z4jNDnMpxjHBB+oxz9elNxGpHCeIm3wCDy2WJ5PkYn5hgDP1+vsazrRbS8uRFIscEcc" +
            "TQllfHnKecnj8OKsahNc6oEd4kiMJKOx+9IwJySB0A6DHvUOpXMMdpb7LURXSBtspf7pweOvQ+grnqJtWT1Nq" +
            "bSeqMPV7K2uNXSC2YRKuIyQCVyOCRnnNOs3e2ugsxAaJth55OO59qpyaidRnzKpMhfeH7lu+frxVzVofNeO" +
            "dEkYooRm4AHUnPfvW0LxioyCUU23E6oyfvDJb4KhM7+oz0xUlnoYv18u2vvLcDcYXXdz7H0PpXK2l3Klsiq" +
            "/8R2qW/lWsNRkDwTeZJBISCCDg54wR7GlODa912Y4SS+JXRe1nTrnTm33MGxeArqCw/PtWO0zbCpjIO3AOO" +
            "c8/hjpzXap4le6t/sN2FmjlXYzYGR71UbwpG9wsUN5sUruO+LgD1zmso1PZ+7U3KdPn96GxyLwMeTz8vden" +
            "vUIRtv7zOznJC13dv8OLxgJG1SJoyMI0S53DPWnDSPDuluY7rUEeSMfvEkYHJ/z2p/WY9NSVS8zgAAVHVccd" +
            "eajUBS3zfOe2e1aWtS2L3ckdhIHiJJztC/h9Kx1YzMEICkHjNbxbkrkyiouxNDNPaObi3d0kj+ZGU4IYc5r2" +
            "b4dfERtY0I2l2HF9bKfPeNdpkQnAckd+gPFeQWltLdTpBaqWkZwMBcgDucVc8O6ZqLaxPbadqzafaMrO8rDD" +
            "qB0XpkZPHHB71MuXe9mQ0e8af4m01vKkeHBJYPJO+SnPy/KB8xPPpW0dYYv59qhVVGwAoBn1ODj07V43a2W" +
            "pwX8csl3LMmxkMjOTF5n9/s3fPTqOtXkudZg04GLVp2MWIRuXcJNpHzZI44/OojNg4o5nw051C9WB5EjbPO8" +
            "EknPPFU/F+oRwyfZLaNWht7uTy2IIbHGOe4+vpWjdRSTwG605QkkeW2bRubOeM9+pritWu73UI4hMztChIGO" +
            "7Y5NJRTki1cpwxyNL5oxneDuHHOa3Lu8Emm3CMSzqwJfcSfQise3fbGqIpDAfePGK1Uuy8Ji8hJZJCCfMbG8" +
            "jpyen+c1c1qaR0RNo2jPqEbtdbo7cqBGwAzkcg49Kr6lZ3NndkOPNhbIViOMe3oa1LHUGuo/lbp97tz6EdjX" +
            "WwaZZarpDRlhuIyG7qaxqVnTn72xt7KLp3izkPDetW+n30kd/DI6mPbEUXcS38s1rRTW0Nxb3FwZJ5hy9uh" +
            "wpx2J9PbFYV1ZPoerKl4VWE5IYHCsQD3xS290t1bJI6pHKPvIeij1rT2cKj5jBTlBcp1eu+Nr28RLewWOzRD" +
            "jbj53GP4T6D2riZ5gZSWJ3n+da4LXNoFmVWDA5ZDxj2NZ0USteeTLL5idA+MH6H8utVGEYLQjmbKDQGQqwP" +
            "J7+9acGi3xgM3kEEjCkEZH1Fdrofgixngi1FLlmVxvQAgitW50ieCPevlvgEDnGK5quKs7RN6VGL1kzgtNv" +
            "Ro0cxuraUXMxwGRsHFUomgj8QW7TTv5IxLMEchuMtyep7Vv63aRRtG123AGWIboBz/SuV0fSLvxNqrTW7BA" +
            "ZCxYk/J9cc4xW0GmnNkVocj5Uz1Oyis9WEcdhJGElZvJmLHb5jY+Vu27kVt3Wm2fhDTJZ7p2nkcKvl7iY8k" +
            "Eg9SOx6DtWf4X0dtBtpQZI1ncoWaCDcGwuAAG7nJJxjp0FdDrl0NT0ZPNIBMoBV4s+Wcdz6ZOeOlTGUXsYtM" +
            "8/mtZUnV44FUhwzIDgYzztrjNWR1VtQt/3kTsyyxyJypyckZ6j3r2Oxh029SO+iZbzTpwRhTjacdx2I7iuH1" +
            "jSVt7wx2shaOeYqYmOV3AHPXnn070RmnqVtojzH7QjSDK7e33ulPlcHGxyT1JFX/Efh6XTGF3EoNtIxAA6p7" +
            "Edj1/KsSKQd66bJq6HGXRmrFcM6LLFsiuIVwQoIMwz1PbIz+VW7DxLc2cgbayOPvgfdf8ADsaxuSMrn6jtU" +
            "kcizqsMjAMDhXP9alxTVminzLWLPRbObT/E+nqL2ZGIPKDAZT9SeKpz6C1kxuIIlljRCFQN8+B0PvXKWujGK" +
            "+kt5HU45BjkDKw9iDg1PcC50K6URXFwIJRkKHwM1zxp8knGEvkaOSlFSmjrdOsF1TTRJayRiYL8yqTj8c96" +
            "wYdLv7a5mnuYdgjkA45/OrGiajYJcJdW928Nwfvwynhye+R7100eo6WQzXU5u3ncKyAAbcdT71LnUhK26G6" +
            "cZR5jI0rxLf8Ah+MpCqTWTMzeW3BB74NVdW8VahqiyROTBA38KjPHufek1Z7e1leO2kDwO3y5PKn0P19a5+" +
            "WZtwRgFxkZNbQpwm+exhNyh7tyDUr3bpwtIwSXfqOx9K9D8GWQ0nRYJRawyF8uXM4BLEYIxnnkYFeeQw/ar" +
            "23hRR+7PmNu6E5wBjufavRdOnhj1SCzitrlliQ7gsyooXPLE4OSx59806zskkZx11Z2sCQanJBJLeT2zw8j" +
            "aTsY46bP4jk9aZqurLb4khkWOIELPkBunQ/XsaomaC8kU2d9FawAhXafrGFOCV65Of8A61F3qOmzyLp9pGb+" +
            "BC2ZXg2+Xu5wOeT1rKMdLobZ55pOual4Xv3ntAxhztubSUYz7MOx9CK7KW407XrS41O0lYxXBSNoAMvCxH" +
            "zBh+GQa6/xd4FtdeVrq3xbaiowsoHDezDuK8kSzuvD+sS29yDZzONrg52OM5yp/DPrXROmpO/UiMmtB9urw" +
            "WbWDguWdiu9M7WxgdetcxqWhg5mslZW6+SRxIPVP/if5121xDBqN6J0inWykj3NIq5AbHLD2yOnqD61aj0" +
            "uS70lkS3W5gjculwnGDjt3Hr3FZptM0drHksUnIqwsUcx5ypPTHrXQa9oMjyNKts8F0fmAyCs6+oI43evr" +
            "7VzTCa3kMcyMjKcYIxW2+xcJdGXbeC4tmV4Zl8wNkI3Ab2rqHht/EViLd2MF1GcBX+Uqe4Irj5HE0KDcd6" +
            "+p4Iq7a6jNvj+0OzPGNsc2fmQZ6H1H1/Cs5w5tVujRPl03TM2WB7W7eGTIZHK9OeOKtwTtBKrlvkY9c1p6o" +
            "Euy7zlGn24ikjbMbjcPbr161iog8xo0Z2fdhoyvIrRO61MGuV6G68CS2bOGYhzjd2GO1ZxDCI5G7PTB/nVi" +
            "wu1jUq7oTnGw8VUvrhru6jtbNf3kjBAqc8k4pRvew5uLjcSzFzPq8Yso5JJYRuIhQvggdcD09a9O8KotxZ+" +
            "ZPAlpFET5nnSMjPgZB3EcjOePer3gXQrvQrC4tHt7BTONrmQ5d8c8kHK+1dXZadZSNd/2teE27wEeUw3BfR" +
            "vfB9KmVpPQyTsjnriztJYbf7JNADiTEX3RnAwMfU5zz7g0k0d35McTJIZZDwtuQOnc8CtcaWrWFz5Ytn8lx" +
            "5Tg4yeQ2cjrgH8qzNa1v8AsyR4JLb/AEm2BUEn5JpOg2gHkEAHFEUDZ6kWLctyfWsXX/Dlhr9mYLyFWP8AA+" +
            "OVNbFKOa2aMzyefSo9De4t52mt1T/V94ZOM8EjjPpWHbax/Y2qz25LRw3asyLbdPl6nHPGD29K9h13TP7T0" +
            "me2HDlcocdGFeE3ui2tvqUdypmyFbZIkm3awyG49R14rFq0jWLujp47xJbZLXUbeM2u7zFfbkuD06dDjjj0" +
            "rK1jwhMkJ1q1EV7YsR5kBUblXpn0P4YNVbMyxhrZnklmCDcrYw6dmAH8Q459jmupsfst9Eot7t7U7Qs8Ur" +
            "/NkdgD1H1qbDvY8l1HSYUnf7FOGVcdVKj8jyDWfJHNCMSxkDoG6g/jXtV74Ttb+WJpmiRFbMrwKGZwen0OP" +
            "5jjirNj4D0VpEUSzyGVmUxPgAjPQnA/MelWm7DU7HiFrfS2W8KAyupUq/Sp7eVUilUjZOTkPu7Y5+ueMV6D" +
            "4j+EU9hI0ljdxtBnJWT5SnfGOc8fSoNK+FcV7bQ3kmqxrCCfMilYIzHPAU84HTr1/Wjmjew+a+p55b20+p" +
            "6gkMYWJifmkwcAf3jXrWjeGdN8P6bLfIM3akKswIdVkG7G3PuOv5Guo8M6Fo9ppMscMNrHIyBZRkfvRyeSO" +
            "ePr61QvN7xxWEdnBLCGHFictMAOhAyc+/bmhyuQWIplg01Lia1Es4Ub5Y8jPY9u2DzUhvriSwSK5jEhkjd" +
            "dnG6M88BuOo59OazLqZU8QLYSLOstgqvMDKMKrAdcdducnOevaslpdR1i8htNOZp5rqURRq/CIB3PpjrnPT" +
            "1qVFyE2kben+TBqhe8usrFFuVcgHcO2DyTz06dDms7VXMyW8ItCkkm0u7YURuTwFznIBA6VvarZLYwWrB1u" +
            "IlGWcgxtvYYLc9+vPb8KgfRTHaRyT2oltZVUId+4QDJ+ZCDySfr0rZRadmRe+p0+ia2s8KJLIHVgCkoOQRX" +
            "QA9wa+bfDPi+bQpvst5uk06Q9OpiPqPUeor27RtcjkgiJlEtu4BjlByMURkDR07OG5Iw3cjvXkvjWwGl69J" +
            "K64tbxvOjKL0YAbgfx5/GvVQwIyDkGqWqaVZa1YtZ30Ikibkc4ZT6g9jROPMgi7M8IRZY5gkTQyW0S5ilU" +
            "sZA2c457Yz9RWzYyW+oRoPuXmcfaGbyyFPJC8DK+/tXoNt8MNDTbJBLemZB84Mo+cevSuX8VeCJdM0xprC+" +
            "PkCUMUdPmj57MOx79KxcGjRSTM6OPVbOSVUdX6u3mDJXjPB75HpTF165dwyXH2UqTw6t8x9FI6GobvVNU0r" +
            "7GtzZDyp1HlM0I28cct0x2qO4ZdSZWdPJeBAV8pgxbqcYXk/zo6DNK71DULm2lSTUFuIQc+UiM25u3XB9fy" +
            "pvhvxKLLct1ZpIjSGJvlwF6HsOeM8VDZTrFJFDND9qt3YJKNpPmA4OBgcAH1PbpUmt6La32pS/2LFNBpzxr" +
            "5iTk5OOpU98HPNJJPUTNXV7FdNWW5sp4Jre4XcqROMxkgkggdvf8Khm1zRrS1s0e6tFeUDe1qjecgAxyew5" +
            "5POfeoU8KRrZtbQX0kdtCp/fmX/W5/2cHOTVX/hXTSC2Mk8bvMhkhiRSHkGMkYPBH4jGPwrRIkraNdaVd+K" +
            "5muBdCYZWVi28Mu0gdOoHB7Vs3OmXuhaxHHb2kD+SySxSCRsEdSM+44rotP8ACsfhjRYtUWwhaeGIbrYSff" +
            "B+88jdSOvygdu9OsE0rxZdm3RFS7bMsLRPhFHZQMduPc1pZRsTe4lncSX9xbLd2zZVGj3SSYYnPy5H3T164o" +
            "1vRodPs3uYZGiaVvJOF4TnJPHQGtK4j0/UttvZwfZ5lcpNs7MuepzjJOaqrZzWFtLJqU93fK5dmibLGIEYU" +
            "7e5Hp+PpVxknv0JafQ//9k="
