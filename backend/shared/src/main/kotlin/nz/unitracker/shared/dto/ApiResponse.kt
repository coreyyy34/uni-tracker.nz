package nz.unitracker.nz.unitracker.shared.dto

import java.time.Instant

/**
 * Represents a successful API response containing optional data.
 *
 * @param T The type of the data payload.
 * @property data The actual response data, or `null` if no content is returned.
 */
data class ApiResponse<T>(
    override val timestamp: Instant,
    val data: T? = null
) : ApiEnvelope {

    companion object {

        /**
         * Creates a new [ApiResponse] with the [timestamp] automatically set to now.
         *
         * @param data Optional response data.
         * @return A new [ApiResponse] instance.
         */
        fun <T> now(
            data: T? = null
        ) = ApiResponse(
            timestamp = Instant.now(),
            data = data
        )
    }
}