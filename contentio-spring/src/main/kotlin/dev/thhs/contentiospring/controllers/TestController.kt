package dev.thhs.contentiospring.controllers

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.Statement
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.webrequests.InitProjectRequest

import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.services.reddit.AskredditService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("test")
class TestController(val projectRepo: AskredditProjectRepository,
                     val submissionRepo: SubmissionRepository,
                     val statementRepo: StatementRepository,
                     val sentenceRepo: SentenceRepository,
                     val askredditService: AskredditService) {

    @GetMapping("/string")
    fun getTestString(): String {
        return "Test String :)"
    }

    @PostMapping("/createproject")
    fun createProject(@RequestBody request:InitProjectRequest):String {
        askredditService.createProject(request)
        return "success"
    }

    @GetMapping("/projects")
    fun getProjects(): List<AskredditProject> {
        return projectRepo.findAll()
    }

    @GetMapping("/submissions")
    fun getSubmissions(): List<Submission> {
        return submissionRepo.findAll()
    }

    @GetMapping("/statements")
    fun getStatements(): List<Statement> {
        return statementRepo.findAll()
    }

    @GetMapping("/sentences")
    fun getSentences(): List<Sentence> {
        return sentenceRepo.findAll()
    }

    @GetMapping("/sentences/{id}")
    fun getSentencesByProjectId(@PathVariable id: String):List<Sentence>{
        return sentenceRepo.findSentencesByStatementSubmissionProjectId(id.toLong())
    }

}