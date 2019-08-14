package dev.thhs.contentiospring.models.webrequests

data class TextToSentencesRequest(val text: String)

data class ResponseSentence(val text: String, val paragraph: Int)
data class TextToSentencesResponse(val sentences: List<ResponseSentence>, val wordCount: Int)