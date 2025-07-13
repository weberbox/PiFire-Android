package com.weberbox.pifire.common.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
fun ConfirmSheet(
    title: String,
    message: String,
    negativeButtonText: String = stringResource(android.R.string.ok),
    positiveButtonText: String = stringResource(R.string.cancel),
    negativeButtonColor: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    positiveButtonColor: ButtonColors = ButtonDefaults.buttonColors(),
    onNegative: () -> Unit,
    onPositive: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = MaterialTheme.spacing.mediumOne,
                    end = MaterialTheme.spacing.mediumOne,
                    bottom = MaterialTheme.spacing.smallThree
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallThree),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                text = title,
                fontWeight = FontWeight.Bold
            )
            Text(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                text = message
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.spacing.mediumOne,
                    end = MaterialTheme.spacing.mediumOne,
                    bottom = MaterialTheme.spacing.smallThree
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                colors = negativeButtonColor,
                onClick = { onNegative() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = negativeButtonText,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                colors = positiveButtonColor,
                onClick = { onPositive() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = positiveButtonText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ButtonSheetPreview() {
    PiFireTheme {
        Surface {
            ConfirmSheet(
                title = stringResource(R.string.dialog_confirm_action),
                message = stringResource(R.string.dialog_confirm_delete_generic),
                negativeButtonText = stringResource(R.string.cancel),
                positiveButtonText = stringResource(R.string.delete),
                positiveButtonColor = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error
                ),
                onNegative = {},
                onPositive = {}
            )
        }
    }
}