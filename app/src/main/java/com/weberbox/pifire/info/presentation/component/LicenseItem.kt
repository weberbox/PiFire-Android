package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.toColorInt
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.theme.SettingsTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.info.presentation.model.Licenses.License

@Composable
internal fun LicenseItem(
    license: License,
    modifier: Modifier = Modifier,
    onClick: ((String) -> Unit)? = null,
) {
    Card(
        modifier = Modifier.padding(
            top = MaterialTheme.spacing.extraExtraSmall,
            bottom = MaterialTheme.spacing.extraExtraSmall
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface
        ),
        shape = MaterialTheme.shapes.small,
        onClick = {
            if (onClick != null) onClick(license.license)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .fillMaxHeight()
                    .width(MaterialTheme.spacing.large)
                    .background(Color(license.color.toColorInt()))
            ) {
                Text(
                    text = license.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(MaterialTheme.spacing.extraExtraSmall)
                )
            }
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = license.project,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(MaterialTheme.spacing.extraExtraSmall)
                )
                Text(
                    text = license.license,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = modifier.padding(MaterialTheme.spacing.extraExtraSmall)
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun LicenseItemPreview() {
    val license = remember {
        License(
            project = "PiFire Android",
            license = "https://github.com/weberbox/PiFire-Android/blob/master/LICENSE"
        )
    }
    SettingsTheme {
        Surface {
            LicenseItem(
                license = license
            )
        }
    }
}