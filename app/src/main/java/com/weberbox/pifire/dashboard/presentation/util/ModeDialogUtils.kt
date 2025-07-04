package com.weberbox.pifire.dashboard.presentation.util

import com.weberbox.pifire.dashboard.presentation.model.ModeOptions
import com.weberbox.pifire.dashboard.presentation.model.RunningMode

internal fun getModeButtons(mode: String, paused: Boolean): List<ModeOptions> {
    return when (mode) {
        RunningMode.Startup.name, RunningMode.Reignite.name, RunningMode.Shutdown.name -> {
            listOf(ModeOptions.Smoke, ModeOptions.Hold, ModeOptions.Stop)
        }

        RunningMode.Stop.name -> {
            listOf(ModeOptions.Prime, ModeOptions.Monitor, ModeOptions.Start)
        }

        RunningMode.Monitor.name, RunningMode.Prime.name -> {
            listOf(ModeOptions.Start, ModeOptions.Monitor, ModeOptions.Stop)
        }

        RunningMode.Recipe.name -> {
            listOf(ModeOptions.Shutdown, if (paused) ModeOptions.Continue else ModeOptions.Next)
        }

        RunningMode.Hold.name -> {
            listOf(ModeOptions.Smoke, ModeOptions.Hold, ModeOptions.Shutdown)
        }

        RunningMode.Smoke.name -> {
            listOf(ModeOptions.Hold, ModeOptions.Stop, ModeOptions.Shutdown)
        }

        else -> {
            listOf(ModeOptions.Smoke, ModeOptions.Hold, ModeOptions.Stop)
        }
    }
}