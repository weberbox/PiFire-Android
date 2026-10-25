package com.weberbox.pifire.feature.info.data.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result

interface InfoApi {
    suspend fun getInfo(): Result<String, DataError>
}