package nz.unitracker.auth.config.jwt

import nz.unitracker.auth.domain.user.model.UserId

/**
 * Represents the details extracted from a **JWT refresh token**.
 *
 * @property userId The unique identifier of the user.
 */
data class JwtRefreshUserDetails(
    val userId: UserId,
)
