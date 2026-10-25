package com.weberbox.pifire.common.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import kotlin.reflect.KClass

/**
 * The back stack entry of the destination this composable belongs to. Pager pages get their
 * host's entry, which is what the navigation guard expects.
 */
@Composable
fun rememberSourceEntry(): NavBackStackEntry {
    val lifecycleOwner = LocalLifecycleOwner.current
    return remember(lifecycleOwner) {
        requireNotNull(lifecycleOwner as? NavBackStackEntry) {
            "rememberSourceEntry() must be called inside a NavHost destination"
        }
    }
}

// Guarded on "is the sender still on top" rather than lifecycle state: the back stack updates
// synchronously, so a second tap is ignored even mid-animation, while a screen that's still
// animating in (STARTED, not yet RESUMED) can already navigate.
private fun NavController.isOnTop(entry: NavBackStackEntry): Boolean =
    currentBackStackEntry?.id == entry.id

/** [popUpToRoute] is a route object, or its `KClass` for a route with arguments. */
fun NavController.navigateFrom(
    from: NavBackStackEntry,
    route: Any,
    popUpToRoute: Any? = null,
    inclusive: Boolean = true,
) {
    if (!isOnTop(entry = from)) return
    navigate(route = route) {
        when (popUpToRoute) {
            null -> Unit
            is KClass<*> -> popUpTo(route = popUpToRoute) { this.inclusive = inclusive }
            else -> popUpTo(route = popUpToRoute) { this.inclusive = inclusive }
        }
    }
}

/**
 * [onNothingToPop] runs only when [from] is on top but is the last screen (e.g. to finish the
 * activity), never when the call is ignored, so a double back tap can't close the app.
 */
fun NavController.popBackStackFrom(
    from: NavBackStackEntry,
    onNothingToPop: () -> Unit = {},
) {
    if (!isOnTop(entry = from)) return
    if (!popBackStack()) onNothingToPop()
}

/** Pops back to [route], which is a route object or its `KClass` for a route with arguments. */
fun NavController.popBackStackFrom(
    from: NavBackStackEntry,
    route: Any,
    inclusive: Boolean,
) {
    if (!isOnTop(entry = from)) return
    when (route) {
        is KClass<*> -> popBackStack(route = route, inclusive = inclusive)
        else -> popBackStack(route = route, inclusive = inclusive)
    }
}

/** Pops [from] and hands [arg] to the screen below, which reads it from its own entry. */
fun <T> NavController.navBackWithResultFrom(from: NavBackStackEntry, key: String, arg: T) {
    if (!isOnTop(entry = from)) return
    previousBackStackEntry?.savedStateHandle?.set(key, arg)
    popBackStack()
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
