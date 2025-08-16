package com.fredrickosuala.ncheta.database

import com.fredrickosuala.ncheta.data.model.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatabaseTest {

    private val json = Json { isLenient = true; ignoreUnknownKeys = true }

    @Test
    fun `InputSourceType adapter should encode and decode correctly`() {
        // Arrange
        val inputSourceType = InputSourceType.MANUAL

        // Act
        val encoded = inputSourceType.name
        val decoded = InputSourceType.valueOf(encoded)

        // Assert
        assertEquals(inputSourceType, decoded)
        assertEquals("MANUAL", encoded)
    }

    @Test
    fun `InputSourceType adapter should handle all enum values`() {
        // Test all enum values
        InputSourceType.values().forEach { inputSourceType ->
            val encoded = inputSourceType.name
            val decoded = InputSourceType.valueOf(encoded)
            assertEquals(inputSourceType, decoded)
        }
    }

    @Test
    fun `GeneratedContent adapter should encode and decode Summary correctly`() {
        // Arrange
        val summary = GeneratedContent.Summary("Test summary content")

        // Act
        val encoded = json.encodeToString(GeneratedContent.serializer(), summary)
        val decoded = json.decodeFromString(GeneratedContent.serializer(), encoded)

        // Assert
        assertTrue(decoded is GeneratedContent.Summary)
        assertEquals(summary.text, (decoded as GeneratedContent.Summary).text)
    }

    @Test
    fun `GeneratedContent adapter should encode and decode FlashcardSet correctly`() {
        // Arrange
        val flashcardSet = GeneratedContent.FlashcardSet(
            listOf(
                Flashcard("Question 1", "Answer 1"),
                Flashcard("Question 2", "Answer 2")
            )
        )

        // Act
        val encoded = json.encodeToString(GeneratedContent.serializer(), flashcardSet)
        val decoded = json.decodeFromString(GeneratedContent.serializer(), encoded)

        // Assert
        assertTrue(decoded is GeneratedContent.FlashcardSet)
        val decodedFlashcardSet = decoded as GeneratedContent.FlashcardSet
        assertEquals(2, decodedFlashcardSet.items.size)
        assertEquals("Question 1", decodedFlashcardSet.items[0].front)
        assertEquals("Answer 1", decodedFlashcardSet.items[0].back)
    }

    @Test
    fun `GeneratedContent adapter should encode and decode McqSet correctly`() {
        // Arrange
        val mcqSet = GeneratedContent.McqSet(
            listOf(
                MultipleChoiceQuestion(
                    questionText = "Test Question",
                    options = listOf("A", "B", "C", "D"),
                    correctOptionIndex = 2
                )
            )
        )

        // Act
        val encoded = json.encodeToString(GeneratedContent.serializer(), mcqSet)
        val decoded = json.decodeFromString(GeneratedContent.serializer(), encoded)

        // Assert
        assertTrue(decoded is GeneratedContent.McqSet)
        val decodedMcqSet = decoded as GeneratedContent.McqSet
        assertEquals(1, decodedMcqSet.items.size)
        assertEquals("Test Question", decodedMcqSet.items[0].questionText)
        assertEquals(2, decodedMcqSet.items[0].correctOptionIndex)
    }

    @Test
    fun `GeneratedContent adapter should handle complex nested structures`() {
        // Arrange
        val complexFlashcardSet = GeneratedContent.FlashcardSet(
            listOf(
                Flashcard(
                    front = "What is the capital of France?",
                    back = "Paris is the capital and largest city of France."
                ),
                Flashcard(
                    front = "What is 2 + 2?",
                    back = "4"
                )
            )
        )

        // Act
        val encoded = json.encodeToString(GeneratedContent.serializer(), complexFlashcardSet)
        val decoded = json.decodeFromString(GeneratedContent.serializer(), encoded)

        // Assert
        assertTrue(decoded is GeneratedContent.FlashcardSet)
        val decodedFlashcardSet = decoded as GeneratedContent.FlashcardSet
        assertEquals(2, decodedFlashcardSet.items.size)
        assertEquals("What is the capital of France?", decodedFlashcardSet.items[0].front)
        assertEquals("Paris is the capital and largest city of France.", decodedFlashcardSet.items[0].back)
    }
} 