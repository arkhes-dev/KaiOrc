package io.github.arkhesdev.kaiorc

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private sealed class RuntimeTestError {
    data object NotConfigured : RuntimeTestError()
    data object RateLimited : RuntimeTestError()
}

private class RecordingWorkflow(
    override val name: String = "test-workflow",
    private val result: AIResult<String, RuntimeTestError> = AIResult.Success("ok")
) : Workflow<String, String, RuntimeTestError> {
    var receivedInput: String? = null
    var receivedContext: AIExecutionContext? = null

    override suspend fun execute(input: String, context: AIExecutionContext): AIResult<String, RuntimeTestError> {
        receivedInput = input
        receivedContext = context
        return result
    }
}

private class RecordingAIObserver : AIObserver {
    val events = mutableListOf<WorkflowExecutionEvent>()
    override fun onWorkflowExecuted(event: WorkflowExecutionEvent) {
        events += event
    }
}

class AIRuntimeTest {

    @Test
    fun `builds an execution context named after the workflow and forwards the input`() = runBlocking {
        val workflow = RecordingWorkflow(name = "ask-aarizo")
        val runtime = AIRuntime(RecordingAIObserver())

        runtime.execute(workflow, "What's due for maintenance?")

        assertEquals("What's due for maintenance?", workflow.receivedInput)
        assertEquals(AIExecutionContext("ask-aarizo"), workflow.receivedContext)
    }

    @Test
    fun `returns the workflow's result unchanged on success`() = runBlocking {
        val workflow = RecordingWorkflow(result = AIResult.Success("42 days left on warranty"))
        val runtime = AIRuntime(RecordingAIObserver())

        val result = runtime.execute(workflow, "question")

        assertTrue(result is AIResult.Success)
        assertEquals("42 days left on warranty", (result as AIResult.Success).value)
    }

    @Test
    fun `returns the workflow's failure unchanged`() = runBlocking {
        val workflow = RecordingWorkflow(result = AIResult.Failure(RuntimeTestError.NotConfigured))
        val runtime = AIRuntime(RecordingAIObserver())

        val result = runtime.execute(workflow, "question")

        assertEquals(AIResult.Failure(RuntimeTestError.NotConfigured), result)
    }

    @Test
    fun `reports a successful execution to the observer with no error type`() = runBlocking {
        val observer = RecordingAIObserver()
        val workflow = RecordingWorkflow(name = "ask-aarizo", result = AIResult.Success("ok"))

        AIRuntime(observer).execute(workflow, "question")

        val event = observer.events.single()
        assertEquals("ask-aarizo", event.workflowName)
        assertTrue(event.succeeded)
        assertNull(event.errorType)
        assertTrue(event.durationMs >= 0)
    }

    @Test
    fun `reports a failed execution to the observer with the error's simple class name`() = runBlocking {
        val observer = RecordingAIObserver()
        val workflow = RecordingWorkflow(name = "smart-capture", result = AIResult.Failure(RuntimeTestError.RateLimited))

        AIRuntime(observer).execute(workflow, "question")

        val event = observer.events.single()
        assertEquals("smart-capture", event.workflowName)
        assertTrue(!event.succeeded)
        assertEquals("RateLimited", event.errorType)
    }
}
