package dev.thhs.contentiospring.services.generators

import dev.thhs.contentiospring.models.Sentence
import dev.thhs.contentiospring.models.askreddit.AskredditProject
import dev.thhs.contentiospring.models.reddit.Submission
import dev.thhs.contentiospring.models.reddit.SubmissionType
import dev.thhs.contentiospring.repositories.SentenceRepository
import dev.thhs.contentiospring.repositories.SubmissionRepository
import dev.thhs.contentiospring.utils.createCommandProcess
import dev.thhs.contentiospring.utils.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import org.springframework.stereotype.Service
import java.io.File
import java.lang.Error
import javax.annotation.PreDestroy
import kotlin.coroutines.CoroutineContext


sealed class Clip(val file: File) {
    class Video(file: File) : Clip(file)
    class Audio(file: File) : Clip(file)
}

data class SubmissionVideoRequest(val submission: Submission, val callback: (Clip.Video) -> Unit)

enum class ClipExtension(val value: String) {
    Video("mp4"), Audio("wav")
}

data class ImgDur(val img: File, val duration: Float)

/**
 *  @property createImagesToVideoInputFile creates input file for ffmpeg command. (Last two lines are repeated due to ffmpeg bug?)
 *  @property createVideoFromImages creates mp4 video from images. No sound
 *  @property createSlidesVideoFromSubmission creates mp4 video from submission's sentences slides
 *
 *  @property createConcatInputFile creates input file for ffmpeg concat command
 *  @property concatClips creates one clip from list of clips. Can concat audio and video
 *  @property mergeVideoAndAudio merges audio and video to mp4 video file
 */
