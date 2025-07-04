package com.weberbox.pifire.setup.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.getPagerAnimationSpec
import com.weberbox.pifire.common.presentation.modifier.limitWidthFraction
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.setup.presentation.component.SetupBottomNavRow
import com.weberbox.pifire.setup.presentation.model.SetupStep
import kotlinx.coroutines.launch

@Composable
fun TermsScreenDestination(
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()
    TermsScreen(
        onNextClick = {
            scope.launch {
                pagerState.animateScrollToPage(
                    page = SetupStep.Auth.ordinal,
                    animationSpec = getPagerAnimationSpec()
                )
            }
        }
    )
}

@Composable
private fun TermsScreen(
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .limitWidthFraction()
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(top = MaterialTheme.spacing.largeTwo)
                .padding(MaterialTheme.spacing.smallThree),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(MaterialTheme.spacing.extraLarge),
                imageVector = Icons.Filled.AssignmentTurnedIn,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.setup_begin_terms_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.setup_begin_terms),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.weight(1f))
        SetupBottomNavRow(
            onNextClick = onNextClick
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
internal fun TermsScreenPreview() {
    PiFireTheme {
        Surface {
            TermsScreen(
                onNextClick = {}
            )
        }
    }
}