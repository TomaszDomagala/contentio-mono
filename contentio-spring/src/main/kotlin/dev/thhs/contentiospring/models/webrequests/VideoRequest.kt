package dev.thhs.contentiospring.models.webrequests

import dev.thhs.contentiospring.services.generators.SentenceVideoData

data class InitProjectVideoRequest(val id: Long)

sealed class InitProjectVideoResponse(val success: Boolean) {
    object Success : InitProjectVideoResponse(true)
    sealed class Failure : InitProjectVideoResponse(false) {
        object ProjectNotFound : Failure()
        data class MissingAssets(val incompleteSentences: List<SentenceVideoData.Invalid>): Failure()
    }

}