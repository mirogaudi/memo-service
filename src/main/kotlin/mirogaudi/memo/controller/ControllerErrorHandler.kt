package mirogaudi.memo.controller

import com.fasterxml.jackson.annotation.JsonFormat
import mirogaudi.memo.service.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class ControllerErrorHandler {

    @ExceptionHandler(NotFoundException::class)
    fun notFoundErrorHandler(e: Exception): ResponseEntity<Error> {
        return errorResponseEntity(HttpStatus.NOT_FOUND, e)
    }

    private fun errorResponseEntity(
        status: HttpStatus,
        t: Throwable
    ): ResponseEntity<Error> {
        return ResponseEntity.status(status).body(
            Error(
                LocalDateTime.now(),
                status.value(),
                t.toString()
            )
        )
    }
}

data class Error(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String
)
