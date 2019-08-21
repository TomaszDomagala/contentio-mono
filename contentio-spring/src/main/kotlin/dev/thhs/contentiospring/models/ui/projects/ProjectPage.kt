package dev.thhs.contentiospring.models.ui.projects

import dev.thhs.contentiospring.models.reddit.Submission


data class ProjectPage(val title: String, val predictedDuration: Float, val audioDuration: Float, val submissions: List<SubmissionListItem>)

data class SubmissionListItem(val id: String, val author: String, val score: Int, val text: String, val predictedDuration: Float, val audioDuration: Float, val edited: Boolean)
