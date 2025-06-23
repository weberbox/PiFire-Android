package com.weberbox.pifire.info.domain

import androidx.compose.ui.graphics.toArgb
import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.common.presentation.theme.eventsDebug
import com.weberbox.pifire.common.presentation.theme.eventsError
import com.weberbox.pifire.common.presentation.theme.eventsInfo
import com.weberbox.pifire.common.presentation.theme.eventsModeEnded
import com.weberbox.pifire.common.presentation.theme.eventsModeStarted
import com.weberbox.pifire.common.presentation.theme.eventsScriptEnded
import com.weberbox.pifire.common.presentation.theme.eventsScriptStart
import com.weberbox.pifire.common.presentation.theme.eventsWarning
import com.weberbox.pifire.info.data.model.LicenseDto
import com.weberbox.pifire.info.presentation.model.Licenses
import com.weberbox.pifire.info.presentation.model.Licenses.License
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