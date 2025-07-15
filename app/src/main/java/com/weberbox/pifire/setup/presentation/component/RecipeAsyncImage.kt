package com.weberbox.pifire.setup.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoPhotography
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.weberbox.pifire.common.presentation.component.CircularLoadingIndicator
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.recipes.presentation.screens.buildRecipe
import com.weberbox.pifire.recipes.presentation.util.coinImageBuilder
import com.weberbox.pifire.recipes.presentation.util.decodeBase64Image
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun RecipeAsyncImage(
    image: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    zoomState: ZoomState? = null
) {
    val colorStops = arrayOf(
        0.5f to MaterialTheme.colorScheme.surfaceContainerHighest,
        1f to MaterialTheme.colorScheme.surfaceContainer
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .then(
                zoomState?.let {
                    Modifier.zoomable(
                        zoomState = zoomState,
                        scrollGesturePropagation =
                            ScrollGesturePropagation.NotZoomed
                    )
                } ?: Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        SubcomposeAsyncImage(
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large),
            model = coinImageBuilder(image),
            loading = {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .background(Brush.radialGradient(colorStops = colorStops)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularLoadingIndicator(
                        isLoading = true,
                        modifier = Modifier.size(150.dp)
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .background(Brush.radialGradient(colorStops = colorStops)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(0.7f),
                        imageVector = Icons.Outlined.NoPhotography,
                        contentDescription = null
                    )
                }
            },
            onSuccess = { state ->
                zoomState?.setContentSize(state.painter.intrinsicSize)
            },
            contentDescription = null,
            contentScale = contentScale
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RecipeAsyncImagePreview() {
    val image = remember { decodeBase64Image(buildRecipe()[0].assets[0].encodedThumb) }
    PiFireTheme {
        Surface {
            RecipeAsyncImage(
                image = image
            )
        }
    }
}