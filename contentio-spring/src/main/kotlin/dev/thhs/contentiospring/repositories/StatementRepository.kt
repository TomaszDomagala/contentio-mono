package dev.thhs.contentiospring.repositories

import dev.thhs.contentiospring.models.Statement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StatementRepository : JpaRepository<Statement, Long> {

    fun findStatementBySubmissionId(submissionId: String): Statement

    @Query("select s from Statement s where s.submission.type = dev.thhs.contentiospring.models.reddit.SubmissionType.POST and s.submission.project.id = :id")
    fun findPostStatementByProjectId(@Param("id") id: Long): Statement

    fun findStatementsBySubmissionProjectId(id: Long): List<Statement>
}