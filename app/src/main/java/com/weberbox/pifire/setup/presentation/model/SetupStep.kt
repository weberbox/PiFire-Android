package com.weberbox.pifire.setup.presentation.model

import com.weberbox.pifire.R

enum class SetupStep(val step: Int, val title: Int) {
    Terms(step = 0, title = R.string.setup_terms),
    Auth(step = 1, title = R.string.setup_auth),
    Connect(step = 2, title = R.string.setup_connect),
    Push(step = 3, title = R.string.setup_push),
    Finish(step = 4, title = R.string.setup_finish)
}