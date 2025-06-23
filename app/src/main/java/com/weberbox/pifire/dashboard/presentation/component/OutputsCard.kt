package com.weberbox.pifire.dashboard.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.DoubleArrowRight
import com.weberbox.pifire.common.icons.filled.Fan
import com.weberbox.pifire.common.icons.filled.Flame
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.modifier.rotateEffect
import com.weberbox.pifire.common.presentation.modifier.scaleEffect
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
internal fun OutputsCard(
    fanOutput: Boolean,
    augerOutput: Boolean,
    igniterOutput: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .height(MaterialTheme.spacing.extraExtraLarge)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small),
        onClick = {
            if (onClick != null) onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.padding(top = MaterialTheme.spacing.smallOne),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    imageVector = Icon.Filled.Fan,
                    contentDescription = stringResource(R.string.fan),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .size(MaterialTheme.spacing.largeTwo)
                        .rotateEffect(fanOutput)

                )
                Text(
                    text = stringResource(R.string.fan),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.small)
                )
            }
            Column(
                modifier = Modifier.padding(top = MaterialTheme.spacing.smallOne),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    imageVector = Icon.Filled.DoubleArrowRight,
                    contentDescription = stringResource(R.string.auger),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .size(MaterialTheme.spacing.largeTwo)
                        .scaleEffect(augerOutput)
                )
                Text(
                    text = stringResource(R.string.auger),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.small)
                )
            }
            Column(
                modifier = Modifier.padding(top = MaterialTheme.spacing.smallOne),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    imageVector = Icon.Filled.Flame,
                    contentDescription = stringResource(R.string.igniter),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .size(MaterialTheme.spacing.largeTwo)
                        .scaleEffect(igniterOutput)
                )
                Text(
                    text = stringResource(R.string.igniter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.small)
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun OutputsCardPreview() {
    PiFireTheme {
        Surface {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 5.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutputsCard(
                    fanOutput = false,
                    augerOutput = false,
                    igniterOutput = false,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .weight(1f)
                )
                OutputsCard(
                    fanOutput = false,
                    augerOutput = false,
                    igniterOutput = false,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .weight(1f)
                )
            }
        }
    }
}