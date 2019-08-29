package dev.thhs.contentiospring.repositories

import dev.thhs.contentiospring.models.Sentence
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SentenceRepository : JpaRepository<Sentence, Long> {
    fun findSentencesByStatementId(statementId:Long): List<Sentence>

    fun findSentencesByStatementSubmissionId(submissionId: String): List<Sentence>

    fun findSentencesByStatementSubmissionProjectId(projectId: Long): List<Sentence>
}