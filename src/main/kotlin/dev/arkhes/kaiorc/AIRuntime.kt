package dev.arkhes.kaiorc

import javax.inject.Inject
import javax.inject.Singleton

/**
 * The single entry point every AI feature runs through: "run this [Workflow] with this input."
 * Does no AI work itself — it builds the [AIExecutionContext], hands off to the workflow, and
 * reports what happened to [AIObserver] for local debugging.
 */
@Singleton
class AIRuntime @Inject constructor(
    private val observer: AIObserver
) {
    suspend fun <TInput, TOutput, TError : Any> execute(
        workflow: Workflow<TInput, TOutput, TError>,
        input: TInput
    ): AIResult<TOutput, TError> {
        val startedAt = System.currentTimeMillis()
        val result = workflow.execute(input, AIExecutionContext(workflow.name))
        val durationMs = System.currentTimeMillis() - startedAt

        observer.onWorkflowExecuted(
            when (result) {
                is AIResult.Success -> WorkflowExecutionEvent(workflow.name, durationMs, succeeded = true)
                is AIResult.Failure -> WorkflowExecutionEvent(workflow.name, durationMs, succeeded = false, errorType = result.error::class.simpleName)
            }
        )
        return result
    }
}
