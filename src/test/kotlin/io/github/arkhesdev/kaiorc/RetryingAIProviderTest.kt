package io.github.arkhesdev.kaiorc

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private sealed class RetryTestError {
    data object RateLimited : RetryTestError()
    data object NoInternet : RetryTestError()
    data object AuthenticationFailed : RetryTestError()
}

private val testRetryPolicy = RetryPolicy<RetryTestError> { it == RetryTestError.RateLimited || it == RetryTestError.NoInternet }

/** Returns each of [results] in order, one per call, holding on the last entry once exhausted. */
private class SequencedAIProvider(private val results: List<AIResult<String, RetryTestError>>) : AIProvider<RetryTestError> {
    var callCount = 0
        private set

    override suspend fun chat(systemPrompt: String, userMessage: String): AIResult<String, RetryTestError> = nextResult()
    override suspend fun chatWithImages(systemPrompt: String, userMessage: String, images: List<Pair<String, String>>): AIResult<String, RetryTestError> = nextResult()

    private fun nextResult(): AIResult<String, RetryTestError> {
        val result = results[callCount.coerceAtMost(results.size - 1)]
        callCount++
        return result
    }
}

class RetryingAIProviderTest {

    @Test
    fun `succeeds immediately without retrying`() = runTest {
        val delegate = SequencedAIProvider(listOf(AIResult.Success("ok")))
        val provider = RetryingAIProvider(delegate, testRetryPolicy)

        val result = provider.chat("system", "question")

        assertEquals(AIResult.Success("ok"), result)
        assertEquals(1, delegate.callCount)
    }

    @Test
    fun `retries a rate-limited failure and returns the eventual success`() = runTest {
        val delegate = SequencedAIProvider(listOf(AIResult.Failure(RetryTestError.RateLimited), AIResult.Success("ok")))
        val provider = RetryingAIProvider(delegate, testRetryPolicy)

        val result = provider.chat("system", "question")

        assertEquals(AIResult.Success("ok"), result)
        assertEquals(2, delegate.callCount)
    }

    @Test
    fun `retries a NoInternet failure too`() = runTest {
        val delegate = SequencedAIProvider(listOf(AIResult.Failure(RetryTestError.NoInternet), AIResult.Success("ok")))
        val provider = RetryingAIProvider(delegate, testRetryPolicy)

        val result = provider.chatWithImages("system", "question", emptyList())

        assertEquals(AIResult.Success("ok"), result)
        assertEquals(2, delegate.callCount)
    }

    @Test
    fun `gives up after exhausting retries and returns the last failure`() = runTest {
        val delegate = SequencedAIProvider(listOf(AIResult.Failure(RetryTestError.RateLimited)))
        val provider = RetryingAIProvider(delegate, testRetryPolicy)

        val result = provider.chat("system", "question")

        assertEquals(AIResult.Failure(RetryTestError.RateLimited), result)
        assertEquals(3, delegate.callCount)
    }

    @Test
    fun `does not retry a non-transient failure`() = runTest {
        val delegate = SequencedAIProvider(listOf(AIResult.Failure(RetryTestError.AuthenticationFailed), AIResult.Success("should never be reached")))
        val provider = RetryingAIProvider(delegate, testRetryPolicy)

        val result = provider.chat("system", "question")

        assertEquals(AIResult.Failure(RetryTestError.AuthenticationFailed), result)
        assertEquals(1, delegate.callCount)
    }
}
