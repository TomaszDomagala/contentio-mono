package dev.thhs.contentiospring.services.reddit

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.Statement
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.reddit.SubmissionType
import dev.thhs.contentiospring.models.webrequests.InitProjectRequest
import dev.thhs.contentiospring.models.webrequests.InitProjectResponse
import dev.thhs.contentiospring.models.webrequests.InitProjectVideoRequest
import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.services.apis.NlpApiService
import dev.thhs.contentiospring.services.apis.RedditApiService
import dev.thhs.contentiospring.services.generators.MediaGenerator
import dev.thhs.contentiospring.services.generators.VideoService
import dev.thhs.contentiospring.services.generators.VideoService2
import dev.thhs.contentiospring.utils.logger
import kotlinx.coroutines.*
import net.dean.jraw.models.CommentSort
import net.dean.jraw.models.PublicContribution
import net.dean.jraw.references.CommentsRequest
import net.dean.jraw.references.SubmissionReference
import net.dean.jraw.tree.CommentNode
import kotlin.coroutines.*
import org.springframework.stereotype.Service
import javax.annotation.PreDestroy


sealed class RawSubmission(val order: Int) {
    class Post(val postRef: SubmissionReference, order: Int) : RawSubmission(order)
    class Comment(val commentRef: CommentNode<PublicContribution<*>>, order: Int) : RawSubmission(order)
}

sealed class ProcessedSubmission {
    data class Valid(val submission: Submission, val statement: Statement) : ProcessedSubmission()
    object Invalid : ProcessedSubmission()
}

sealed class PreparedSubmission {
    class Success(val duration: Float) : PreparedSubmission()
    object Failure : PreparedSubmission()
}

