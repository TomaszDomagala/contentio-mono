package dev.thhs.contentiospring.models.ui.projects


data class ProjectPage(
        val id: Long,
        val title: String,
        val predictedDuration: Float,
        val audioDuration: Float,
        val submissions: List<SubmissionListItem>
)

data class SubmissionListItem(
        val id: String,
        val author: String,
        val score: Int,
        val text: String,
        val ignore: Boolean,
        val predictedDuration: Float,
        val audioDuration: Float,
        val edited: Boolean,
        val orderInProject: Int
)
