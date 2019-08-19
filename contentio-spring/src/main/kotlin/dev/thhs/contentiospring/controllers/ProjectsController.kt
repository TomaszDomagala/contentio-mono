package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.webrequests.InitProjectRequest
import dev.thhs.contentiospring.models.webrequests.InitProjectResponse
import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.services.reddit.AskredditContentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("projects")
class ProjectsController(
        val projectRepo: AskredditProjectRepository,
        val statementRepo: StatementRepository,
        val submissionRepo: SubmissionRepository,
        val contentService: AskredditContentService
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

}