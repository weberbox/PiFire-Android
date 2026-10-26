package com.weberbox.pifire.core.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServerDataSyncTest {

    private val published = mutableListOf<String>()
    private val cache = mutableMapOf<String, String>()
    private var currentServer = "server-1"

    private val sync = ServerDataSync<String>(
        currentServerUuid = { currentServer },
        publish = { published += it },
        readCache = { cache[it] },
        writeCache = { cache[currentServer] = it },
    )

    private val parseUpper: suspend (String) -> Result<String, DataError> = { Result.Success(it.uppercase()) }
    private val parseFails: suspend (String) -> Result<String, DataError> = { Result.Error(DataError.Local.JSON_ERROR) }

    @Test
    fun `fetch parses, publishes and caches a success`() = runTest {
        val result = sync.fetch(request = { Result.Success("dash") }, parse = parseUpper)
        assertEquals(Result.Success<String, DataError>("DASH"), result)
        assertEquals(listOf("DASH"), published)
        assertEquals("DASH", cache["server-1"])
    }

    @Test
    fun `fetch passes errors through without publishing or caching`() = runTest {
        val requestError = sync.fetch(request = { Result.Error(DataError.Network.SOCKET_TIMEOUT) }, parse = parseUpper)
        val parseError = sync.fetch(request = { Result.Success("dash") }, parse = parseFails)
        assertEquals(Result.Error<String, DataError>(DataError.Network.SOCKET_TIMEOUT), requestError)
        assertEquals(Result.Error<String, DataError>(DataError.Local.JSON_ERROR), parseError)
        assertTrue(published.isEmpty())
        assertTrue(cache.isEmpty())
    }

    @Test
    fun `cached reads the current server and can skip publishing`() = runTest {
        cache["server-1"] = "cached"
        assertEquals(Result.Success<String, DataError.Local>("cached"), sync.cached())
        assertEquals(listOf("cached"), published)
        sync.cached(publishToSession = false)
        assertEquals(listOf("cached"), published)
    }

    @Test
    fun `cached is an error with no data or no current server`() = runTest {
        assertEquals(Result.Error<String, DataError.Local>(DataError.Local.NO_CACHED_DATA), sync.cached())
        cache["server-1"] = "cached"
        currentServer = ""
        assertEquals(Result.Error<String, DataError.Local>(DataError.Local.NO_CACHED_DATA), sync.cached())
    }

    @Test
    fun `listen parses and publishes each update`() = runTest {
        val updates = flowOf<Result<String, DataError>>(
            Result.Success("a"),
            Result.Error(DataError.Network.SOCKET_ERROR),
            Result.Success("b"),
        )
        val results = sync.listen(updates = updates, parse = parseUpper).toList()
        assertEquals(
            listOf(
                Result.Success<String, DataError>("A"),
                Result.Error(DataError.Network.SOCKET_ERROR),
                Result.Success("B"),
            ),
            results,
        )
        assertEquals(listOf("A", "B"), published)
    }
}
