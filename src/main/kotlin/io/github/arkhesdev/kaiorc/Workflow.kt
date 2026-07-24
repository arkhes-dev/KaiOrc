package io.github.arkhesdev.kaiorc

/**
 * One reusable AI orchestration pipeline — e.g. "answer a home question" or "extract assets from
 * photos". [TInput]/[TOutput] are deliberately per-workflow generic: different AI features take and
 * return genuinely different shapes, so the runtime never forces them into one common shape.
 * [TError] is fixed per host app (see [AIResult]).
 */
interface Workflow<TInput, TOutput, TError : Any> {
    /** Stable identifier used for the [AIExecutionContext] built around a run — e.g. for observability. */
    val name: String

    suspend fun execute(input: TInput, context: AIExecutionContext): AIResult<TOutput, TError>
}
