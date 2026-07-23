package com.kaiorc

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Qualifier

/** Binds the un-retried [AIProvider] that [RetryingAIProvider] wraps — a plain [AIProvider] binding here would be circular. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnretriedAIProvider

/**
 * Retries a transient AI provider failure (rate limiting, a network hiccup — as decided by
 * [retryPolicy]) a bounded number of times with a short backoff before giving up. Every other
 * failure comes back immediately, since retrying it can't help.
 */
class RetryingAIProvider<TError : Any> @Inject constructor(
    @UnretriedAIProvider private val delegate: AIProvider<TError>,
    private val retryPolicy: RetryPolicy<TError>
) : AIProvider<TError> {

    override suspend fun chat(systemPrompt: String, userMessage: String): AIResult<String, TError> =
        withRetry { delegate.chat(systemPrompt, userMessage) }

    override suspend fun chatWithImages(
        systemPrompt: String,
        userMessage: String,
        images: List<Pair<String, String>>
    ): AIResult<String, TError> =
        withRetry { delegate.chatWithImages(systemPrompt, userMessage, images) }

    private suspend fun withRetry(call: suspend () -> AIResult<String, TError>): AIResult<String, TError> {
        var result = call()
        for (delayMs in RETRY_DELAYS_MS) {
            if (result !is AIResult.Failure || !retryPolicy.isRetryable(result.error)) return result
            delay(delayMs)
            result = call()
        }
        return result
    }

    private companion object {
        val RETRY_DELAYS_MS = listOf(500L, 1500L)
    }
}
