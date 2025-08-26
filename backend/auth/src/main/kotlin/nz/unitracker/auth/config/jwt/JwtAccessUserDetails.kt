package nz.unitracker.auth.config.jwt

import nz.unitracker.auth.domain.user.model.UserId
import org.springframework.security.core.GrantedAuthority

/**
 * Represents the details extracted from a JWT access token.
 *
 * @property userId The unique identifier of the user.
 * @property email The email address of the user.
 * @property authorities The collection of granted authorities associated with the user.
 */
data class JwtAccessUserDetails(
    val userId: UserId,
    val email: String,
    val authorities: Collection<GrantedAuthority>,
)
