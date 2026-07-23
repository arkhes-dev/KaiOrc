package com.kaiorc

/**
 * What every KaiOrc seam returns instead of throwing or returning a bare `Boolean` — generic over
 * the error type ([TError]) rather than a fixed enum, since a reusable library can't presume its
 * host app's error vocabulary. A host app typically fixes [TError] to its own domain error type
 * (e.g. `typealias AiResult<T> = AIResult<T, DomainError>`), at which point this behaves exactly
 * like a host's existing sealed result type — just owned by the library, not the app.
 */
sealed class AIResult<out T, out TError : Any> {
    data class Success<T>(val value: T) : AIResult<T, Nothing>()
    data class Failure<TError : Any>(val error: TError) : AIResult<Nothing, TError>()

    inline fun onSuccess(action: (T) -> Unit): AIResult<T, TError> {
        if (this is Success) action(value)
        return this
    }

    inline fun onFailure(action: (TError) -> Unit): AIResult<T, TError> {
        if (this is Failure) action(error)
        return this
    }

    inline fun <R> map(transform: (T) -> R): AIResult<R, TError> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }
}
