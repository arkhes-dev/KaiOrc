package com.kaiorc

/**
 * Turns a raw AI reply into a trusted [TOutput], or an [AIResult.Failure] if it doesn't match what
 * the workflow expected — never let malformed/hallucinated output reach the rest of the app unchecked.
 */
fun interface ResponseValidator<TOutput, TError : Any> {
    fun validate(rawResponse: String): AIResult<TOutput, TError>
}
