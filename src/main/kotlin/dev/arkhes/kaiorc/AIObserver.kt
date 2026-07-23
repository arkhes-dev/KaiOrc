package dev.arkhes.kaiorc

/** One completed AI workflow execution — developer diagnostics only, never sent anywhere or collected as analytics. */
data class WorkflowExecutionEvent(
    val workflowName: String,
    val durationMs: Long,
    val succeeded: Boolean,
    /** The failing error's simple class name (e.g. "AiRateLimited"), null on success. */
    val errorType: String? = null
)

fun interface AIObserver {
    fun onWorkflowExecuted(event: WorkflowExecutionEvent)
}
