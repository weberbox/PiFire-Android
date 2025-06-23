package com.weberbox.pifire.settings.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.settings.presentation.model.ManualData

interface ManualRepo {
    suspend fun getManualData(): Result<ManualData, DataError>
}