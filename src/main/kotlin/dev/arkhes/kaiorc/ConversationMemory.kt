package dev.arkhes.kaiorc

import javax.inject.Inject

/** One remembered question/answer exchange. */
data class ConversationTurn(val question: String, val answer: String)

/**
 * Short-term, session-scoped memory of recent conversation turns — not persisted, not long-term.
 * Lets a workflow resolve references like "it"/"them" to what was just discussed, without
 * threading full chat history through every layer of the runtime as a parameter.
 */
interface ConversationMemory {
    fun recentTurns(): List<ConversationTurn>
    fun record(turn: ConversationTurn)
}

/** Bounded ring buffer — keeps only the last [MAX_TURNS] exchanges, a fixed turn count rather than a token budget for simplicity. */
class InMemoryConversationMemory @Inject constructor() : ConversationMemory {
    private val turns = ArrayDeque<ConversationTurn>()

    override fun recentTurns(): List<ConversationTurn> = turns.toList()

    override fun record(turn: ConversationTurn) {
        turns.addLast(turn)
        while (turns.size > MAX_TURNS) turns.removeFirst()
    }

    private companion object {
        const val MAX_TURNS = 6
    }
}
