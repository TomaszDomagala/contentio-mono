package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.models.ProjectMediaStatus
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.webrequests.InitProjectRequest
import dev.thhs.contentiospring.models.webrequests.InitProjectResponse
import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.services.MediaStatusService
import dev.thhs.contentiospring.services.reddit.AskredditContentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("projects")
class ProjectsController(
        val projectRepo: AskredditProjectRepository,
        val statementRepo: StatementRepository,
        val submissionRepo: SubmissionRepository,
        val sentenceRepository: SentenceRepository,
        val contentService: AskredditContentService,
        val mediaStatusService: MediaStatusService
) {

    @PostMapping("/init")
    fun initAsyncProject(@RequestBody request: InitProjectRequest): InitProjectResponse {
        return contentService.initProject(request)
    }

    @GetMapping("")
    fun getProjects(): List<AskredditProject> {
        return projectRepo.findAll()
    }


    @GetMapping("/page", params = ["page", "size"])
    fun getProjectsPage(
            @RequestParam(value = "page") page: Int,
            @RequestParam(value = "size") size: Int
    ): Page<AskredditProject> {
        return projectRepo.findAll(PageRequest.of(page, size, Sort.by("id").descending()))
    }


    @GetMapping("/{id}/title")
    fun getProjectsPostTitle(@PathVariable id: Long): String {
        return statementRepo.findPostStatementByProjectId(id).originalText
    }

    @GetMapping("/{id}/submissions")
    fun getProjectSubmissions(@PathVariable id: Long): List<Submission> {
        return submissionRepo.findSubmissionsByProjectId(id)
    }

    @GetMapping("/{id}/mediastatus")
    fun getProjectMediaStatus(@PathVariable id: Long): ResponseEntity<ProjectMediaStatus> {
        val project = try {
            projectRepo.findById(id).orElseThrow()
        } catch (err: NoSuchElementException) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(mediaStatusService.mediaStatus(project))
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: Long): String {
        val submissions = submissionRepo.findSubmissionsByProjectId(id)
        val statements = statementRepo.findStatementsBySubmissionProjectId(id)
        val sentences = sentenceRepository.findSentencesByStatementSubmissionProjectId(id)

        sentenceRepository.deleteAll(sentences)
        statementRepo.deleteAll(statements)
        submissionRepo.deleteAll(submissions)
        projectRepo.deleteById(id)

        return "deleted (?) $id project"
    }

}