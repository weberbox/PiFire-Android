package com.weberbox.pifire.feature.info.data.mapper

import androidx.compose.ui.graphics.toArgb
import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.ui.theme.eventsDebug
import com.weberbox.pifire.core.ui.theme.eventsError
import com.weberbox.pifire.core.ui.theme.eventsInfo
import com.weberbox.pifire.core.ui.theme.eventsModeEnded
import com.weberbox.pifire.core.ui.theme.eventsModeStarted
import com.weberbox.pifire.core.ui.theme.eventsScriptEnded
import com.weberbox.pifire.core.ui.theme.eventsScriptStart
import com.weberbox.pifire.core.ui.theme.eventsWarning
import com.weberbox.pifire.feature.info.data.model.LicenseDto
import com.weberbox.pifire.feature.info.presentation.model.Licenses
import com.weberbox.pifire.feature.info.presentation.model.Licenses.License
import kotlin.random.Random

object LicenseDtoToDataMapper : Mapper<List<LicenseDto>, Licenses> {
    private val colors = listOf(
        eventsScriptStart,
        eventsScriptEnded,
        eventsModeStarted,
        eventsModeEnded,
        eventsError,
        eventsWarning,
        eventsDebug,
        eventsInfo
    )

    override fun map(from: List<LicenseDto>) = Licenses(
        from.map {
            License(
                project = it.project ?: "",
                license = it.license ?: "",
                title = it.project?.substring(0, 1)?.uppercase() ?: "",
                color = String.format("#%08X", colors[Random.nextInt(colors.size)].toArgb())
            )
        }
    )
}