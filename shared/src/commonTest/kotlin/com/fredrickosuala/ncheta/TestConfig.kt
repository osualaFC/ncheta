package com.fredrickosuala.ncheta

import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Test configuration and utilities for the Ncheta project
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
object TestConfig {
    
    /**
     * Default test dispatcher for coroutines testing
     */
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    
    /**
     * Test timeout for long-running operations
     */
    const val TEST_TIMEOUT = 5000L
    
    /**
     * Sample test data for flashcards
     */
    val sampleFlashcards = listOf(
        com.fredrickosuala.ncheta.data.model.Flashcard(
            front = "What is the capital of France?",
            back = "Paris"
        ),
        com.fredrickosuala.ncheta.data.model.Flashcard(
            front = "What is 2 + 2?",
            back = "4"
        )
    )
    
    /**
     * Sample test data for multiple choice questions
     */
    val sampleMcqs = listOf(
        com.fredrickosuala.ncheta.data.model.MultipleChoiceQuestion(
            questionText = "What is the capital of France?",
            options = listOf("London", "Paris", "Berlin", "Madrid"),
            correctOptionIndex = 1
        )
    )
    
    /**
     * Sample test data for Ncheta entries
     */
    val sampleNchetaEntry = com.fredrickosuala.ncheta.data.model.NchetaEntry(
        id = "test-entry-id",
        title = "Test Entry",
        sourceText = "This is a test source text for testing purposes.",
        inputSourceType = com.fredrickosuala.ncheta.data.model.InputSourceType.MANUAL,
        content = com.fredrickosuala.ncheta.data.model.GeneratedContent.Summary("This is a test summary.")
    )
    
    /**
     * Sample API key for testing
     */
    const val SAMPLE_API_KEY = "test-api-key-12345"
    
    /**
     * Sample error messages for testing
     */
    object ErrorMessages {
        const val API_KEY_MISSING = "API Key is missing. Please add it in settings."
        const val INPUT_TEXT_EMPTY = "Input text cannot be empty."
        const val TITLE_EMPTY = "Title cannot be empty."
        const val NO_CONTENT_AVAILABLE = "No generated content is available to save."
    }
} 