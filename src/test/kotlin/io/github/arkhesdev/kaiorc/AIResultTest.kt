package io.github.arkhesdev.kaiorc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private sealed class ResultTestError {
    data object Broken : ResultTestError()
}

class AIResultTest {

    @Test
    fun `onSuccess runs the action with the value and returns the same result`() {
        var received: String? = null
        val result: AIResult<String, ResultTestError> = AIResult.Success("value")

        val returned = result.onSuccess { received = it }

        assertEquals("value", received)
        assertEquals(result, returned)
    }

    @Test
    fun `onSuccess does not run on a failure`() {
        var ran = false
        val result: AIResult<String, ResultTestError> = AIResult.Failure(ResultTestError.Broken)

        result.onSuccess { ran = true }

        assertFalse(ran)
    }

    @Test
    fun `onFailure runs the action with the error and returns the same result`() {
        var received: ResultTestError? = null
        val result: AIResult<String, ResultTestError> = AIResult.Failure(ResultTestError.Broken)

        val returned = result.onFailure { received = it }

        assertEquals(ResultTestError.Broken, received)
        assertEquals(result, returned)
    }

    @Test
    fun `onFailure does not run on a success`() {
        var ran = false
        val result: AIResult<String, ResultTestError> = AIResult.Success("value")

        result.onFailure { ran = true }

        assertFalse(ran)
    }

    @Test
    fun `map transforms a success value`() {
        val result: AIResult<Int, ResultTestError> = AIResult.Success(2)

        val mapped = result.map { it * 10 }

        assertEquals(AIResult.Success(20), mapped)
    }

    @Test
    fun `map passes a failure through unchanged`() {
        val result: AIResult<Int, ResultTestError> = AIResult.Failure(ResultTestError.Broken)

        val mapped = result.map { it * 10 }

        assertEquals(AIResult.Failure(ResultTestError.Broken), mapped)
    }

    @Test
    fun `map does not invoke the transform on a failure`() {
        val result: AIResult<Int, ResultTestError> = AIResult.Failure(ResultTestError.Broken)
        var invoked = false

        result.map {
            invoked = true
            it
        }

        assertTrue(!invoked)
    }
}
