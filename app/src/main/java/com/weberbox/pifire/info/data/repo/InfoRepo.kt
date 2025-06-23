package com.weberbox.pifire.info.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import com.weberbox.pifire.info.presentation.model.Licenses

interface InfoRepo {
    suspend fun getInfo(): Result<Info, DataError>
    suspend fun getCachedData(): Result<Info, DataError.Local>
    suspend fun getLicences(): Result<Licenses, DataError>
    suspend fun updateInfoData(info: Info)
}