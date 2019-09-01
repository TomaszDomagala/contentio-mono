package dev.thhs.contentiospring.services.generators

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.exceptions.FfmpegFailure
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.reddit.SubmissionType
import dev.thhs.contentiospring.models.webrequests.InitProjectVideoRequest
import dev.thhs.contentiospring.models.webrequests.InitProjectVideoResponse
import dev.thhs.contentiospring.repositories.AskredditProjectRepository
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.utils.createCommandProcess
import dev.thhs.contentiospring.utils.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import org.springframework.stereotype.Service
import java.io.File
import javax.annotation.PreDestroy
import kotlin.coroutines.CoroutineContext


data class VideoRequest(val data: SentenceVideoData.Valid, val onComplete: (VideoResult) -> Unit)

sealed class VideoResult {
    data class Success(val video: File) : VideoResult()
    object Failure : VideoResult()
}

sealed class SentenceVideoData {
    data class Valid(
            val sentenceId: Long,
            val slide: File,
            val audio: File,
            val outputName: String,
            val workingDir: File)
        : SentenceVideoData()

    data class Invalid(
            val sentenceId: Long,
            val slideExists: Boolean,
            val audioExists: Boolean
    ) : SentenceVideoData()
}


@Service
class VideoService2(
        private val sentenceRepository: SentenceRepository,
        private val submissionRepository: SubmissionRepository,
        private val projectRepository: AskredditProjectRepository
) : CoroutineScope {

    private val job = Job()
    private val log by logger()


    @PreDestroy
    fun dispose() {
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default


    fun initProjectVideo(request: InitProjectVideoRequest): InitProjectVideoResponse {
        val project = try {
            projectRepository.findById(request.id).orElseThrow()
        } catch (err: NoSuchElementException) {
            return InitProjectVideoResponse.Failure.ProjectNotFound
        }
        launch {
            createProjectVideo(project)
        }
        return InitProjectVideoResponse.Success
    }

    private suspend fun createProjectVideo(project: AskredditProject) = coroutineScope {
        log.info("Checking sentence videos...")
        val videos = sentenceRepository.findSentencesByStatementSubmissionProjectId(project.id).map { File(it.videoPath) }
        if (videos.any { !it.exists() }) {
            log.error("Not all sentences have video generated")
            return@coroutineScope
        }
        log.info("Done!")
        val workingDir = File(project.projectPath, "Product")
        workingDir.mkdirs()

//        val orderedSubmissions = submissionRepository.findSubmissionsByProjectId(project.id).filter { !it.ignore }.sortedBy { it.orderInProject }
        val orderedSubmissions = submissionsOrderInVideo(project).filter { !it.ignore }
        val interlude = File(VideoService2::class.java.getResource("/media/no_signal.mp4").toURI())
        val submissionVideos: List<List<File>> = orderedSubmissions.map { submission ->
            sentenceRepository.findSentencesByStatementSubmissionId(submission.id).map { File(it.videoPath) }
        }
        val clips: List<Clip.Video> = submissionVideos.flatMap { listOf(it, listOf(interlude)) }.flatten().map { Clip.Video(it) }.dropLast(1)
        log.info("Creating raw video...")
        val rawVideo = concatClipsWithConcatFilter(clips, workingDir, "video_no_background,.mp4")
        log.info("Raw video created!")
        val backgroundMusic = Clip.Audio(File(VideoService2::class.java.getResource("/media/music.wav").toURI()))
        log.info("Adding background music...")
        val video = addBackgroundMusic(rawVideo, backgroundMusic, workingDir, "final_video")
        project.videoPath = video.file.absolutePath
        projectRepository.save(project)
        log.info("Video ready! ${video.file.absolutePath}")
    }

    fun submissionsOrderInVideo(project: AskredditProject): List<Submission> {
        val submissions = submissionRepository.findSubmissionsByProjectId(project.id)
        val postAndComments = submissions.partition { it.type == SubmissionType.POST }
        val shuffledComments = postAndComments.second.shuffled().toMutableList()
        shuffledComments.addAll(0, postAndComments.first)
        return shuffledComments
    }

    private fun addBackgroundMusic(video: Clip.Video, music: Clip.Audio, workingDir: File, fileName: String = "video"): Clip.Video {
        val tempAudioClips = List(15) { music }
        val backgroundMusic = concatClips(tempAudioClips, workingDir, ClipExtension.Audio, "background_music")

        val videoPath = video.file.absolutePath.replace("\\", "/")
        val musicPath = backgroundMusic.file.absolutePath.replace("\\", "/")
        val audioFromVideo = "audio_from_video.aac"
        val audioWithMusic = "audio_with_music.mp3"

        val exportAudioCommand = "ffmpeg -y -i $videoPath -vn -acodec copy $audioFromVideo"
        val margeAudioCommand = "ffmpeg -y -i $audioFromVideo -i $musicPath -filter_complex amix=inputs=2:duration=shortest $audioWithMusic"
        val replaceAudioCommand = "ffmpeg -y -i $videoPath -i $audioWithMusic -c:v copy -map 0:v:0 -map 1:a:0 $fileName.mp4"

        runFfmpegCommand(exportAudioCommand, workingDir)
        runFfmpegCommand(margeAudioCommand, workingDir)
        runFfmpegCommand(replaceAudioCommand, workingDir)

        backgroundMusic.file.delete()
        return Clip.Video(File(workingDir, "$fileName.mp4"))
    }

    private fun <T : Clip> concatClips(
            clips: List<T>,
            workingDir: File,
            extension: ClipExtension,
            filePrefix: String = "concatClip"
    ): T {
        val inputFile = createConcatInputFile(clips, workingDir, filePrefix)
        val outputName = "${inputFile.nameWithoutExtension}.${extension.value}"
        val concatCommand = "ffmpeg -y -f concat -safe 0  -i ${inputFile.name} -c copy $outputName"
        runFfmpegCommand(concatCommand, workingDir)
        val file = File(workingDir, outputName)
        @Suppress("UNCHECKED_CAST")
        return (if (extension == ClipExtension.Video) Clip.Video(file) else Clip.Audio(file)) as T
    }

    private fun createConcatInputFile(clips: List<Clip>, workingDir: File, filePrefix: String = "concatClip"): File {
        val inputFile = createTempFile(filePrefix, ".txt", workingDir)
        clips.forEach {
            inputFile.appendText("file ${it.file.absolutePath.replace("\\", "/")}\n")
        }
        return inputFile
    }

    private fun concatClipsWithConcatFilter(
            clips: List<Clip.Video>,
            workingDir: File,
            fileName: String = "video"
    ): Clip.Video {
        val inputs: String = clips.map { it.file.absolutePath }.map { it.replace("\\", "/") }.joinToString(" ") { "-i $it" }
        val channels: String = clips.mapIndexed { index, _ -> "[$index:v:0][$index]" }.joinToString("")
        val command = "ffmpeg -y $inputs -filter_complex \"${channels}concat=n=${clips.size}:v=1:a=1[outv][outa]\" -map \"[outv]\" -map \"[outa]\" $fileName.mp4"
        runFfmpegCommand(command, workingDir)
        return Clip.Video(File(workingDir, "$fileName.mp4"))
    }


    suspend fun initSentenceVideos(sentences: List<Sentence>) = coroutineScope {
        val videoRequests = sentences.map { it.createVideoRequest() }
        val validRequests = videoRequests.filterIsInstance<SentenceVideoData.Valid>()
        if (validRequests.size != videoRequests.size) {
            log.error("Missing assets in sentences")
            return@coroutineScope
        }
        createSentenceVideos(validRequests)
    }

    private suspend fun createSentenceVideos(videoRequests: List<SentenceVideoData.Valid>): List<VideoResult> = coroutineScope {
        val videoResults = mutableListOf<VideoResult>()
        val requestChannel = Channel<VideoRequest>()

        repeat(3) {
            sentenceVideoWorker(requestChannel)
        }
        videoRequests.forEach {
            val request = VideoRequest(it) { res: VideoResult -> videoResults.add(res) }
            requestChannel.send(request)
        }
        requestChannel.close()
        return@coroutineScope videoResults
    }

    private fun CoroutineScope.sentenceVideoWorker(requests: ReceiveChannel<VideoRequest>) = launch {
        for (req in requests) {
            val video = req.data.createVideo()
            req.onComplete(video)
        }
    }

    private fun SentenceVideoData.Valid.createVideo(): VideoResult {
        val command = "ffmpeg -y -i ${slide.unixPath()} -i ${audio.unixPath()} -c:v libx264 -c:a aac -pix_fmt yuv420p $outputName"
        try {
            runFfmpegCommand(command, workingDir)
        } catch (err: FfmpegFailure) {
            log.error(err.message)
            return VideoResult.Failure
        }
        val videoFile = File(workingDir, outputName)
        val sentence = sentenceRepository.findById(sentenceId).get()
        sentence.videoPath = videoFile.absolutePath
        sentenceRepository.save(sentence)
        return VideoResult.Success(videoFile)
    }

    private fun Sentence.createVideoRequest(): SentenceVideoData {
        val slide = File(slidePath)
        val audio = File(audioPath)
        if (slide.exists() && audio.exists()) {
            val submission = statement.submission
            val outputName = "${submission.id}_$index.mp4"
            val workingDir = submission.createCategorySubmissionDir("Video")
            return SentenceVideoData.Valid(id, slide, audio, outputName, workingDir)
        }
        return SentenceVideoData.Invalid(id, slide.exists(), audio.exists())
    }

    private fun File.unixPath(): String = this.absolutePath.replace("\\", "/")

    private fun runFfmpegCommand(command: String, workingDir: File) {
        val concatProcess = command.createCommandProcess(workingDir)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
        if (concatProcess.waitFor() != 0) {
            throw FfmpegFailure(String(concatProcess.errorStream.readAllBytes()))
        }
    }

}