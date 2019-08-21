package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.models.exceptions.NoStatementFound
import dev.thhs.contentiospring.models.ui.projects.ProjectItem
import dev.thhs.contentiospring.models.ui.projects.ProjectPage
import dev.thhs.contentiospring.models.ui.projects.SubmissionListItem
import dev.thhs.contentiospring.models.ui.submissions.SentenceDetails
import dev.thhs.contentiospring.models.ui.submissions.SubmissionDetails
import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import okhttp3.Response
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File

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
            val edited = statement.originalText != statement.editedText
            val durations = sentenceRepo.findSentencesByStatementSubmissionId(it.id)
                    .map { sentence -> Pair(sentence.predictedDuration, sentence.audioDuration) }
                    .reduce { acc, next -> Pair(acc.first + next.first, acc.second + next.second) }

            SubmissionListItem(it.id, it.author, it.score, statement.editedText, durations.first, durations.second, edited)
        }
        val predictedDuration = submissionItems.map { it.predictedDuration }.sum()
        val audioDuration = submissionItems.map { it.audioDuration }.sum()
        return ResponseEntity.ok(ProjectPage(post.editedText, predictedDuration, audioDuration, submissionItems))
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

    @GetMapping("submissions/{id}/sentences")
    fun getSubmissionSentences(@PathVariable id: String): ResponseEntity<List<SentenceDetails>> {
        val sentences = sentenceRepo.findSentencesByStatementSubmissionId(id)
        val details = sentences.map {
            val isAudioGenerated = File(it.audioPath).exists()
            val isSlideGenerated = File(it.slidePath).exists()
            SentenceDetails(it.id, it.text, isAudioGenerated, isSlideGenerated)
        }
        return ResponseEntity.ok(details)

    }

    @GetMapping("sentences/{id}/slide")
    fun getSentenceSlide(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val sentence = try {
            sentenceRepo.findById(id).orElseThrow()
        } catch (err: NoSuchElementException) {
            return ResponseEntity.notFound().build()
        }
        val slideFile = File(sentence.slidePath)
        if (!slideFile.exists()) return ResponseEntity.noContent().build()
        val slide: ByteArray = slideFile.inputStream().readAllBytes()
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_PNG
        headers.contentLength = slide.size.toLong()

        return ResponseEntity(slide, headers, HttpStatus.OK)
    }

}