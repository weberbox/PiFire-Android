package com.weberbox.pifire.feature.landing.presentation.util

import android.content.Context
import com.weberbox.pifire.R
import com.weberbox.pifire.feature.landing.presentation.model.ServerData.Server

/** The server's name, or "Smoker <n>" when it has none. */
internal fun Server.displayName(context: Context): String = name.ifEmpty { context.getString(R.string.smoker, number) }
