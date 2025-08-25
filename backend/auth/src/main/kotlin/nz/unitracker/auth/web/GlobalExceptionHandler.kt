package nz.unitracker.auth.web

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import nz.unitracker.nz.unitracker.shared.dto.ApiEnvelope
import nz.unitracker.nz.unitracker.shared.dto.ApiError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import org.springframework.web.util.HtmlUtils

/**
 * Global exception handler for REST API errors.
 *
 * This class ensures that all exceptions are handled in a consistent way and
 * responses follow the `ApiEnvelope` format.
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = KotlinLogging.logger { }

    /**
     * Handles requests to resources that do not exist.
     *
     * @param exception the thrown [NoResourceFoundException]
     * @param request the incoming HTTP request
     * @return a 404 NOT_FOUND [ApiError] response containing the attempted path
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(
        exception: NoResourceFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiEnvelope> {
        val status = HttpStatus.NOT_FOUND
        val safePath = HtmlUtils.htmlEscape(request.requestURI)

        logger.debug(exception) { "No resource found at path: $safePath" }

        val apiResponse =
            ApiError.now(
                code = status.name,
                status = status.value(),
                path = safePath,
            )
        return ResponseEntity.status(status).body(apiResponse)
    }

    /**
     * Handles all unexpected exceptions that are not otherwise caught.
     *
     * @param exception the thrown [Exception]
     * @param request the incoming HTTP request
     * @return a 500 INTERNAL_SERVER_ERROR [ApiError] response
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        exception: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiEnvelope> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val safePath = HtmlUtils.htmlEscape(request.requestURI)

        logger.debug(exception) { "Unexpected exception encountered at path: $safePath" }

        val apiResponse =
            ApiError.now(
                code = status.name,
                status = status.value(),
                path = safePath,
            )
        return ResponseEntity.status(status).body(apiResponse)
    }
}