/**
 *  @property estimateSpeechDuration https://wordcounter.net/blog/2016/06/02/101702_how-fast-average-person-speaks.html
 *  talk speed is ~120 words/min = 2 words/sec
 */
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@Service
class AskredditContentService(val redditApi: RedditApiService,
                              val projectRepository: AskredditProjectRepository,
                              val submissionRepository: SubmissionRepository,
                              val statementRepository: StatementRepository,
                              val sentenceRepository: SentenceRepository,
                              val nlpApi: NlpApiService,
                              val mediaGenerator: MediaGenerator,
                              val videoService: VideoService,
                              val videoService2: VideoService2
) : CoroutineScope {
    private val job = Job()
    private val log by logger()

    @PreDestroy
    fun dispose() {
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default


    fun initProject(request: InitProjectRequest, startContentGeneration: Boolean = true): InitProjectResponse {
        val url = request.url
        val postId = redditApi.submissionUrlToId(url)
        if (projectRepository.findByPostId(postId).isPresent)
            return InitProjectResponse.Failure("Project with this submission id already exist")

        log.info("Starting project initiation")
        val projectDir = createTempDir("askredditproject_")

        val project = AskredditProject(url, postId, projectDir.absolutePath, request.duration)
        projectRepository.save(project)
        log.info("Project entity saved")

        if (startContentGeneration) launch {
            log.info("Generate content async process started")
            generateContent(project, true)
            mediaGenerator.generateMedia(project)
            log.info("Creating video...")
            videoService2.initSentenceVideos(sentenceRepository.findSentencesByStatementSubmissionProjectId(project.id))
//            videoService.generateVideo(project)
            log.info("Generating content end")
        }

        return InitProjectResponse.Success(project.id)
    }

    suspend fun generateContent(project: AskredditProject, generatePostSubmission: Boolean = false) = coroutineScope {
        val coreSubmissionRef = redditApi.getSubmissionByUrl(project.url)
        val commentsRoot = coreSubmissionRef.comments(CommentsRequest(sort = CommentSort.TOP, depth = 1))

        var orderInProject = submissionRepository.findSubmissionsByProjectId(project.id).size
        var projectDuration = sentenceRepository.findSentencesByStatementSubmissionProjectId(project.id)
                .filter { !it.statement.submission.ignore }
                .map { it.audioDuration }
                .sum()


        val handleSubmissionPreparation: suspend (RawSubmission) -> Unit = { rawSubmission: RawSubmission ->
            when (val preparedSubmission = prepareSubmission(rawSubmission, project)) {
                is PreparedSubmission.Failure -> log.info("Submission not created")
                is PreparedSubmission.Success -> {
                    projectDuration += preparedSubmission.duration
                    orderInProject++
                    log.info("Submission created")
                }
            }
        }

        if (generatePostSubmission) handleSubmissionPreparation(RawSubmission.Post(coreSubmissionRef, orderInProject))

        for (comment in commentsRoot.walkTree()) {
            if (submissionRepository.findById(comment.subject.id).isPresent) continue
            if (project.minDuration <= projectDuration) break

            handleSubmissionPreparation(RawSubmission.Comment(comment, orderInProject))
        }
        if (project.minDuration > projectDuration) {
            project.allCommentsUsed = true
            projectRepository.save(project)
        }
        log.info("Generation End")
    }

    suspend fun prepareSubmission(rawSubmission: RawSubmission, project: AskredditProject): PreparedSubmission {
        val processed = when (rawSubmission) {
            is RawSubmission.Post -> createSubmission(project, rawSubmission.postRef, rawSubmission.order)
            is RawSubmission.Comment -> createSubmission(project, rawSubmission.commentRef, rawSubmission.order)
        } as? ProcessedSubmission.Valid ?: return PreparedSubmission.Failure
        statementRepository.save(processed.statement)
        submissionRepository.save(processed.submission)

        val duration = prepareSentences(processed.statement)
        return PreparedSubmission.Success(duration)
    }

    suspend fun createSubmission(
            project: AskredditProject,
            postRef: SubmissionReference,
            order: Int
    ): ProcessedSubmission {
        val details = postRef.inspect()
        val post = Submission(details.id, details.author, details.score, details.created, project, order, SubmissionType.POST)
        val statement = Statement(post, details.title)
        post.statement = statement
        return ProcessedSubmission.Valid(post, statement)
    }

    suspend fun createSubmission(
            project: AskredditProject,
            commentRef: CommentNode<PublicContribution<*>>,
            order: Int
    ): ProcessedSubmission {
        val subject = commentRef.subject
        val commentText: String? = subject.body
        if (subject.isStickied || commentText == null) return ProcessedSubmission.Invalid

        val comment = Submission(subject.id, subject.author, subject.score, subject.created, project, order, SubmissionType.COMMENT)
        val statement = Statement(comment, commentText)
        comment.statement = statement
        if (commentText in listOf("[deleted]", "[removed")) {
            comment.ignore = true
        }
        return ProcessedSubmission.Valid(comment, statement)
    }


    fun createSentences(statement: Statement) = launch {
        log.info("Creting sentences...")
        prepareSentences(statement)
        val sentences = sentenceRepository.findSentencesByStatementId(statement.id)
        mediaGenerator.generateMedia(sentences)
        videoService2.initSentenceVideos(sentences)
        log.info("Creating sentences done!")
    }

    suspend fun prepareSentences(statement: Statement): Float {
        val sentencesData = nlpApi.textToSentences(statement.editedText)
        val estimatedStatementDuration = estimateSpeechDuration(sentencesData.wordCount)
        val sentences = sentencesData.sentences.mapIndexed { index, it ->
            val predictedSentenceDuration = estimateSpeechDuration(it.wordCount)
            Sentence(statement, index, it.text, it.paragraph, predictedSentenceDuration)
        }
        sentenceRepository.saveAll(sentences)
        return estimatedStatementDuration
    }

    fun estimateSpeechDuration(wordCount: Int): Float {
        val talkSpeedInWordsPerSec = 4f
        return wordCount.toFloat() / talkSpeedInWordsPerSec
    }
}

