package com.fredrickosuala.ncheta.services

import kotlinx.serialization.Serializable

// --- Request Models ---

@Serializable
data class GeminiRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String // Base64-encoded string
)

// --- Response Models ---

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val promptFeedback: PromptFeedback? = null
)

@Serializable
data class Candidate(
    val content: Content?,
    val finishReason: String? = null
) {
    // Helper property to easily get the text from the first part
    val text: String?
        get() = content?.parts?.firstOrNull()?.text
}

@Serializable
data class PromptFeedback(
    val blockReason: String? = null
)