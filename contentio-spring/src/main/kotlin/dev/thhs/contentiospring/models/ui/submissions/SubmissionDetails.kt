package dev.thhs.contentiospring.models.ui.submissions


data class SubmissionDetails(
        val id: String,
        val author: String,
        val score: Int,
        val originalText: String,
        val editedText: String
)

data class SentenceDetails(
        val id: Long,
        val index: Int,
        val text: String,
        val isAudioGenerated: Boolean,
        val isSlideGenerated: Boolean
)