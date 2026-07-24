package io.github.arkhesdev.kaiorc

/**
 * Assembles the local, domain-specific data a [Workflow] needs before it can prompt the AI. The AI
 * never queries a repository directly; it only ever sees what a [ContextBuilder] decided to hand it.
 */
fun interface ContextBuilder<TInput, TContext> {
    suspend fun build(input: TInput): TContext
}