@Service
class VideoService(
        private val sentenceRepository: SentenceRepository,
        private val submissionRepository: SubmissionRepository
) : CoroutineScope {

    private val job = Job()
    private val log by logger()


    @PreDestroy
    fun dispose() {
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    suspend fun generateVideo(project: AskredditProject) = coroutineScope {
        val orderedSubmissions = submissionsOrderInVideo(project)
        val submissionVideos = createSubmissionsVideos(orderedSubmissions)
        val filtered: List<Clip.Video> = submissionVideos.values.toList().filterNotNull()

        val interlude = Clip.Video(File(VideoService::class.java.getResource("/media/no_signal.mp4").toURI()))
        val clips: List<Clip.Video> = filtered.flatMap { listOf(it, interlude) }.dropLast(1)

        val montageDir = File(project.projectPath, "FinalVideo")
        montageDir.mkdirs()
        val video = concatClipsWithConcatFilter(clips, montageDir, "video_no_music")

        val music = Clip.Audio(File(VideoService::class.java.getResource("/media/music.wav").toURI()))
        log.info("Adding background music...")
        val finalVideo = addBackgroundMusic(video, music, montageDir, "final_video")
        log.info("Video ready at ${finalVideo.file.absolutePath}")
    }


    private fun addBackgroundMusic(video: Clip.Video, music: Clip.Audio, workingDir: File, fileName: String = "video"): Clip.Video {
        val tempAudioClips = List(15) { music }
        val backgroundMusic = concatClips(tempAudioClips, workingDir, ClipExtension.Audio, "background_music")

        val videoPath = video.file.absolutePath.replace("\\", "/")
        val musicPath = backgroundMusic.file.absolutePath.replace("\\", "/")
        val audioFromVideo = "audio_from_video.aac"
        val audioWithMusic = "audio_with_music.mp3"

        val exportAudioCommand = "ffmpeg -i $videoPath -vn -acodec copy $audioFromVideo"
        val margeAudioCommand = "ffmpeg -i $audioFromVideo -i $musicPath -filter_complex amix=inputs=2:duration=shortest $audioWithMusic"
        val replaceAudioCommand = "ffmpeg -i $videoPath -i $audioWithMusic -c:v copy -map 0:v:0 -map 1:a:0 $fileName.mp4"

        runFfmpegCommand(exportAudioCommand, workingDir)
        runFfmpegCommand(margeAudioCommand, workingDir)
        runFfmpegCommand(replaceAudioCommand, workingDir)

        backgroundMusic.file.delete()
        return Clip.Video(File(workingDir, "$fileName.mp4"))
    }


    suspend fun createSubmissionsVideos(orderedSubmissions: List<Submission>): Map<String, Clip.Video?> = coroutineScope {
        val submissionVideos = mutableMapOf<String, Clip.Video?>(*orderedSubmissions.map { Pair(it.id, null) }.toTypedArray())
        val requests = Channel<SubmissionVideoRequest>()
        repeat(3) {
            videoWorker(requests)
        }
        orderedSubmissions.forEach {
            val request = SubmissionVideoRequest(it) { video: Clip.Video ->
                submissionVideos[it.id] = video
            }
            requests.send(request)
        }
        requests.close()
        submissionVideos
    }

    private fun CoroutineScope.videoWorker(requests: ReceiveChannel<SubmissionVideoRequest>) = launch {
        for (request in requests) {
            val video = createVideoFromSubmission(request.submission)
            request.callback(video)
        }
    }

    suspend fun createVideoFromSubmission(submission: Submission): Clip.Video {
        val sentences = sentenceRepository.findSentencesByStatementSubmissionId(submission.id)
        val videoClip = createSlidesVideoFromSubmission(submission, sentences)
        val audioClip = createAudioClipFromSubmission(submission, sentences)
        val montageDir = submission.createCategorySubmissionDir("Video")
        return mergeVideoAndAudio("${submission.id}_video", videoClip, audioClip, montageDir)
    }


    fun submissionsOrderInVideo(project: AskredditProject): List<Submission> {
        val submissions = submissionRepository.findSubmissionsByProjectId(project.id)
        val postAndComments = submissions.partition { it.type == SubmissionType.POST }
        val shuffledComments = postAndComments.second.shuffled().toMutableList()
        shuffledComments.addAll(0, postAndComments.first)
        return shuffledComments
    }

    private fun mergeVideoAndAudio(
            outputName: String,
            video: Clip.Video,
            audio: Clip.Audio,
            workingDir: File
    ): Clip.Video {
        val command = "ffmpeg -i ${video.file.name} -i ${audio.file.name} -c:v copy -c:a aac -strict experimental $outputName.mp4"
        runFfmpegCommand(command, workingDir)
        val file = File(workingDir, "$outputName.${ClipExtension.Video.value}")
        return Clip.Video(file)
    }

    private fun createAudioClipFromSubmission(
            submission: Submission,
            sentences: List<Sentence> = sentenceRepository.findSentencesByStatementSubmissionId(submission.id)
    ): Clip.Audio {
        val audioClips = sentences.map { Clip.Audio(File(it.audioPath)) }
        val montageDir = submission.createCategorySubmissionDir("Video")
        return concatClips(audioClips, montageDir, ClipExtension.Audio, "${submission.id}_audio")
    }

    private fun concatClipsWithConcatFilter(
            clips: List<Clip.Video>,
            workingDir: File,
            fileName: String = "video"
    ): Clip.Video {
        val inputs: String = clips.map { it.file.absolutePath }.map { it.replace("\\", "/") }.joinToString(" ") { "-i $it" }
        val channels: String = clips.mapIndexed { index, _ -> "[$index:v:0][$index]" }.joinToString("")
        val command = "ffmpeg $inputs -filter_complex \"${channels}concat=n=${clips.size}:v=1:a=1[outv][outa]\" -map \"[outv]\" -map \"[outa]\" $fileName.mp4"
        runFfmpegCommand(command, workingDir)
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
        val concatCommand = "ffmpeg -f concat -safe 0  -i ${inputFile.name} -c copy $outputName"
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


    private fun createSlidesVideoFromSubmission(
            submission: Submission,
            sentences: List<Sentence> = sentenceRepository.findSentencesByStatementSubmissionId(submission.id)
    ): Clip.Video {
        val slides: List<ImgDur> = sentences.map { ImgDur(File(it.slidePath), it.duration) }
        val montageDir = submission.createCategorySubmissionDir("Video")

        return createVideoFromImages(slides, montageDir, "${submission.id}_slides")
    }

    private fun createVideoFromImages(
            images: List<ImgDur>,
            workingDir: File = createTempDir("montage"),
            videoName: String
    ): Clip.Video {
        val inputFile = createImagesToVideoInputFile(images, workingDir, videoName)
        val clipName = "$videoName.mp4"
        val concatCommand = "ffmpeg -f concat -safe 0 -i ${inputFile.name} -vf fps=10 -pix_fmt yuv420p $clipName"
        runFfmpegCommand(concatCommand, workingDir)
        val videoFile = File(workingDir, clipName)
        return Clip.Video(videoFile)
    }

    private fun createImagesToVideoInputFile(
            images: List<ImgDur>,
            workingDir: File,
            videoName: String
    ): File {
        val inputFile = File(workingDir, "${videoName}_input.txt")
        images.forEach {
            inputFile.appendText("file ${it.img.absolutePath.replace("\\", "/")}\n")
            inputFile.appendText("duration ${it.duration}\n")
        }
        inputFile.readLines().takeLast(2).forEach {
            inputFile.appendText("$it\n")
        }
        return inputFile
    }

    fun runFfmpegCommand(command: String, workingDir: File) {
        val concatProcess = command.createCommandProcess(workingDir)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
        if (concatProcess.waitFor() != 0) {
            throw Error(String(concatProcess.errorStream.readAllBytes()))
        }
    }
}