package com.weberbox.pifire.settings.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.settings.presentation.model.ManualData

interface ManualRepo {
    suspend fun getManualData(): Result<ManualData, DataError>
}