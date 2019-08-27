package dev.thhs.contentiospring.services

import dev.thhs.contentiospring.models.*
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.exceptions.NoStatementFound
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import org.springframework.stereotype.Service
import java.io.File

@Service
class MediaStatusService(
        val submissionRepository: SubmissionRepository,
        val sentenceRepository: SentenceRepository
) {


    fun mediaStatus(project: AskredditProject): ProjectMediaStatus {
        val sentences = sentenceRepository.findSentencesByStatementSubmissionProjectId(project.id)
        val sentencesMediaStatus = sentences.map { mediaStatus(it).mediaStatus }.reduce { curr, next -> curr + next }
        val videoStatus = statusFromFiles(File(project.videoPath))
        return ProjectMediaStatus(project.id, sentencesMediaStatus, videoStatus)
    }

    fun mediaStatus(submission: Submission): SubmissionMediaStatus {
        val sentences = sentenceRepository.findSentencesByStatementSubmissionId(submission.id)
        val sentencesMediaStatus = sentences.map { mediaStatus(it).mediaStatus }.reduce { curr, next -> curr + next }
        return SubmissionMediaStatus(submission.id, sentencesMediaStatus)
    }

    fun mediaStatus(sentence: Sentence): SentenceMediaStatus {
        val audioStatus = statusFromFiles(File(sentence.audioPath))
        val slidesStatus = statusFromFiles(File(sentence.slidePath))
        val videoStatus = statusFromFiles(File(sentence.videoPath))
        val mediaStatus = MediaStatus(audioStatus, slidesStatus, videoStatus)
        return SentenceMediaStatus(sentence.id, mediaStatus)
    }

    fun statusFromFiles(file: File): GeneratedFilesStatus = statusFromFiles(listOf(file))

    fun statusFromFiles(files: List<File>): GeneratedFilesStatus {
        val filesByStatus = files.partition { it.exists() }
        return GeneratedFilesStatus(filesByStatus.first.size, filesByStatus.second.size, files.size)
    }
}