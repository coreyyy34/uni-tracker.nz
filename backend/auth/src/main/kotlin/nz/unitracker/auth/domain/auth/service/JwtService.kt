package nz.unitracker.auth.domain.auth.service

import jakarta.servlet.http.Cookie
import nz.unitracker.auth.config.properties.JwtProperties
import nz.unitracker.auth.domain.auth.model.AuthToken
import nz.unitracker.auth.domain.auth.model.JwtTokenType
import nz.unitracker.auth.domain.auth.model.ParsedJwt
import nz.unitracker.auth.domain.user.model.UserId
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
     * @return [AuthToken] containing the JWT string and its cookie.
     */
    fun generateAccessToken(userId: UserId): AuthToken = generateToken(JwtTokenType.ACCESS, jwtProperties.accessLifetime, userId)

    /**
     * Generates a refresh token and its associated cookie.
     *
     * @param userId the [UserId] of the user for whom the token is generated for.
     * @return [AuthToken] containing the JWT string and its cookie.
     */
    fun generateRefreshToken(userId: UserId): AuthToken = generateToken(JwtTokenType.REFRESH, jwtProperties.refreshLifetime, userId)

    /**
     * Validates an access token.
     *
     * @param token The raw JWT string to validate.
     * @return The decoded [ParsedJwt] if valid, otherwise `null`.
     */
    fun validateAccessToken(token: String): ParsedJwt? = validateToken(token, JwtTokenType.ACCESS)

    /**
     * Validates a refresh token.
     *
     * @param token The raw JWT string to validate.
     * @return The decoded [ParsedJwt] if valid, otherwise `null`.
     */
    fun validateRefreshToken(token: String): ParsedJwt? = validateToken(token, JwtTokenType.REFRESH)

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
     * @return [AuthToken] containing the JWT string and its cookie.
     */
    private fun generateToken(
        type: JwtTokenType,
        lifetime: Duration,
        userId: UserId,
    ): AuthToken {
        val now = Instant.now(clock)
        val claims =
            JwtClaimsSet
                .builder()
                .issuer("http://localhost:9000")
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plus(lifetime))
                .claim(TOKEN_TYPE_CLAIM_NAME, type.tokenName)
                .build()

        val token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue

        val cookie =
            Cookie(type.cookieName, token).apply {
                isHttpOnly = true
                secure = false
                path = "/"
                maxAge = lifetime.toSeconds().toInt()
            }
        return AuthToken(token, cookie)
    }

    /**
     * Validates a JWT against its expected type.
     *
     * @param token The raw JWT string.
     * @param type The expected token type.
     * @return The decoded [ParsedJwt] if valid and type matches, otherwise `null`.
     */
    private fun validateToken(
        token: String,
        type: JwtTokenType,
    ): ParsedJwt? =
        try {
            val jwt = jwtDecoder.decode(token)
            if (jwt.claims[TOKEN_TYPE_CLAIM_NAME] == type.tokenName) {
                ParsedJwt(userId = UserId(jwt.subject))
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
