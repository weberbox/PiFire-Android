package com.weberbox.pifire.feature.settings.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.feature.settings.presentation.model.ManualData

interface ManualRepo {
    suspend fun getManualData(): Result<ManualData, DataError>
}