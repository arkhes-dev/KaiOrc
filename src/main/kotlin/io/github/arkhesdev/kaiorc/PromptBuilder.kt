package io.github.arkhesdev.kaiorc

/**
 * Composes the final system prompt from a [ContextBuilder]'s output — the one place a workflow's
 * prompt text is assembled, instead of concatenating strings ad hoc at each call site.
 */
fun interface PromptBuilder<TContext> {
    fun buildSystemPrompt(context: TContext): String
}
