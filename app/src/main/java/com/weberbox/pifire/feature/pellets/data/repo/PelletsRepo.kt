package com.weberbox.pifire.feature.pellets.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletProfile
import kotlinx.coroutines.flow.Flow

interface PelletsRepo {
    suspend fun getPellets(): Result<Pellets, DataError>
    suspend fun getCachedData(): Result<Pellets, DataError.Local>
    suspend fun listenPelletsData(): Flow<Result<Pellets, DataError>>
    suspend fun getPelletsDataState(): Flow<Pellets?>
    suspend fun updatePelletsData(pellets: Pellets)

    suspend fun getPelletLevel(): Result<Unit, DataError>
    suspend fun addPelletWood(wood: String): Result<Unit, DataError>
    suspend fun deletePelletWood(wood: String): Result<Unit, DataError>
    suspend fun addPelletBrand(brand: String): Result<Unit, DataError>
    suspend fun deletePelletBrand(brand: String): Result<Unit, DataError>
    suspend fun deletePelletLog(logDate: String): Result<Unit, DataError>
    suspend fun loadPelletProfile(profile: String): Result<Unit, DataError>
    suspend fun addPelletProfile(profile: PelletProfile, load: Boolean): Result<Unit, DataError>
    suspend fun editPelletProfile(profile: PelletProfile): Result<Unit, DataError>
    suspend fun deletePelletProfile(profile: String): Result<Unit, DataError>
}
