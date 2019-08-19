package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.models.Statement
import dev.thhs.contentiospring.models.exceptions.NoStatementFound
import dev.thhs.contentiospring.models.ui.projects.ProjectItem
import dev.thhs.contentiospring.models.ui.projects.ProjectPage
import dev.thhs.contentiospring.models.ui.projects.SubmissionListItem
import dev.thhs.contentiospring.models.ui.submissions.SubmissionDetails
import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.AssertionError

@RestController
@RequestMapping("ui")
class UiController(
        val projectRepo: AskredditProjectRepository,
        val submissionRepo: SubmissionRepository,
        val statementRepo: StatementRepository,
        val sentenceRepo: SentenceRepository
) {

    @GetMapping("/projects", params = ["page", "size"])
    fun getProjects(
            @RequestParam(value = "page") page: Int,
            @RequestParam(value = "size") size: Int
    ): Page<ProjectItem> {
        return projectRepo.findAll(PageRequest.of(page, size, Sort.by("id").descending())).map {
            val post = statementRepo.findStatementBySubmissionId(it.postId)
            ProjectItem(it.id, post.editedText)
        }
    }

    @GetMapping("projects/{id}")
    fun getProject(@PathVariable id: Long): ResponseEntity<ProjectPage> {
        val project = try {
            projectRepo.findById(id).orElseThrow()
        } catch (err: NoSuchElementException) {
            return ResponseEntity.notFound().build()
        }
        val post = statementRepo.findStatementBySubmissionId(project.postId)
        val submissionItems = submissionRepo.findSubmissionsByProjectId(project.id).map { it ->
            val statement = it.statement ?: throw NoStatementFound()
            val duration = sentenceRepo.findSentencesByStatementSubmissionId(it.id).map { sentence -> sentence.duration }.sum()
            val edited = statement.originalText != statement.editedText
            SubmissionListItem(it.id, it.author, it.score, statement.editedText, duration, edited)
        }
        val projectDuration = submissionItems.map { it.duration }.sum()
        return ResponseEntity.ok(ProjectPage(post.editedText, projectDuration, submissionItems))
    }

    @GetMapping("submissions/{id}")
    fun getSubmission(@PathVariable id: String): ResponseEntity<SubmissionDetails> {
        val submission = try {
            submissionRepo.findById(id).orElseThrow()
        } catch (err: NoSuchElementException) {
            return ResponseEntity.notFound().build()
        }
        val statement = submission.statement ?: throw NoStatementFound()
        return ResponseEntity.ok(SubmissionDetails(submission.id, submission.author, submission.score, statement.originalText, statement.editedText))
    }

}