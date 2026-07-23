package dev.arkhes.kaiorc

/** Decides whether a given [TError] is worth retrying — host-app-owned, since only the app knows which of its own error cases are transient (rate limiting, no connection) vs. permanent (bad credentials, a rejected response). */
fun interface RetryPolicy<TError : Any> {
    fun isRetryable(error: TError): Boolean
}
