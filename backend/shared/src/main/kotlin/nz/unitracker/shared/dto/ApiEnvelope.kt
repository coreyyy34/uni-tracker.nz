package nz.unitracker.nz.unitracker.shared.dto

import java.time.Instant

/**
 * Represents a generic envelope for API responses.
 *
 * @property timestamp The UTC timestamp when the response was generated.
 */
sealed interface ApiEnvelope {
    val timestamp: Instant
}