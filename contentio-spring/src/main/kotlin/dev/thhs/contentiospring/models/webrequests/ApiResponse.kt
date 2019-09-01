package dev.thhs.contentiospring.models.webrequests

sealed class ApiResponse(val success: Boolean)

open class SuccessResponse() : ApiResponse(true)

data class ErrorResponse(val errorMessage: String) : ApiResponse(false)