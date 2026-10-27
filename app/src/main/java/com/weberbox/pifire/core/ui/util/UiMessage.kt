package com.weberbox.pifire.core.ui.util

/**
 * A short message for the user, shown with [showMessage]. The style follows what it's about:
 * - [Style.Toast]: confirms something the user just did (and can't otherwise see, e.g. the
 *   screen closed).
 * - [Style.Alert]: something the user didn't cause and wouldn't see otherwise.
 * - [Style.Error]: something went wrong; a red alert that vibrates.
 *
 * No message at all when the screen already shows the result (an icon toggles, a list empties).
 */
data class UiMessage(val text: UiText, val style: Style) {
    enum class Style { Toast, Alert, Error }

    companion object {
        fun toast(text: UiText) = UiMessage(text = text, style = Style.Toast)
        fun alert(text: UiText) = UiMessage(text = text, style = Style.Alert)
        fun error(text: UiText) = UiMessage(text = text, style = Style.Error)

        /** For contract effects that carry an error flag. */
        fun of(text: UiText, isError: Boolean) = if (isError) error(text = text) else alert(text = text)
    }
}
