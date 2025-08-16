package com.fredrickosuala.ncheta.data.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NchetaDataModelsTest {

    private val json = Json { isLenient = true; ignoreUnknownKeys = true }

    @Test
    fun `Flashcard should serialize and deserialize correctly`() {
        // Arrange
        val flashcard = Flashcard(
            front = "What is the capital of France?",
            back = "Paris"
        )

        // Act
        val serialized = json.encodeToString(Flashcard.serializer(), flashcard)
        val deserialized = json.decodeFromString(Flashcard.serializer(), serialized)

        // Assert
        assertEquals(flashcard, deserialized)
        assertEquals("What is the capital of France?", deserialized.front)
        assertEquals("Paris", deserialized.back)
    }

    @Test
    fun `MultipleChoiceQuestion should serialize and deserialize correctly`() {
        // Arrange
        val mcq = MultipleChoiceQuestion(
            questionText = "What is 2 + 2?",
            options = listOf("3", "4", "5", "6"),
            correctOptionIndex = 1
        )

        // Act
        val serialized = json.encodeToString(MultipleChoiceQuestion.serializer(), mcq)
        val deserialized = json.decodeFromString(MultipleChoiceQuestion.serializer(), serialized)

        // Assert
        assertEquals(mcq, deserialized)
        assertEquals("What is 2 + 2?", deserialized.questionText)
        assertEquals(listOf("3", "4", "5", "6"), deserialized.options)
        assertEquals(1, deserialized.correctOptionIndex)
    }

    @Test
    fun `FlashcardResponse should serialize and deserialize correctly`() {
        // Arrange
        val flashcards = listOf(
            Flashcard("Question 1", "Answer 1"),
            Flashcard("Question 2", "Answer 2")
        )
        val response = FlashcardResponse(flashcards)

        // Act
        val serialized = json.encodeToString(FlashcardResponse.serializer(), response)
        val deserialized = json.decodeFromString(FlashcardResponse.serializer(), serialized)

        // Assert
        assertEquals(response, deserialized)
        assertEquals(2, deserialized.flashcards.size)
        assertEquals("Question 1", deserialized.flashcards[0].front)
        assertEquals("Answer 1", deserialized.flashcards[0].back)
    }

    @Test
    fun `McqResponse should serialize and deserialize correctly`() {
        // Arrange
        val questions = listOf(
            MultipleChoiceQuestion(
                questionText = "Question 1",
                options = listOf("A", "B", "C", "D"),
                correctOptionIndex = 0
            )
        )
        val response = McqResponse(questions)

        // Act
        val serialized = json.encodeToString(McqResponse.serializer(), response)
        val deserialized = json.decodeFromString(McqResponse.serializer(), serialized)

        // Assert
        assertEquals(response, deserialized)
        assertEquals(1, deserialized.questions.size)
        assertEquals("Question 1", deserialized.questions[0].questionText)
        assertEquals(0, deserialized.questions[0].correctOptionIndex)
    }

    @Test
    fun `GeneratedContent should serialize and deserialize FlashcardSet correctly`() {
        // Arrange
        val flashcardSet = GeneratedContent.FlashcardSet(
            listOf(Flashcard("Front", "Back"))
        )

        // Act
        val serialized = json.encodeToString(GeneratedContent.serializer(), flashcardSet)
        val deserialized = json.decodeFromString(GeneratedContent.serializer(), serialized)

        // Assert
        assertTrue(deserialized is GeneratedContent.FlashcardSet)
        val deserializedFlashcardSet = deserialized as GeneratedContent.FlashcardSet
        assertEquals(1, deserializedFlashcardSet.items.size)
        assertEquals("Front", deserializedFlashcardSet.items[0].front)
        assertEquals("Back", deserializedFlashcardSet.items[0].back)
    }

    @Test
    fun `GeneratedContent should serialize and deserialize McqSet correctly`() {
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
        val serialized = json.encodeToString(GeneratedContent.serializer(), mcqSet)
        val deserialized = json.decodeFromString(GeneratedContent.serializer(), serialized)

        // Assert
        assertTrue(deserialized is GeneratedContent.McqSet)
        val deserializedMcqSet = deserialized as GeneratedContent.McqSet
        assertEquals(1, deserializedMcqSet.items.size)
        assertEquals("Test Question", deserializedMcqSet.items[0].questionText)
        assertEquals(2, deserializedMcqSet.items[0].correctOptionIndex)
    }

    @Test
    fun `GeneratedContent should serialize and deserialize Summary correctly`() {
        // Arrange
        val summary = GeneratedContent.Summary("This is a test summary")

        // Act
        val serialized = json.encodeToString(GeneratedContent.serializer(), summary)
        val deserialized = json.decodeFromString(GeneratedContent.serializer(), serialized)

        // Assert
        assertTrue(deserialized is GeneratedContent.Summary)
        val deserializedSummary = deserialized as GeneratedContent.Summary
        assertEquals("This is a test summary", deserializedSummary.text)
    }

    @Test
    fun `NchetaEntry should serialize and deserialize correctly`() {
        // Arrange
        val entry = NchetaEntry(
            id = "test-id",
            title = "Test Entry",
            sourceText = "Source text content",
            inputSourceType = InputSourceType.MANUAL,
            content = GeneratedContent.Summary("Summary content")
        )

        // Act
        val serialized = json.encodeToString(NchetaEntry.serializer(), entry)
        val deserialized = json.decodeFromString(NchetaEntry.serializer(), serialized)

        // Assert
        assertEquals(entry.id, deserialized.id)
        assertEquals(entry.title, deserialized.title)
        assertEquals(entry.sourceText, deserialized.sourceText)
        assertEquals(entry.inputSourceType, deserialized.inputSourceType)
        assertTrue(deserialized.content is GeneratedContent.Summary)
    }

    @Test
    fun `InputSourceType enum values should be correct`() {
        // Assert
        assertEquals(3, InputSourceType.values().size)
        assertEquals(InputSourceType.MANUAL, InputSourceType.valueOf("MANUAL"))
        assertEquals(InputSourceType.DOCUMENT, InputSourceType.valueOf("DOCUMENT"))
        assertEquals(InputSourceType.IMAGE, InputSourceType.valueOf("IMAGE"))
    }
} 