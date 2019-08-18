package dev.thhs.contentiospring.services.apis

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import dev.thhs.contentiospring.utils.logger
import dev.thhs.contentiospring.utils.runCommandWithOutput
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.io.File
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine

@Service
object TextToSpeechService {
    private val log by logger()
    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    fun textToAudioFile(text: String, fileName: String, workingDir: File, deleteTextFile: Boolean = true): File {
        val tempTextFile = createTempFile(fileName, ".txt", workingDir)
        val audioName = "$fileName.wav"
        tempTextFile.writeText(text)

        val output: ByteArray = "balcon -n Daniel -fr 44 -o -f ${tempTextFile.name}".runCommandWithOutput(workingDir)
        val audioFile = File(workingDir, audioName)
        audioFile.writeBytes(output)

        if (deleteTextFile) tempTextFile.delete()
        return audioFile
    }

    private fun clearMarkdownText(text: String): String {
        val document = parser.parse(text)
        val html = renderer.render(document)
        return Jsoup.parse(html).text()
    }

    private fun clearQuotationMarks(text: String): String = text.replace("\"", "")

    fun clearTextForReading(text: String): String {
        val cleared = clearMarkdownText(text)
        return clearQuotationMarks(cleared)
    }

    fun getAudioFileDuration(audioFile: File): Float {
        var stream = AudioSystem.getAudioInputStream(audioFile)
        var format = stream.format
        if (format.encoding !== AudioFormat.Encoding.PCM_SIGNED) {
            format = AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, format.sampleRate, format.sampleSizeInBits * 2,
                    format.channels, format.frameSize * 2, format.frameRate, true
            )
            stream = AudioSystem.getAudioInputStream(format, stream)
        }
        val info = DataLine.Info(Clip::class.java, stream.format, stream.frameLength.toInt() * format.frameSize)

        val clip = AudioSystem.getLine(info) as Clip
        clip.close()
        return clip.bufferSize / (clip.format.frameSize * clip.format.frameRate)


    }


}