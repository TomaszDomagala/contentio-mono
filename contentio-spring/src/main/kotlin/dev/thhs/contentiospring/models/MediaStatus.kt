package dev.thhs.contentiospring.models

import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.exceptions.NoStatementFound
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.repositories.SentenceRepository
import org.springframework.stereotype.Service
import java.io.File

data class GeneratedFilesStatus(val generated: Int, val missing: Int, val all: Int) {
    operator fun plus(status: GeneratedFilesStatus) = GeneratedFilesStatus(
            generated + status.generated,
            missing + status.missing,
            all + status.all
    )
}

data class MediaStatus(
        val audioStatus: GeneratedFilesStatus,
        val slidesStatus: GeneratedFilesStatus,
        val videoStatus: GeneratedFilesStatus
) {
    operator fun plus(media: MediaStatus) = MediaStatus(
            audioStatus + media.audioStatus,
            slidesStatus + media.slidesStatus,
            videoStatus + media.videoStatus
    )
}

data class ProjectMediaStatus(
        val id: Long,
        val submissionsVideosStatus: GeneratedFilesStatus,
        val sentencesMediaStatus: MediaStatus,
        val videoStatus: GeneratedFilesStatus
)

data class SubmissionMediaStatus(val id: String, val sentencesMediaStatus: MediaStatus, val videoStatus: GeneratedFilesStatus)

data class SentenceMediaStatus(val id: Long, val mediaStatus: MediaStatus)









