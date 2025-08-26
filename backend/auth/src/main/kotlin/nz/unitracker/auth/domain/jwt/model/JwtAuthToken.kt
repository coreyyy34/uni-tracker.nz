package nz.unitracker.auth.domain.jwt.model

import jakarta.servlet.http.Cookie

/**
 * Represents an authentication token. It bundles together the raw JWT string value with a corresponding [Cookie]
 * configured for storage in the client.
 *
 * @property value The raw JWT string.
 * @property cookie The HTTP cookie containing the token.
 */
data class JwtAuthToken(
    val value: String,
    val cookie: Cookie,
)
