package com.weberbox.pifire.common.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.navFontFamily
import com.weberbox.pifire.common.presentation.theme.pelletDanger
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
fun PiFireLogo(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium
) {
    Image(
        modifier = modifier.size(MaterialTheme.spacing.extraExtraLarge),
        painter = painterResource(id = R.drawable.ic_pifire_logo),
        contentDescription = stringResource(R.string.app_name)
    )
    Text(buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontFamily = navFontFamily,
                fontSize = textStyle.fontSize
            )
        ) {
            append(stringResource(R.string.app_name).substring(0, 2))
        }
        withStyle(
            style = SpanStyle(
                color = pelletDanger,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontFamily = navFontFamily,
                fontSize = textStyle.fontSize
            )
        ) {
            append(stringResource(R.string.app_name).substring(2, 6))
        }
    })
}