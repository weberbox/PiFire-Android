package com.weberbox.pifire.info.presentation.screens

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
import androidx.navigation.compose.rememberNavController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.screens.DetailsScreen
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.info.presentation.component.LicenseItem

@Composable
fun LicenseDetailsScreen(
    navController: NavHostController,
    args: NavGraph.InfoDest.LicenseDetails
) {
    val context = LocalContext.current
    DetailsScreen(
        title = stringResource(R.string.info_credits),
        navController = navController
    ) {
        items(items = args.licenses.list) { project ->
            LicenseItem(project) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(it.toUri())
                context.startActivity(intent)
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
            LicenseDetailsScreen(
                navController = rememberNavController(),
                args = NavGraph.InfoDest.LicenseDetails(buildLicenseData())
            )
        }
    }
}