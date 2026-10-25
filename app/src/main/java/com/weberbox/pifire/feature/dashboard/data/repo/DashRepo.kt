package com.weberbox.pifire.feature.dashboard.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.dashboard.DashData.Dash
import kotlinx.coroutines.flow.Flow

interface DashRepo {
    suspend fun getDashData(): Result<Dash, DataError>
    suspend fun getCachedData(): Result<Dash, DataError.Local>
    suspend fun listenDashData(): Flow<Result<Dash, DataError>>
    suspend fun updateDashData(dash: Dash)
}