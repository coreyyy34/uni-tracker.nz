package nz.unitracker.auth.domain.auth.service

import jakarta.servlet.http.Cookie
import nz.unitracker.auth.config.jwt.JwtAccessUserDetails
import nz.unitracker.auth.config.jwt.JwtRefreshUserDetails
import nz.unitracker.auth.config.properties.JwtProperties
import nz.unitracker.auth.domain.auth.model.JwtAuthToken
import nz.unitracker.auth.domain.auth.model.JwtTokenType
import nz.unitracker.auth.domain.user.model.UserId
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * Service class responsible for generating, validating and managing JSON Web Tokens for authentication.
 */
@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    private val jwtProperties: JwtProperties,
    private val clock: Clock = Clock.systemUTC(),
) {
    companion object {
        /**
         * Claim name used to distinguish between token types.
         */
        const val TOKEN_TYPE_CLAIM_NAME = "typ"
    }

    /**
     * Generates an access token and its associated cookie.
     *
     * @param userId the [UserId] of the user for whom the token is generated for.
     * @param email The email of the user to include in the token claims.
     * @return [JwtAuthToken] containing the JWT string and its cookie.
     */
    fun generateAccessToken(
        userId: UserId,
        email: String,
    ): JwtAuthToken =
        generateToken(
            JwtTokenType.ACCESS,
            jwtProperties.accessLifetime,
            userId,
            mapOf("email" to email),
        )

    /**
     * Generates a refresh token and its associated cookie.
     *
     * @param userId the [UserId] of the user for whom the token is generated for.
     * @return [JwtAuthToken] containing the JWT string and its cookie.
     */
    fun generateRefreshToken(userId: UserId): JwtAuthToken = generateToken(JwtTokenType.REFRESH, jwtProperties.refreshLifetime, userId)

    /**
     * Validates an access token and maps it to [JwtAccessUserDetails].
     *
     * @param token The raw JWT string to validate.
     * @return [JwtAccessUserDetails] if valid, otherwise `null`.
     */
    fun validateAccessToken(token: String): JwtAccessUserDetails? =
        validateToken(token, JwtTokenType.ACCESS) { jwt ->
            JwtAccessUserDetails(
                userId = UserId(jwt.subject),
                email = jwt.claims["email"] as? String ?: "",
                authorities =
                    (jwt.claims["roles"] as? List<*>)?.map {
                        SimpleGrantedAuthority(it.toString())
                    } ?: emptyList(),
            )
        }

    /**
     * Validates a refresh token and maps it to [JwtRefreshUserDetails]
     *
     * @param token The raw JWT string to validate.
     * @return [JwtRefreshUserDetails] if valid, otherwise `null`.
     */
    fun validateRefreshToken(token: String): JwtRefreshUserDetails? =
        validateToken(token, JwtTokenType.REFRESH) { jwt ->
            JwtRefreshUserDetails(userId = UserId(jwt.subject))
        }

    /**
     * Creates cookies that instruct the client to delete existing access and refresh token cookies.
     *
     * @return List of deletion cookies.
     */
    fun createDeletionCookies(): List<Cookie> =
        listOf(
            createDeletionCookie(JwtTokenType.ACCESS.cookieName),
            createDeletionCookie(JwtTokenType.REFRESH.cookieName),
        )

    /**
     * Generates a JWT with a given type and lifetime.
     *
     * @param type The token type ([JwtTokenType.ACCESS] or [JwtTokenType.REFRESH]).
     * @param lifetime The duration before the token expires.
     * @param userId the [UserId] of the user for whom the token is generated for.
     * @param extraClaims Optional additional claims to include in the token.
     * @return [JwtAuthToken] containing the JWT string and its cookie.
     */
    private fun generateToken(
        type: JwtTokenType,
        lifetime: Duration,
        userId: UserId,
        extraClaims: Map<String, Any> = emptyMap(),
    ): JwtAuthToken {
        val now = Instant.now(clock)
        val claimsBuilder =
            JwtClaimsSet
                .builder()
                .issuer("http://localhost:9000")
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plus(lifetime))
                .claim(TOKEN_TYPE_CLAIM_NAME, type.tokenName)
        extraClaims.forEach { (key, value) -> claimsBuilder.claim(key, value) }

        val claims = claimsBuilder.build()
        val token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
        val cookie =
            Cookie(type.cookieName, token).apply {
                isHttpOnly = true
                secure = false
                path = "/"
                maxAge = lifetime.toSeconds().toInt()
            }
        return JwtAuthToken(token, cookie)
    }

    /**
     * Validates a JWT against its expected type and maps it using a provided function.
     *
     * @param token The raw JWT string.
     * @param type The expected token type ([JwtTokenType.ACCESS] or [JwtTokenType.REFRESH]).
     * @param mapper A function that maps a decoded [Jwt] to a user detail object of type [T].
     * @return The mapped user detail object if valid, otherwise `null`.
     */
    private fun <T> validateToken(
        token: String,
        type: JwtTokenType,
        mapper: (Jwt) -> T?,
    ): T? =
        try {
            val jwt = jwtDecoder.decode(token)
            if (jwt.claims[TOKEN_TYPE_CLAIM_NAME] == type.tokenName) {
                mapper(jwt)
            } else {
                null
            }
        } catch (e: JwtException) {
            null
        }

    /**
     * Creates a cookie that instructs the client to delete an existing cookie by setting its max age to 0.
     *
     * @param name The cookie name to delete.
     * @return A [Cookie] configured for deletion.
     */
    private fun createDeletionCookie(name: String): Cookie =
        Cookie(name, "").apply {
            maxAge = 0
            path = "/"
        }
}
