package dev.thhs.contentiospring.services.apis

import dev.thhs.contentiospring.utils.createCommandProcess
import dev.thhs.contentiospring.utils.logger
import dev.thhs.contentiospring.utils.runCommand
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.File
import java.lang.AssertionError
import javax.annotation.PreDestroy
import javax.imageio.ImageIO

@Service
object SlidesApiService {
    private val log by logger()

//    private val slidesLocation = System.getenv("ContentioSlides")
//            ?: throw AssertionError("No ContentioSlides location environment variable set")
//    private val slidesDir = File(slidesLocation)
//    private val slidesProcess: Process = startSlidesProcess()

//    private fun buildSlidesApplication() {
//        val buildCommand = "npm.cmd run build"
//
//        log.info("Building slides application...")
//        val buildProcess = buildCommand.runCommand(slidesDir)
//        if (buildProcess.waitFor() != 0) throw AssertionError("Contentio slides build error")
//        log.info("Build completed!")
//    }

//    private fun startSlidesProcess(): Process {
//        val startCommand = "npm.cmd run dev"
////        buildSlidesApplication()
//
//        log.info("Starting slides process")
//        return startCommand.createCommandProcess(slidesDir)
//                .redirectInput(ProcessBuilder.Redirect.INHERIT)
//                .redirectError(ProcessBuilder.Redirect.INHERIT)
//                .start()
//    }

//    @PreDestroy
//    private fun destroySlidesProcess() {
//        log.info("Destroying nlp process")
//        if (slidesProcess.isAlive) slidesProcess.destroy()
//    }


    fun createSlide(submissionId: String, slideNumber: Int, workingDir: File): File {
        val httpClient = HttpClientBuilder.create().build()
        val getMethod = HttpGet("http://127.0.0.1:3030/api/screenshot/$submissionId/$slideNumber")

        val rawResponse = httpClient.execute(getMethod)
        val bytes: ByteArray = rawResponse.entity.content.readAllBytes()

        val slide = File(workingDir, "${slideNumber}_$submissionId.png")

        return byteArrayToImage(bytes, slide)
    }


    private fun byteArrayToImage(data: ByteArray, saveTo: File): File {
        val bis = ByteArrayInputStream(data)
        val bufferedImg = ImageIO.read(bis)

        ImageIO.write(bufferedImg, "png", saveTo)
        return saveTo
    }
}