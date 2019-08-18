package dev.thhs.contentiospring.models.webrequests


/**
 * @property duration is minimal duration of final video in seconds
 */
data class InitProjectRequest(val url: String, val duration: Int)

sealed class InitProjectResponse(val error: Boolean = false) {
    data class Success(val projectId: Long) : InitProjectResponse()
    data class Failure(val message: String) : InitProjectResponse(true)
}





