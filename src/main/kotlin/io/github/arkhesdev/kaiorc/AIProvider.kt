package io.github.arkhesdev.kaiorc

/**
 * The one seam a [Workflow] uses to talk to "the AI" — deliberately tiny. Everything provider-
 * specific (which provider is active, resolving its config, checking connectivity first) lives
 * behind this interface in the host app's implementation, so a workflow never has to know which
 * concrete provider is answering it.
 */
interface AIProvider<TError : Any> {
    suspend fun chat(systemPrompt: String, userMessage: String): AIResult<String, TError>

    /** [images] is a list of (base64, mimeType) pairs, in the same order they should be described in [userMessage]. */
    suspend fun chatWithImages(
        systemPrompt: String,
        userMessage: String,
        images: List<Pair<String, String>>
    ): AIResult<String, TError>
}
