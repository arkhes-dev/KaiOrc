package dev.arkhes.kaiorc

/**
 * Classifies raw input into an intent before a [Workflow] is chosen. Deliberately just a decision
 * function: it does not execute anything itself, and it knows nothing about which workflow an
 * intent maps to (that's the caller's job).
 */
fun interface IntentRouter<TInput, TIntent> {
    suspend fun route(input: TInput): TIntent
}
