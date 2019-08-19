package dev.thhs.contentiospring.models.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No statement found on submission")
class NoStatementFound(message: String = "No statement found on submission") : RuntimeException(message)