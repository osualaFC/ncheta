package com.fredrickosuala.ncheta.services

interface ContentGenerationService {
    /**
     * Generates a summary for the given input text using the provided API key.
     * @param textToSummarize The text content to be summarized.
     * @param apiKey The user's API key for the generation service (e.g., Gemini API key).
     * @return A [GenerationResult] containing the summary or an error message.
     */
    suspend fun generateSummary(textToSummarize: String, apiKey: String): GenerationResult
}
