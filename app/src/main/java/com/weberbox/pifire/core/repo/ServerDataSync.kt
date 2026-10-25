package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.model.andThen
import com.weberbox.pifire.core.common.model.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * The steps each repo repeats for one kind of per-server data: fetch, parse, publish to the
 * session and save to the cache; read the cache back for the current server; parse live updates.
 * Repos pass the parse function (the response parsers are reified) and where the value lives.
 * Everything runs on [Dispatchers.IO], so callers can stay on Main.
 */
class ServerDataSync<T>(
    private val currentServerUuid: () -> String,
    private val publish: suspend (T) -> Unit,
    private val readCache: suspend (serverUuid: String) -> T?,
    private val writeCache: suspend (T) -> Unit,
) {
    suspend fun fetch(
        request: suspend () -> Result<String, DataError>,
        parse: suspend (String) -> Result<T, DataError>,
    ): Result<T, DataError> = withContext(Dispatchers.IO) {
        request()
            .andThen { parse(it) }
            .onSuccess {
                publish(it)
                writeCache(it)
            }
    }

    /** The current server's cached value; [publishToSession] also makes it the session's value. */
    suspend fun cached(publishToSession: Boolean = true): Result<T, DataError.Local> = withContext(Dispatchers.IO) {
        val serverUuid = currentServerUuid()
        val value = if (serverUuid.isNotBlank()) readCache(serverUuid) else null
        if (value == null) {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            if (publishToSession) publish(value)
            Result.Success(value)
        }
    }

    fun listen(
        updates: Flow<Result<String, DataError>>,
        parse: suspend (String) -> Result<T, DataError>,
    ): Flow<Result<T, DataError>> = updates
        .map { result -> result.andThen { parse(it) }.onSuccess { publish(it) } }
        .flowOn(Dispatchers.IO)
}
