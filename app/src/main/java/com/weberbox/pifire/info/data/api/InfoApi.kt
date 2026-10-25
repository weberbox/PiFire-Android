package com.weberbox.pifire.info.data.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result

interface InfoApi {
    suspend fun getInfo(): Result<String, DataError>
}