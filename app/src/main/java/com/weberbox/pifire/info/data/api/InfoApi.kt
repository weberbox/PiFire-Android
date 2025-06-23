package com.weberbox.pifire.info.data.api

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result

interface InfoApi {
    suspend fun getInfo(): Result<String, DataError>
}