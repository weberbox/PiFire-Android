package com.weberbox.pifire.feature.pellets.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.feature.pellets.presentation.model.PelletsData.Pellets
import kotlinx.coroutines.flow.Flow

interface PelletsRepo {
    suspend fun getPellets(): Result<Pellets, DataError>
    suspend fun getCachedData(): Result<Pellets, DataError.Local>
    suspend fun listenPelletsData(): Flow<Result<Pellets, DataError>>
    suspend fun getPelletsDataState(): Flow<Pellets?>
    suspend fun updatePelletsData(pellets: Pellets)
}