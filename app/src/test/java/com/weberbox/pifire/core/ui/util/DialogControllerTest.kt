package com.weberbox.pifire.core.ui.util

import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DialogControllerTest {
    private fun dialog(tag: String? = null) = DialogEvent(
        title = UiText(value = "Title"),
        message = UiText(value = "Message"),
        positiveAction = DialogAction(buttonText = UiText(value = "OK")),
        tag = tag,
    )

    @After
    fun tearDown() {
        DialogController.current.value?.let(DialogController::dismiss)
    }

    @Test
    fun `a new dialog replaces the one showing`() = runTest {
        val first = dialog()
        val second = dialog(tag = "second")
        DialogController.sendEvent(event = first)
        DialogController.sendEvent(event = second)

        assertEquals(second, DialogController.current.value)
    }

    @Test
    fun `dismissing a replaced dialog leaves the new one showing`() = runTest {
        val first = dialog()
        val second = dialog(tag = "second")
        DialogController.sendEvent(event = first)
        DialogController.sendEvent(event = second)

        DialogController.dismiss(event = first)

        assertEquals(second, DialogController.current.value)
    }

    @Test
    fun `dismissing the dialog showing closes it`() = runTest {
        val event = dialog()
        DialogController.sendEvent(event = event)

        DialogController.dismiss(event = event)

        assertNull(DialogController.current.value)
    }

    @Test
    fun `withdraw closes only the dialog with that tag`() = runTest {
        val event = dialog(tag = "shown")
        DialogController.sendEvent(event = event)

        DialogController.withdraw(tag = "other")
        assertEquals(event, DialogController.current.value)

        DialogController.withdraw(tag = "shown")
        assertNull(DialogController.current.value)
    }
}
