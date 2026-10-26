package com.weberbox.pifire.core.notification

import com.weberbox.pifire.R
import com.weberbox.pifire.core.model.push.AlertType
import com.weberbox.pifire.core.model.push.PushAlert
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.core.ui.util.stringResArg
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class AlertContentTest {

    private val defaultLocale = Locale.getDefault()

    @Before
    fun setUp() {
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    private fun alert(type: AlertType) = PushAlert(
        alertId = "alert-1",
        type = type,
        serverUuid = "server-1",
        timeMillis = null,
    )

    private fun temp(
        value: String,
        units: String = "F",
    ) = stringResArg(resId = R.string.push_temp, args = uiTextArgsOf(value, units))

    @Test
    fun `probe target uses the name, target and units`() {
        val content = alert(AlertType.PROBE_TARGET)
            .copy(probeName = "Brisket", target = 203.0, units = "F")
            .toAlertContent()
        assertEquals(
            AlertContent(
                title = UiText(resId = R.string.push_probe_target_title, args = uiTextArgsOf("Brisket")),
                body = UiText(resId = R.string.push_probe_target_body, args = uiTextArgsOf("Brisket", temp("203"))),
                channel = AlertChannel.TEMP,
            ),
            content,
        )
    }

    @Test
    fun `Celsius decimals keep one digit`() {
        val content = alert(AlertType.PROBE_TARGET)
            .copy(probeName = "Brisket", target = 93.33, units = "C")
            .toAlertContent()
        assertEquals(
            UiText(
                resId = R.string.push_probe_target_body,
                args = uiTextArgsOf("Brisket", temp(value = "93.3", units = "C")),
            ),
            content.body,
        )
    }

    @Test
    fun `probe limit wording follows the direction`() {
        val limit = alert(AlertType.PROBE_LIMIT).copy(probeName = "Pork", limit = 225.0, units = "F")
        assertEquals(
            UiText(resId = R.string.push_probe_limit_high_title, args = uiTextArgsOf("Pork")),
            limit.copy(isHighLimit = true).toAlertContent().title,
        )
        assertEquals(
            UiText(resId = R.string.push_probe_limit_low_body, args = uiTextArgsOf("Pork", temp("225"))),
            limit.copy(isHighLimit = false).toAlertContent().body,
        )
        assertEquals(
            UiText(resId = R.string.push_probe_limit_title, args = uiTextArgsOf("Pork")),
            limit.toAlertContent().title,
        )
    }

    @Test
    fun `missing details fall back to the PiFire text, then generic wording, on the type channel`() {
        val noTarget = alert(AlertType.PROBE_TARGET).copy(
            probeName = "Brisket",
            fallbackTitle = "Brisket Target Achieved",
            fallbackBody = "Brisket target achieved",
        )
        assertEquals(
            AlertContent(
                title = UiText("Brisket Target Achieved"),
                body = UiText("Brisket target achieved"),
                channel = AlertChannel.TEMP,
            ),
            noTarget.toAlertContent(),
        )
        assertEquals(
            AlertContent(
                title = UiText(resId = R.string.push_generic_title),
                body = UiText(resId = R.string.push_generic_body),
                channel = AlertChannel.ERROR,
            ),
            alert(AlertType.GRILL_ERROR_MAX_TEMP).copy(temp = 600.0).toAlertContent(),
        )
    }

    @Test
    fun `unknown types use the fallback text on the base channel`() {
        val content = alert(AlertType.UNKNOWN)
            .copy(fallbackTitle = "New Alert", fallbackBody = "Something new")
            .toAlertContent()
        assertEquals(
            AlertContent(title = UiText("New Alert"), body = UiText("Something new"), channel = AlertChannel.BASE),
            content,
        )
    }

    @Test
    fun `recipe messages show the recipe text as-is`() {
        val content = alert(AlertType.RECIPE_MESSAGE).copy(message = "Wrap it now").toAlertContent()
        assertEquals(UiText("Wrap it now"), content.body)
        assertEquals(AlertChannel.RECIPE, content.channel)
        assertEquals(UiText(resId = R.string.push_recipe_body), alert(AlertType.RECIPE_MESSAGE).toAlertContent().body)
    }

    @Test
    fun `grill errors, pellets and timers use their own channels`() {
        val reignite = alert(AlertType.GRILL_REIGNITE)
            .copy(temp = 140.0, startupTemp = 150.0, units = "F")
            .toAlertContent()
        assertEquals(
            UiText(resId = R.string.push_grill_reignite_body, args = uiTextArgsOf(temp("140"), temp("150"))),
            reignite.body,
        )
        assertEquals(AlertChannel.ERROR, reignite.channel)
        assertEquals(
            UiText(resId = R.string.push_pellets_low_body, args = uiTextArgsOf("15")),
            alert(AlertType.PELLET_LEVEL_LOW).copy(level = 15.0).toAlertContent().body,
        )
        assertEquals(AlertChannel.TIMER, alert(AlertType.TIMER_EXPIRED).toAlertContent().channel)
    }
}
