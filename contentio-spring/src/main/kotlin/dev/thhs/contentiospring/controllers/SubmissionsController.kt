package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.Statement
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @property getSubmission used in contentio-slides
 */


@RestController
@RequestMapping("submissions")
class SubmissionsController(val statementRepository: StatementRepository, val submissionRepository: SubmissionRepository, val sentenceRepository: SentenceRepository) {

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

    @GetMapping("/{id}/sentences")
    fun getSubmissionSentences(@PathVariable id: String): List<Sentence> {
        return sentenceRepository.findSentencesByStatementSubmissionId(id)
    }

}