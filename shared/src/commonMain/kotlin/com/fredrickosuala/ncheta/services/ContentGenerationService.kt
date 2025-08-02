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

    /**
     * Extracts text from the given image data using a multimodal model.
     * @param imageData The platform-agnostic byte array of the image.
     * @param apiKey The user's API key.
     * @return A Result containing the extracted text or an error.
     */
    suspend fun getTextFromImage(imageData: ByteArray, apiKey: String): Result<String>
}
