package dev.thhs.contentiospring.services.reddit

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.Statement
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.reddit.SubmissionType
import dev.thhs.contentiospring.models.webrequests.InitProjectRequest
import dev.thhs.contentiospring.models.webrequests.InitProjectResponse
import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.StatementRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.services.apis.NlpApiService
import dev.thhs.contentiospring.services.apis.RedditApiService
import dev.thhs.contentiospring.services.generators.MediaGenerator
import dev.thhs.contentiospring.services.generators.VideoService
import dev.thhs.contentiospring.utils.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import net.dean.jraw.models.CommentSort
import net.dean.jraw.models.PublicContribution
import net.dean.jraw.references.CommentsRequest
import net.dean.jraw.references.SubmissionReference
import net.dean.jraw.tree.CommentNode
import kotlin.coroutines.*
import org.springframework.stereotype.Service
import javax.annotation.PreDestroy


sealed class DurationMsg {
    data class Get(val response: CompletableDeferred<Float>) : DurationMsg()
    data class Add(val duration: Float) : DurationMsg()
}

sealed class RawSubmission {
    data class Post(val postRef: SubmissionReference) : RawSubmission()
    data class Comment(val commentRef: CommentNode<PublicContribution<*>>) : RawSubmission()
}

sealed class ProcessedSubmission {
    data class Valid(val submission: Submission, val statement: Statement) : ProcessedSubmission()
    object Invalid : ProcessedSubmission()
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
                              val videoService: VideoService
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
            generateContent(project)
            mediaGenerator.generateMedia(project)
//            log.info("Creating video...")
//            videoService.generateVideo(project)
            log.info("Generating content end")
        }

        return InitProjectResponse.Success(project.id)
    }

    suspend fun generateContent(project: AskredditProject) = coroutineScope {

        val coreSubmissionRef = redditApi.getSubmissionByUrl(project.url)
        val commentsRoot = coreSubmissionRef.comments(CommentsRequest(sort = CommentSort.TOP, depth = 1))

        val rawSubmissions = Channel<RawSubmission>(capacity = 1)
        val projectDuration = sentenceRepository.findSentencesByStatementSubmissionProjectId(project.id).map { it.predictedDuration }.sum()
        val durationChannel = durationActor(projectDuration)

        repeat(1) {
            projectWorker(project, rawSubmissions, durationChannel)
        }
        rawSubmissions.send(RawSubmission.Post(coreSubmissionRef))

        var durationReached = false
        for (comment in commentsRoot.walkTree()) {
            if (submissionRepository.findById(comment.subject.id).isPresent) continue

            val durationRes = CompletableDeferred<Float>()
            durationChannel.send(DurationMsg.Get(durationRes))
            val duration = durationRes.await()
            log.info("Current duration is $duration")
            if (project.minDuration <= duration) {
                rawSubmissions.close()
                durationChannel.close()
                durationReached = true
                break
            }
            val rawComment = RawSubmission.Comment(comment)
            rawSubmissions.send(rawComment)
            log.info("Comment send to channel")
        }
        if (!durationReached) {
            project.allCommentsUsed = true
            projectRepository.save(project)
        }
        log.info("Canceling generator")
    }


    fun CoroutineScope.durationActor(initDuration: Float = 0f) = actor<DurationMsg> {
        var duration = initDuration
        for (msg in channel) {
            when (msg) {
                is DurationMsg.Add -> duration += msg.duration
                is DurationMsg.Get -> msg.response.complete(duration)
            }
        }
        log.info("Duration actor closed")
    }

    fun CoroutineScope.projectWorker(
            project: AskredditProject,
            rawSubmissions: ReceiveChannel<RawSubmission>,
            durationChannel: SendChannel<DurationMsg>
    ) = launch {
        for (rawSubmission in rawSubmissions) {
            val processed = when (rawSubmission) {
                is RawSubmission.Post -> createSubmission(project, rawSubmission.postRef)
                is RawSubmission.Comment -> createSubmission(project, rawSubmission.commentRef)
            } as? ProcessedSubmission.Valid ?: continue

            statementRepository.save(processed.statement)
            submissionRepository.save(processed.submission)

            val duration = prepareSentences(processed.statement)
            if (!durationChannel.isClosedForSend) durationChannel.send(DurationMsg.Add(duration))
        }
        log.info("Worker closed")
    }

    suspend fun createSubmission(
            project: AskredditProject,
            postRef: SubmissionReference
    ): ProcessedSubmission {
        val details = postRef.inspect()
        val post = Submission(details.id, details.author, details.score, details.created, project, SubmissionType.POST)
        val statement = Statement(post, details.title)
        post.statement = statement
        return ProcessedSubmission.Valid(post, statement)
    }

    suspend fun createSubmission(
            project: AskredditProject,
            commentRef: CommentNode<PublicContribution<*>>
    ): ProcessedSubmission {
        val subject = commentRef.subject
        val commentText: String? = subject.body
        if (subject.isStickied || commentText == null) return ProcessedSubmission.Invalid

        val comment = Submission(subject.id, subject.author, subject.score, subject.created, project, SubmissionType.COMMENT)
        val statement = Statement(comment, commentText)
        comment.statement = statement
        return ProcessedSubmission.Valid(comment, statement)
    }

    suspend fun prepareSentences(statement: Statement): Float {
        val sentencesData = nlpApi.textToSentences(statement.originalText)
        val estimatedStatementDuration = estimateSpeechDuration(sentencesData.wordCount)
        val sentences = sentencesData.sentences.mapIndexed { index, it ->
            val predictedSentenceDuration = estimateSpeechDuration(it.wordCount)
            Sentence(statement, index, it.text, it.paragraph, predictedSentenceDuration)
        }
        sentenceRepository.saveAll(sentences)
        return estimatedStatementDuration
    }

    fun estimateSpeechDuration(wordCount: Int): Float {
        val talkSpeed = 2f
        return wordCount.toFloat() / talkSpeed
    }
}