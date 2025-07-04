package com.weberbox.pifire.pellets.data.api

import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto
import com.weberbox.pifire.common.data.model.PostDto.PelletsDto
import com.weberbox.pifire.core.singleton.SocketManager
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PelletsApiImpl @Inject constructor(
    private val socketManager: SocketManager,
    private val json: Json
) : PelletsApi {

    override suspend fun getPellets(): Result<String, DataError> {
        return socketManager.emitGet(ServerConstants.GA_PELLETS_DATA)
    }

    override suspend fun getPelletLevel(): Result<Unit, DataError> {
        return socketManager.emitPost(
            ServerConstants.PA_PELLETS_ACTION,
            ServerConstants.PT_HOPPER_CHECK
        )
    }

    override suspend fun listenPelletsData(): Flow<Result<String, DataError>> {
        return socketManager.onFlow(ServerConstants.LISTEN_PELLET_DATA)
            .transform { result ->
                when (result) {
                    is Result.Error -> emit(Result.Error(result.error))
                    is Result.Success -> {
                        if (result.data.isNotEmpty() && result.data[0] != null) {
                            emit(Result.Success(result.data[0].toString()))
                        } else {
                            emit(Result.Error(DataError.Network.SERVER_ERROR))
                        }
                    }
                }
            }
    }

    override suspend fun addPelletWood(wood: String): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(pelletsDto = PelletsDto(newWood = wood)))
        return sendEmitPost(ServerConstants.PT_EDIT_WOODS, json)
    }

    override suspend fun deletePelletWood(wood: String): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(pelletsDto = PelletsDto(deleteWood = wood)))
        return sendEmitPost(ServerConstants.PT_EDIT_WOODS, json)
    }

    override suspend fun addPelletBrand(brand: String): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(pelletsDto = PelletsDto(newBrand = brand)))
        return sendEmitPost(ServerConstants.PT_EDIT_BRANDS, json)
    }

    override suspend fun deletePelletBrand(brand: String): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(pelletsDto = PelletsDto(deleteBrand = brand)))
        return sendEmitPost(ServerConstants.PT_EDIT_BRANDS, json)
    }

    override suspend fun deletePelletLog(logDate: String): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(pelletsDto = PelletsDto(logDate = logDate)))
        return sendEmitPost(ServerConstants.PT_DELETE_LOG, json)
    }

    override suspend fun loadPelletProfile(profile: String): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(pelletsDto = PelletsDto(profile = profile)))
        return sendEmitPost(ServerConstants.PT_LOAD_PROFILE, json)
    }

    override suspend fun addPelletProfile(
        profile: PelletProfile,
        load: Boolean
    ): Result<Unit, DataError> {
        val json = json.encodeToString(
            PostDto(
                pelletsDto = PelletsDto(
                    profile = profile.id,
                    brandName = profile.brand,
                    woodType = profile.wood,
                    rating = profile.rating,
                    comments = profile.comments,
                    addAndLoad = load
                )
            )
        )
        return sendEmitPost(ServerConstants.PT_ADD_PROFILE, json)
    }

    override suspend fun editPelletProfile(profile: PelletProfile): Result<Unit, DataError> {
        val json = json.encodeToString(
            PostDto(
                pelletsDto = PelletsDto(
                    profile = profile.id,
                    brandName = profile.brand,
                    woodType = profile.wood,
                    rating = profile.rating,
                    comments = profile.comments
                )
            )
        )
        return sendEmitPost(ServerConstants.PT_EDIT_PROFILE, json)
    }

    override suspend fun deletePelletProfile(profile: String): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(pelletsDto = PelletsDto(profile = profile)))
        return sendEmitPost(ServerConstants.PT_DELETE_PROFILE, json)
    }

    private suspend fun sendEmitPost(type: String, json: String): Result<Unit, DataError> {
        return socketManager.emitPost(ServerConstants.PA_PELLETS_ACTION, type, json)
    }
}