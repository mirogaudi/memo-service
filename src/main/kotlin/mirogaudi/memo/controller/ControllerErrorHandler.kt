package mirogaudi.memo.controller

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.ConstraintViolationException
import mirogaudi.memo.service.NotFoundException
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.LocalDateTime

@ControllerAdvice
class ControllerErrorHandler {

    @ExceptionHandler(
        MethodArgumentTypeMismatchException::class,
        MethodArgumentNotValidException::class,
        ConstraintViolationException::class
    )
    fun requestErrorHandler(e: Exception): ResponseEntity<Error> = errorResponseEntity(BAD_REQUEST, e)

    @ExceptionHandler(NotFoundException::class)
    fun notFoundErrorHandler(e: Exception): ResponseEntity<Error> = errorResponseEntity(NOT_FOUND, e)

    @ExceptionHandler(
        ConcurrencyFailureException::class,
        DataIntegrityViolationException::class
    )
    fun conflictErrorHandler(e: Exception): ResponseEntity<Error> = errorResponseEntity(CONFLICT, e)

    @ExceptionHandler(Throwable::class)
    fun defaultErrorHandler(t: Throwable): ResponseEntity<Error> = errorResponseEntity(INTERNAL_SERVER_ERROR, t)

    private fun errorResponseEntity(
        status: HttpStatus,
        t: Throwable
    ): ResponseEntity<Error> = ResponseEntity.status(status).body(
        Error(
            timestamp = LocalDateTime.now(),
            status = status.value(),
            error = status.reasonPhrase,
            cause = t.toString()
        )
    )
}

data class Error(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val cause: String
)
