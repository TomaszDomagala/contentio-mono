package dev.thhs.contentiospring.models.webrequests

/**
 * @property duration is minimal duration of final video in seconds
 */
data class InitProjectRequest(val url: String, val duration: Int)

data class InitProjectResponse(val success: Boolean)

