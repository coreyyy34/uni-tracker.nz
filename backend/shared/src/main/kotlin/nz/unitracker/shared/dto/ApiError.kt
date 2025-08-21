package nz.unitracker.nz.unitracker.shared.dto

import java.time.Instant

/**
 * Represents a standardized error response returned by the API.
 *
 * @property error A brief description of the error type.
 * @property code A custom application-level error code for programmatic handling.
 * @property message A detailed explanation of the error.
 * @property path The request path that triggered the error.
 */
data class ApiError(
    override val timestamp: Instant,
    val status: Int,
    val code: String,
    val path: String,
    val message: String? = null,
    val error: String? = null,
) : ApiEnvelope {
    companion object {
        /**
         * Creates a new [ApiError] instance with the [timestamp] automatically set to the current instant.
         *
         * @param code A custom application-level error code for programmatic handling.
         * @param status The HTTP status code corresponding to the error.
         * @param path The request path that triggered the error.
         * @param message A detailed explanation of the error.
         * @param error A brief description of the error type.
         * @return A new [ApiError] instance with [timestamp] set to [Instant.now].
         */
        fun now(
            code: String,
            status: Int,
            path: String,
            message: String? = null,
            error: String? = null,
        ) = ApiError(
            timestamp = Instant.now(),
            status = status,
            code = code,
            path = path,
            message = message,
            error = error,
        )
    }
}
