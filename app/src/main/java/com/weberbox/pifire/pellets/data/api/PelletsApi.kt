package com.weberbox.pifire.pellets.data.api

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile
import kotlinx.coroutines.flow.Flow

interface PelletsApi {
    suspend fun getPellets(): Result<String, DataError>
    suspend fun getPelletLevel(): Result<Unit, DataError>
    suspend fun listenPelletsData(): Flow<Result<String, DataError>>
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