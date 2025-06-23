package com.weberbox.pifire.dashboard.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash
import kotlinx.coroutines.flow.Flow

interface DashRepo {
    suspend fun getDashData(): Result<Dash, DataError>
    suspend fun getCachedData(): Result<Dash, DataError.Local>
    suspend fun listenDashData(): Flow<Result<Dash, DataError>>
    suspend fun updateDashData(dash: Dash)
}