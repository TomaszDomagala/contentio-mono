package dev.thhs.contentiospring.services.generators

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.services.apis.SlidesApiService
import dev.thhs.contentiospring.services.apis.TextToSpeechService
import dev.thhs.contentiospring.utils.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import org.springframework.stereotype.Service
import java.io.File
import java.lang.Exception
import javax.annotation.PreDestroy
import kotlin.coroutines.CoroutineContext


data class MediaRequest(val sentence: Sentence, val type: MediaType, val overwriteExisting: Boolean = false)


enum class MediaType {
    Audio, Slides
}

enum class NumberOfWorkers(val number: Int) {
    MediaGenerator(6)
}

@Service
class MediaGenerator(
        val ttsService: TextToSpeechService,
        val slidesApi: SlidesApiService,
        val sentenceRepository: SentenceRepository
) : CoroutineScope {
    private val job = Job()
    private val log by logger()


    @PreDestroy
    fun dispose() {
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default


    suspend fun generateMedia(project: AskredditProject) = coroutineScope {
        val workersNum = NumberOfWorkers.MediaGenerator.number
        val sentences = sentenceRepository.findSentencesByStatementSubmissionProjectId(project.id)
        val mediaRequests = Channel<MediaRequest>(2 * workersNum)
        repeat(workersNum) {
            mediaWorker(mediaRequests, it)
        }
        sentences.forEach {
            mediaRequests.send(MediaRequest(it, MediaType.Audio))
            mediaRequests.send(MediaRequest(it, MediaType.Slides))
        }
        mediaRequests.close()
    }


    fun CoroutineScope.mediaWorker(requests: ReceiveChannel<MediaRequest>, workerNum: Int) = launch {
        for (request in requests) when (request.type) {
            MediaType.Audio -> {
                log.info("$workerNum: Audio generation start")
                generateSentenceAudio(request.sentence)
            }
            MediaType.Slides -> {
                log.info("$workerNum: Slides generation start")
                generateSentenceSlides(request.sentence)
            }
        }

    }

    fun generateSentenceSlides(
            sentence: Sentence,
            workingDir: File = createSubmissionDir(sentence.statement.submission, MediaType.Slides)
    ) {
        val submissionId = sentence.statement.submission.id
        val slide = slidesApi.createSlide(submissionId, sentence.index, workingDir)
        sentence.slidePath = slide.absolutePath
        sentenceRepository.save(sentence)
    }

    fun generateSentenceAudio(
            sentence: Sentence,
            workingDir: File = createSubmissionDir(sentence.statement.submission, MediaType.Audio)
    ) {
        val submissionId = sentence.statement.submission.id
        val cleanedText = ttsService.clearTextForReading(sentence.text)
        val audioFile = ttsService.textToAudioFile(cleanedText, "${sentence.index}_$submissionId", workingDir)
        val duration = try {
            ttsService.getAudioFileDuration(audioFile)
        } catch (err: Exception) {
            log.error("Error while getting audio file duration")
            log.error("Audio file's text: $cleanedText")
            log.error(err.localizedMessage)
            0f
        }
        sentence.audioPath = audioFile.absolutePath
        sentence.audioDuration = duration
        sentenceRepository.save(sentence)
    }

    fun createSubmissionDir(submission: Submission, type: MediaType): File {
        val projectDir = File(submission.project.projectPath)
        val mediaTypeDir = File(projectDir, type.name)
        val submissionDir = File(mediaTypeDir, submission.id)
        submissionDir.mkdirs()
        return submissionDir
    }

}