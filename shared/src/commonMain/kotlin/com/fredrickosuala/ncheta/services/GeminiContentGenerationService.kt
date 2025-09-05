package com.fredrickosuala.ncheta.services

import com.fredrickosuala.ncheta.data.model.*
import kotlinx.serialization.json.Json
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import okio.ByteString.Companion.toByteString


class GeminiContentGenerationService(
    private val modelName: String = "gemini-1.5-flash-latest"
) : ContentGenerationService {

    private val json = Json { isLenient = true; ignoreUnknownKeys = true }

    private val httpClient = createHttpClient()

    override suspend fun generateSummary(textToSummarize: String, apiKey: String): Result<String> {
        if (textToSummarize.isBlank()) return Result.Error("Input text cannot be empty.")
        try {
            val generativeModel = GenerativeModel(modelName, apiKey)
            val prompt = "Provide a concise summary of the following text:\n\n\"${textToSummarize}\""
            val response = generativeModel.generateContent(prompt)
            return response.text?.let { Result.Success(it) }
                ?: Result.Error("Failed to generate summary. The response was empty.")
        } catch (e: Exception) {
            return Result.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun generateFlashcards(textForFlashcards: String, apiKey: String): Result<List<Flashcard>> {
        val prompt = """
            Analyze the following text and generate a set of flashcards from its key concepts.
            Return the response ONLY as a valid JSON object with a single key "flashcards", which contains an array of objects.
            Each object in the array must have two keys: "front" and "back".
            Do not include any other text, explanations, or markdown formatting in your response.

            Here is the text:
            ---
            $textForFlashcards
            ---
        """.trimIndent()
        if (textForFlashcards.isBlank()) return Result.Error("Input text cannot be empty.")
        try {
            val generativeModel = GenerativeModel(
                modelName = modelName,
                apiKey = apiKey,
                generationConfig = generationConfig { responseMimeType = "application/json" }
            )
            val responseText = generativeModel.generateContent(prompt).text
                ?: return Result.Error("Failed to generate flashcards. The response was empty.")

            val cleanedJson = responseText.substringAfter("```json").substringBeforeLast("```").trim()

            val flashcardResponse = json.decodeFromString<FlashcardResponse>(cleanedJson)
            return Result.Success(flashcardResponse.flashcards)

        } catch (e: Exception) {
            println("Flashcard Generation Error: ${e.message}")
            return Result.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun generateMcqs(textForMcqs: String, apiKey: String): Result<List<MultipleChoiceQuestion>> {

        val prompt = """
            Analyze the following text and generate a set of multiple-choice questions to test understanding of its key concepts.
            Return the response ONLY as a valid JSON object with a single key "questions", which contains an array of objects.
            Each object in the array must have three keys: "questionText" (string), "options" (an array of 4 strings), and "correctOptionIndex" (an integer from 0 to 3).
            Do not include any other text, explanations, or markdown formatting in your response.

            Here is the text:
            ---
            $textForMcqs
            ---
        """.trimIndent()

        if (textForMcqs.isBlank()) return Result.Error("Input text cannot be empty.")

        try {
            val generativeModel = GenerativeModel(modelName, apiKey)
            val responseText = generativeModel.generateContent(prompt).text
                ?: return Result.Error("Failed to generate MCQs. The response was empty.")

            val cleanedJson = responseText.substringAfter("```json").substringBeforeLast("```").trim()

            val mcqResponse = json.decodeFromString<McqResponse>(cleanedJson)
            return Result.Success(mcqResponse.questions)

        } catch (e: Exception) {
            println("MCQ Generation Error: ${e.message}")
            return Result.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun getTextFromImage(
        imageData: ByteArray,
        apiKey: String
    ): Result<String> {

        if (apiKey.isBlank()) return Result.Error("API key is missing.")
        if (imageData.isEmpty()) return Result.Error("Image data cannot be empty.")

        try {

            val generativeModel = GenerativeModel(modelName = modelName, apiKey = apiKey)

            val prompt = content {
                image(imageData)
                text("Extract all the text from this image. If there is no text, respond with an empty string.")
            }

            val response = generativeModel.generateContent(prompt)

            return response.text?.let { Result.Success(it.trim()) }
                ?: Result.Error("Failed to extract text from the image.")

        } catch (e: Exception) {
            println("Gemini Image Error: ${e.message}")
            return Result.Error(e.message ?: "An unknown error occurred during image processing.")
        }
    }

    override suspend fun transcribeAudio(audioData: ByteArray, mimeType: String, apiKey: String): Result<String> {
        val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

        try {
            val encodedAudio = audioData.toByteString().base64()

            val requestBody = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(inlineData = InlineData(mimeType = mimeType, data = encodedAudio)),
                            Part(text = "Transcribe this audio recording. Respond only with the transcribed text.")
                        )
                    )
                )
            )


            val response: GeminiResponse = httpClient.post(baseUrl) {
                url { parameters.append("key", apiKey) }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            // 6. Extract the text from the response
            val text = response.candidates?.firstOrNull()?.text
            return if (text != null) {
                Result.Success(text.trim())
            } else {
                Result.Error(response.promptFeedback?.blockReason ?: "Failed to transcribe audio.")
            }

        } catch (e: Exception) {
            println("Ktor/Gemini Error: ${e.message}")
            return Result.Error(e.message ?: "An unknown network error occurred.")
        }
    }


}