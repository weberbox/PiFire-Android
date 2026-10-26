package com.weberbox.pifire.core.common.model

typealias RootError = Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
}

inline fun <D, E : RootError, R> Result<D, E>.map(transform: (D) -> R): Result<R, E> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(error)
}

/** Runs [next] on success, so a chain of calls stops at the first error. */
inline fun <D, E : RootError, R> Result<D, E>.andThen(next: (D) -> Result<R, E>): Result<R, E> = when (this) {
    is Result.Success -> next(data)
    is Result.Error -> Result.Error(error)
}

inline fun <D, E : RootError> Result<D, E>.onSuccess(action: (D) -> Unit): Result<D, E> {
    if (this is Result.Success) action(data)
    return this
}
