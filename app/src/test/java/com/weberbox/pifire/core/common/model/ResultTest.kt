package com.weberbox.pifire.core.common.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ResultTest {

    private val success: Result<Int, DataError> = Result.Success(2)
    private val failure: Result<Int, DataError> = Result.Error(DataError.Network.SERVER_ERROR)

    @Test
    fun `map transforms only a success`() {
        assertEquals(Result.Success<Int, DataError>(4), success.map { it * 2 })
        assertEquals(failure, failure.map { it * 2 })
    }

    @Test
    fun `andThen chains a success and stops at an error`() {
        assertEquals(Result.Success<String, DataError>("2"), success.andThen { Result.Success("$it") })
        assertEquals(
            Result.Error<String, DataError>(DataError.Local.JSON_ERROR),
            success.andThen { Result.Error<String, DataError>(DataError.Local.JSON_ERROR) },
        )
        var called = false
        failure.andThen {
            called = true
            Result.Success("$it")
        }
        assertEquals(false, called)
    }

    @Test
    fun `onSuccess runs only for a success and returns the same result`() {
        val seen = mutableListOf<Int>()
        assertEquals(success, success.onSuccess { seen += it })
        assertEquals(failure, failure.onSuccess { seen += it })
        assertEquals(listOf(2), seen)
    }
}
