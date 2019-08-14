package dev.thhs.contentiospring.services.apis

import dev.thhs.contentiospring.models.exceptions.InvalidSubmissionUrl
import dev.thhs.contentiospring.utils.logger
import net.dean.jraw.RedditClient
import net.dean.jraw.http.NetworkAdapter
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import net.dean.jraw.references.SubmissionReference
import org.springframework.stereotype.Service
import java.lang.AssertionError


/**
 * @author Tomasz Domagała https://github.com/TomaszDomagala
 * library used: JRAW https://github.com/mattbdean/JRAW
 *
 * @property ccVars is env var that contains reddit app information client_id;secret;username;password;agent
 *
 * @property submissionUrlToId A url to a submission in one of the following formats:
 *          https://redd.it/<submission_id>
 *          https://reddit.com/comments/<submission_id>/
 *          https://www.reddit.com/r/<subreddit>/comments/<submission_id>/<submission_title>/
 *
 * @property getSubmissionByUrl returns SubmissionReference
 *
 */

@Service
object RedditApiService {
    private val log by logger()
    private val ccVars: String = System.getenv("CC_VARS") ?: throw AssertionError("No CC_VARS environment variable set")
    private val reddit: RedditClient

    init {
        log.info("Initiating Reddit Integration...")
        val vars: List<String> = ccVars.split(";")
        val userAgent = UserAgent("script", "com.hypest.contentio", "1,0", vars[4])
        val credentials = Credentials.script(vars[2], vars[3], vars[0], vars[1])
        val adapter: NetworkAdapter = OkHttpNetworkAdapter(userAgent)
        reddit = OAuthHelper.automatic(adapter, credentials)
        log.info("Reddit Initiation Completed!")

    }

    fun submissionUrlToId(url: String): String {
        val parts = url.split("/")
        if (parts.isEmpty()) throw InvalidSubmissionUrl("Invalid submission url")

        return if ("comments" !in parts) {
            if ("r" in parts) throw InvalidSubmissionUrl("Invalid submission url")
            parts.last()
        } else {
            parts[parts.indexOf("comments") + 1]
        }
    }

    fun getSubmissionByUrl(url: String): SubmissionReference {
        val id = submissionUrlToId(url)
        return reddit.submission(id)
    }

    fun getSubmissionById(id: String): SubmissionReference {
        return reddit.submission(id)
    }



}