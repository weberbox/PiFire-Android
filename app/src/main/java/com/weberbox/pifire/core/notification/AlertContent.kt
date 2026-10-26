package com.weberbox.pifire.core.notification

import com.weberbox.pifire.R
import com.weberbox.pifire.core.model.push.AlertType
import com.weberbox.pifire.core.model.push.PushAlert
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.core.ui.util.stringResArg
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import java.text.NumberFormat

data class AlertContent(
    val title: UiText,
    val body: UiText,
    val channel: AlertChannel,
)

/**
 * Wording for an alert, from string resources. A known type missing the details its text needs
 * shows PiFire's fallback text instead, then generic wording, so every alert still shows.
 */
fun PushAlert.toAlertContent(): AlertContent = typedContent() ?: AlertContent(
    title = fallbackTitle?.let { UiText(it) } ?: UiText(resId = R.string.push_generic_title),
    body = fallbackBody?.let { UiText(it) } ?: UiText(resId = R.string.push_generic_body),
    channel = type.channel(),
)

fun AlertType.channel(): AlertChannel = when (this) {
    AlertType.PROBE_TARGET, AlertType.PROBE_LIMIT -> AlertChannel.TEMP

    AlertType.TIMER_EXPIRED -> AlertChannel.TIMER

    AlertType.PELLET_LEVEL_LOW -> AlertChannel.PELLETS

    AlertType.GRILL_ERROR,
    AlertType.GRILL_ERROR_MAX_TEMP,
    AlertType.GRILL_ERROR_STARTUP_TEMP,
    AlertType.GRILL_REIGNITE,
    AlertType.GRILL_WARNING,
    AlertType.CONTROL_STOPPED,
    -> AlertChannel.ERROR

    AlertType.RECIPE_MESSAGE -> AlertChannel.RECIPE

    AlertType.TEST, AlertType.UNKNOWN -> AlertChannel.BASE
}

private fun PushAlert.typedContent(): AlertContent? = when (type) {
    AlertType.PROBE_TARGET -> {
        val name = probeName ?: return null
        val target = target ?: return null
        content(
            titleRes = R.string.push_probe_target_title,
            titleArgs = listOf(name),
            bodyRes = R.string.push_probe_target_body,
            bodyArgs = listOf(name, tempArg(target)),
        )
    }

    AlertType.PROBE_LIMIT -> {
        val name = probeName ?: return null
        val limit = limit ?: return null
        val (titleRes, bodyRes) = when (isHighLimit) {
            true -> R.string.push_probe_limit_high_title to R.string.push_probe_limit_high_body
            false -> R.string.push_probe_limit_low_title to R.string.push_probe_limit_low_body
            null -> R.string.push_probe_limit_title to R.string.push_probe_limit_body
        }
        content(
            titleRes = titleRes,
            titleArgs = listOf(name),
            bodyRes = bodyRes,
            bodyArgs = listOf(name, tempArg(limit)),
        )
    }

    AlertType.TIMER_EXPIRED -> content(
        titleRes = R.string.push_timer_title,
        bodyRes = R.string.push_timer_body,
    )

    AlertType.PELLET_LEVEL_LOW -> {
        val level = level ?: return null
        content(
            titleRes = R.string.push_pellets_low_title,
            bodyRes = R.string.push_pellets_low_body,
            bodyArgs = listOf(formatNumber(level)),
        )
    }

    AlertType.GRILL_ERROR -> content(
        titleRes = R.string.push_grill_error_title,
        bodyRes = R.string.push_grill_error_body,
    )

    AlertType.GRILL_ERROR_MAX_TEMP -> {
        val temp = temp ?: return null
        val maxTemp = maxTemp ?: return null
        content(
            titleRes = R.string.push_grill_error_title,
            bodyRes = R.string.push_grill_error_max_temp_body,
            bodyArgs = listOf(tempArg(temp), tempArg(maxTemp)),
        )
    }

    AlertType.GRILL_ERROR_STARTUP_TEMP -> {
        val temp = temp ?: return null
        val startupTemp = startupTemp ?: return null
        content(
            titleRes = R.string.push_grill_error_title,
            bodyRes = R.string.push_grill_error_startup_temp_body,
            bodyArgs = listOf(tempArg(temp), tempArg(startupTemp)),
        )
    }

    AlertType.GRILL_REIGNITE -> {
        val temp = temp ?: return null
        val startupTemp = startupTemp ?: return null
        content(
            titleRes = R.string.push_grill_reignite_title,
            bodyRes = R.string.push_grill_reignite_body,
            bodyArgs = listOf(tempArg(temp), tempArg(startupTemp)),
        )
    }

    AlertType.GRILL_WARNING -> content(
        titleRes = R.string.push_grill_warning_title,
        bodyRes = R.string.push_grill_warning_body,
    )

    AlertType.CONTROL_STOPPED -> content(
        titleRes = R.string.push_control_stopped_title,
        bodyRes = R.string.push_control_stopped_body,
    )

    // The message is text the user wrote in their recipe.
    AlertType.RECIPE_MESSAGE -> AlertContent(
        title = UiText(resId = R.string.push_recipe_title),
        body = message?.let { UiText(it) } ?: UiText(resId = R.string.push_recipe_body),
        channel = AlertChannel.RECIPE,
    )

    AlertType.TEST -> content(
        titleRes = R.string.push_test_title,
        bodyRes = R.string.push_test_body,
    )

    AlertType.UNKNOWN -> null
}

private fun PushAlert.content(
    titleRes: Int,
    bodyRes: Int,
    titleArgs: List<Any> = emptyList(),
    bodyArgs: List<Any> = emptyList(),
) = AlertContent(
    title = uiText(resId = titleRes, args = titleArgs),
    body = uiText(resId = bodyRes, args = bodyArgs),
    channel = type.channel(),
)

private fun uiText(
    resId: Int,
    args: List<Any>,
): UiText = if (args.isEmpty()) {
    UiText(resId = resId)
} else {
    UiText(resId = resId, args = uiTextArgsOf(args.first(), *args.drop(1).toTypedArray()))
}

private fun PushAlert.tempArg(value: Double) = units?.let { units ->
    stringResArg(resId = R.string.push_temp, args = uiTextArgsOf(formatNumber(value), units))
} ?: stringResArg(resId = R.string.push_temp_no_units, args = uiTextArgsOf(formatNumber(value)))

// Celsius servers send decimals: show at most one, in the phone's locale.
private fun formatNumber(value: Double): String = NumberFormat.getNumberInstance()
    .apply { maximumFractionDigits = 1 }
    .format(value)
