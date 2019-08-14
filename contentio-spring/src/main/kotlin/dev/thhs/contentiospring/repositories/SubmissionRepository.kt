package dev.thhs.contentiospring.repositories

import dev.thhs.contentiospring.models.reddit.Submission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubmissionRepository : JpaRepository<Submission, String> {
    fun findSubmissionsByProjectId(projectId: Long): List<Submission>
}