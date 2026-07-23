package dev.arkhes.kaiorc

/**
 * Per-run metadata threaded through a [Workflow] execution — deliberately minimal for now.
 * Grows only when a real workflow needs shared state across its steps, not speculatively ahead
 * of that need.
 */
data class AIExecutionContext(val workflowName: String)
