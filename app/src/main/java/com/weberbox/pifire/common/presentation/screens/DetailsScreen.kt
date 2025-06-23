package com.weberbox.pifire.common.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.gradientBackground
import com.weberbox.pifire.common.presentation.component.LinearLoadingIndicator
import com.weberbox.pifire.common.presentation.component.TransparentAppBar
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.component.LicenseItem
import com.weberbox.pifire.info.presentation.screens.buildLicenseData

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetailsScreen(
    title: String,
    navController: NavHostController,
    isLoading: Boolean = false,
    content: LazyListScope.() -> Unit
) {
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground())
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TransparentAppBar(
                title = {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = title
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { navController.popBackStack() }
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets,
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = MaterialTheme.spacing.smallOne,
                    vertical = MaterialTheme.spacing.extraSmall
                )
        ) {
            content()
        }
        LinearLoadingIndicator(
            isLoading = isLoading,
            contentPadding = contentPadding
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun DetailsScreenPreview() {
    PiFireTheme {
        Surface {
            DetailsScreen(
                title = stringResource(R.string.info_credits),
                navController = rememberNavController()
            ) {
                for (license in buildLicenseData().list) {
                    item {
                        LicenseItem(license) {}
                    }
                }
            }
        }
    }
}