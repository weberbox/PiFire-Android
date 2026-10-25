package com.weberbox.pifire.feature.info.presentation.screen

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.components.DetailsScreen
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.feature.info.presentation.components.LicenseItem
import com.weberbox.pifire.navigation.NavGraph
import com.weberbox.pifire.navigation.popBackStackFrom
import com.weberbox.pifire.navigation.rememberSourceEntry

@Composable
fun LicenseDetailsScreen(
    navController: NavHostController,
    args: NavGraph.InfoDest.LicenseDetails,
) {
    val sourceEntry = rememberSourceEntry()
    LicenseDetailsContent(
        args = args,
        onNavigate = { navController.popBackStackFrom(from = sourceEntry) },
    )
}

@Composable
private fun LicenseDetailsContent(
    args: NavGraph.InfoDest.LicenseDetails,
    onNavigate: () -> Unit,
) {
    val context = LocalContext.current
    DetailsScreen(
        title = stringResource(R.string.info_credits),
        onNavigate = onNavigate,
    ) {
        items(items = args.licenses.list) { project ->
            LicenseItem(project) {
                Intent(Intent.ACTION_VIEW).apply {
                    data = it.toUri()
                    context.startActivity(this)
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun LicenseDetailsScreenPreview() {
    PiFireTheme {
        Surface {
            LicenseDetailsContent(
                args = NavGraph.InfoDest.LicenseDetails(buildLicenseData()),
                onNavigate = {},
            )
        }
    }
}
