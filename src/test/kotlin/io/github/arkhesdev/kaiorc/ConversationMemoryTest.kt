package io.github.arkhesdev.kaiorc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversationMemoryTest {

    @Test
    fun `starts empty`() {
        val memory = InMemoryConversationMemory()

        assertTrue(memory.recentTurns().isEmpty())
    }

    @Test
    fun `records a turn and returns it in recentTurns`() {
        val memory = InMemoryConversationMemory()
        val turn = ConversationTurn(question = "what's due?", answer = "nothing this week")

        memory.record(turn)

        assertEquals(listOf(turn), memory.recentTurns())
    }

    @Test
    fun `keeps turns in the order they were recorded`() {
        val memory = InMemoryConversationMemory()
        val first = ConversationTurn("q1", "a1")
        val second = ConversationTurn("q2", "a2")

        memory.record(first)
        memory.record(second)

        assertEquals(listOf(first, second), memory.recentTurns())
    }

    @Test
    fun `drops the oldest turn once more than six are recorded`() {
        val memory = InMemoryConversationMemory()
        val turns = (1..7).map { ConversationTurn("q$it", "a$it") }

        turns.forEach(memory::record)

        assertEquals(turns.drop(1), memory.recentTurns())
    }

    @Test
    fun `never holds more than six turns`() {
        val memory = InMemoryConversationMemory()

        repeat(20) { memory.record(ConversationTurn("q$it", "a$it")) }

        assertEquals(6, memory.recentTurns().size)
    }
}
