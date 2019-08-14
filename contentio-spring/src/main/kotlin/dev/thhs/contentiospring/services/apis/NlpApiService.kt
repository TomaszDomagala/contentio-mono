package dev.thhs.contentiospring.services.apis

import com.google.gson.Gson
import dev.thhs.contentiospring.models.webrequests.TextToSentencesRequest
import dev.thhs.contentiospring.models.webrequests.TextToSentencesResponse
import dev.thhs.contentiospring.utils.createCommandProcess
import dev.thhs.contentiospring.utils.logger
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.stereotype.Service
import java.lang.AssertionError
import javax.annotation.PreDestroy

/**
 * @author Tomasz Domagała
 *  Service for python nlp api
 */
@Service
object NlpApiService {

    private val log by logger()
    private val nlpLocation = System.getenv("NLP_LOCATION")
            ?: throw AssertionError("No CC_VARS environment variable set")
    private var nlpProcess: Process = startNlpProcess()

    private fun startNlpProcess(): Process {
        val startCommand = "python $nlpLocation"
//        return startCommand.runCommand()

        return startCommand.createCommandProcess()
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
    }

    @PreDestroy
    fun destroyNlpProcess() {
        log.info("Destroying nlp process")
        if (nlpProcess.isAlive) nlpProcess.destroy()
    }


    fun textToSentences(text: String): TextToSentencesResponse {
        val httpClient = HttpClientBuilder.create().build()
        val gson = Gson()
        val jsonString = gson.toJson(TextToSentencesRequest(text))

        val requestEntity = StringEntity(jsonString, ContentType.APPLICATION_JSON)
        val postMethod = HttpPost("http://localhost:5000/text-to-sentences")
        postMethod.entity = requestEntity

        val rawResponse = httpClient.execute(postMethod)
        val responseJson = String(rawResponse.entity.content.readAllBytes())
        //        log.info("Text to sentence complete")
        return gson.fromJson(responseJson, TextToSentencesResponse::class.java)
    }

}