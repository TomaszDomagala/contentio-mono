package dev.thhs.contentiospring.services.reddit

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.Statement
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.exceptions.InvalidSubmissionUrl
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.reddit.SubmissionType
import dev.thhs.contentiospring.models.webrequests.InitProjectRequest
import dev.thhs.contentiospring.models.webrequests.InitProjectResponse
import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.services.apis.TextToSpeechService
import dev.thhs.contentiospring.services.apis.NlpApiService
import dev.thhs.contentiospring.services.apis.RedditApiService
import dev.thhs.contentiospring.services.apis.SlidesApiService
import dev.thhs.contentiospring.utils.logger
import net.dean.jraw.http.NetworkException
import net.dean.jraw.models.CommentSort
import net.dean.jraw.models.PublicContribution
import net.dean.jraw.references.CommentsRequest
import net.dean.jraw.references.SubmissionReference
import net.dean.jraw.tree.CommentNode
import org.springframework.stereotype.Service
import java.io.File

@Service
class AskredditService(val redditApi: RedditApiService,
                       val nlpApi: NlpApiService,
                       val slidesApi: SlidesApiService,
                       val ttsService: TextToSpeechService,
                       val projectRepo: AskredditProjectRepository,
                       val submissionRepo: SubmissionRepository,
                       val statementRepo: StatementRepository,
                       val sentenceRepo: SentenceRepository) {


    private val log by logger()
    private val projectsDurationMap = mutableMapOf<Long, Float>().withDefault { 0f }


    fun createProject(request: InitProjectRequest): InitProjectResponse {
        val url = request.url
        val postId = redditApi.submissionUrlToId(url)
        if (projectRepo.findByPostId(postId).isPresent) return InitProjectResponse(false)

        val projectDir = createTempDir("askreddit")

        val project = AskredditProject(url, postId, projectDir.absolutePath, request.duration)
        projectRepo.save(project)
        projectsDurationMap[project.id] = 0f

        log.info("Creating askreddit project ${project.url}")
        prepareContent(project, request.duration)
        log.info("Project creation completed!")

        projectsDurationMap.remove(project.id)
        return InitProjectResponse(true)
    }

    fun prepareContent(project: AskredditProject, minimalVideoDuration: Int) {

        val coreSubmissionRef = redditApi.getSubmissionByUrl(project.url)
        val commentsRoot = try {
            coreSubmissionRef.comments(CommentsRequest(sort = CommentSort.TOP, depth = 1))
        } catch (err: NetworkException) {
            throw InvalidSubmissionUrl(err.message)
        }

        preparePostSubmission(project, coreSubmissionRef)

        for (comment in commentsRoot.walkTree()) {
            val audioDuration = projectsDurationMap.getValue(project.id)
            val progress = audioDuration / minimalVideoDuration

            log.info("${audioDuration.toInt()} sec | ${(100 * progress).toInt()}%")

            if (audioDuration > minimalVideoDuration) break

            prepareCommentSubmission(project, comment)

        }
        log.info("Prepare content finish!")
//        prepareSlides(project)
    }

//    fun prepareSlides(project: AskredditProject) {
//        log.info("Creating slides...")
//        val sentences = sentenceRepo.findSentencesByStatementSubmissionProjectId(project.id)
//        sentences.forEachIndexed { index, sentence ->
//            val slide = slidesApi.createSlide(sentence.statement.submission.id, sentence.index, project.projectPath)
//            sentence.slidePath = slide.absolutePath
//            sentenceRepo.save(sentence)
//            val progress = (index + 1f) / sentences.size
//            log.info("${(100 * progress).toInt()}%")
////            messenger.sendProjectStatusUpdate(ProjectProcess(project.id, ProjectProcessType.SlideCreation, progress))
//        }
//        log.info("Slides created!")
//    }


    fun preparePostSubmission(project: AskredditProject, postRef: SubmissionReference) {
        val details = postRef.inspect()

        val post = Submission(details.id, details.author, details.score, details.created, project, SubmissionType.POST)
        val statement = Statement(submission = post, text = details.title)
        post.statement = statement

//        log.info("Post statement ${post.id} saving...")
        statementRepo.save(statement)
        submissionRepo.save(post)
//        log.info("Saving ${post.id} done")

        prepareSentences(post, details.title)
    }

    fun prepareCommentSubmission(project: AskredditProject, commentRef: CommentNode<PublicContribution<*>>) {
        val subject = commentRef.subject
        val commentText: String? = subject.body

        if (subject.isStickied || commentText == null) return

        val comment = Submission(subject.id, subject.author, subject.score, subject.created, project, SubmissionType.COMMENT)
        val statement = Statement(submission = comment, text = commentText)
        comment.statement = statement

//        log.info("Comment statement ${comment.id} saving...")
        statementRepo.save(statement)
        submissionRepo.save(comment)
//        log.info("Saving ${comment.id} done")

        prepareSentences(comment, commentText)
    }

    fun prepareSentences(submission: Submission, text: String) {
        val statement = submission.statement ?: throw AssertionError("statement save error")

        val audioDir = File(submission.project.projectPath, "audio")
        val submissionAudioDir = File(audioDir, submission.id)
        submissionAudioDir.mkdirs()

        val projectId = submission.project.id

        val sentences = nlpApi.textToSentences(text).sentences
        sentences.forEachIndexed { index, responseSentence ->
            val cleanText = ttsService.cleanMarkdownText(responseSentence.text)
            val audio = ttsService.textToAudioFile(cleanText, "${index}_${submission.id}", submissionAudioDir)
            val duration = ttsService.getAudioFileDuration(audio)

            projectsDurationMap[projectId] = projectsDurationMap.getValue(projectId) + duration

            val sentence = Sentence(statement, index, responseSentence.text, responseSentence.paragraph, duration, audio.absolutePath)
            sentenceRepo.save(sentence)
        }
    }

}