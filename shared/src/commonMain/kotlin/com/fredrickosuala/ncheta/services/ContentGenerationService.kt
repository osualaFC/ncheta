package com.fredrickosuala.ncheta.services

import com.fredrickosuala.ncheta.data.model.Flashcard
import com.fredrickosuala.ncheta.data.model.MultipleChoiceQuestion

interface ContentGenerationService {
    /**
     * Generates a summary for the given input text.
     */
    suspend fun generateSummary(textToSummarize: String, apiKey: String): Result<String>

    /**
     * Generates a list of flashcards from the given input text.
     * The response is expected to be a JSON string, which will be parsed.
     */
    suspend fun generateFlashcards(textForFlashcards: String, apiKey: String): Result<List<Flashcard>>

    /**
     * Generates a list of multiple-choice questions from the given input text.
     * The response is expected to be a JSON string, which will be parsed.
     */
    suspend fun generateMcqs(textForMcqs: String, apiKey: String): Result<List<MultipleChoiceQuestion>>
}
