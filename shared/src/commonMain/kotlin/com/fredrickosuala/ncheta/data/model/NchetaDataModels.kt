package com.fredrickosuala.ncheta.data.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
enum class InputSourceType {
    MANUAL,
    DOCUMENT,
    IMAGE
}


@Serializable
data class Flashcard(
    val front: String,
    val back: String
)


@Serializable
data class MultipleChoiceQuestion(
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int
)

@Serializable
data class FlashcardResponse(val flashcards: List<Flashcard>)

@Serializable
data class McqResponse(val questions: List<MultipleChoiceQuestion>)

@Serializable
sealed interface GeneratedContent {
    @Serializable
    data class FlashcardSet(val items: List<Flashcard>) : GeneratedContent

    @Serializable
    data class McqSet(val items: List<MultipleChoiceQuestion>) : GeneratedContent

    @Serializable
    data class Summary(val text: String) : GeneratedContent
}


@Serializable
data class NchetaEntry(
    val id: String,
    val title: String,
    val sourceText: String,
    val inputSourceType: InputSourceType,
    val content: GeneratedContent,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    var lastPracticedAt: Long? = null
)