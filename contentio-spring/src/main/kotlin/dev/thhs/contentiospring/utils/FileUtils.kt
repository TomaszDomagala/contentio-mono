package dev.thhs.contentiospring.utils

import dev.thhs.contentiospring.models.reddit.Submission
import java.io.File

fun createSubmissionDir(submission: Submission, category: String): File {
    val projectDir = File(submission.project.projectPath)
    val categoryDir = File(projectDir, category)
    val submissionDir = File(categoryDir, submission.id)
    submissionDir.mkdirs()
    return submissionDir
}