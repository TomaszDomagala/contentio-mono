package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.Statement
import dev.thhs.contentiospring.models.SubmissionMediaStatus
import dev.thhs.contentiospring.models.exceptions.NoStatementFound
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.webrequests.ChangeTextRequest
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.services.MediaStatusService
import dev.thhs.contentiospring.services.generators.MediaGenerator
import dev.thhs.contentiospring.services.reddit.AskredditContentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * @property getSubmission used in contentio-slides
 */


@RestController
@RequestMapping("submissions")
class SubmissionsController(
        val statementRepository: StatementRepository,
        val submissionRepository: SubmissionRepository,
        val sentenceRepository: SentenceRepository,
        val mediaStatusService: MediaStatusService,
        val askredditContentService: AskredditContentService,
        val mediaGenerator: MediaGenerator
) {

    @GetMapping
    fun getSubmissions(): List<Submission> {
        return submissionRepository.findAll()
    }

    @GetMapping("/{id}")
    fun getSubmission(@PathVariable id: String): Submission {
        return submissionRepository.findById(id).get()
    }

    @GetMapping("/{id}/statement")
    fun getSubmissionStatement(@PathVariable id: String): Statement {
        return statementRepository.findStatementBySubmissionId(id)
    }

    @PutMapping("/{id}/text")
    fun updateSubmissionStatementText(
            @PathVariable id: String,
            @RequestBody newTextRequest: ChangeTextRequest
    ): ResponseEntity<*> {
        val submission = submissionRepository.findById(id).orElseThrow()
        val statement: Statement = submission?.statement ?: throw NoStatementFound()
        statement.editedText = newTextRequest.newText
        statementRepository.save(statement)

        val oldSentences = sentenceRepository.findSentencesByStatementSubmissionId(id)
        mediaGenerator.clearSentencesMedia(oldSentences)
        sentenceRepository.deleteAll(oldSentences)

        askredditContentService.createSentences(statement)
        return ResponseEntity.ok(object {})
    }


    @GetMapping("/{id}/sentences")
    fun getSubmissionSentences(@PathVariable id: String): List<Sentence> {
        return sentenceRepository.findSentencesByStatementSubmissionId(id)
    }

    @GetMapping("/{id}/mediastatus")
    fun getSubmissionMediaStatus(@PathVariable id: String): ResponseEntity<SubmissionMediaStatus> {
        val submission = try {
            submissionRepository.findById(id).orElseThrow()
        } catch (err: NoSuchElementException) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(mediaStatusService.mediaStatus(submission))
    }

}