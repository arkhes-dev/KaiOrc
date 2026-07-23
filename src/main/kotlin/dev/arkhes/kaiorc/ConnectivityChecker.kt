package dev.arkhes.kaiorc

/** Thin seam over the host platform's connectivity check so an [AIProvider] can be unit-tested without a real network stack. */
fun interface ConnectivityChecker {
    fun hasInternet(): Boolean
}
