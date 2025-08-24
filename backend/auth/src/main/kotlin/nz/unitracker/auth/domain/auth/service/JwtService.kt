package nz.unitracker.auth.domain.auth.service

import jakarta.servlet.http.Cookie
import nz.unitracker.auth.config.properties.JwtProperties
import nz.unitracker.auth.domain.auth.model.AuthToken
import nz.unitracker.auth.domain.auth.model.JwtTokenType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class JwtService(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    private val jwtProperties: JwtProperties,
) {
    companion object {
        const val TOKEN_TYPE_CLAIM_NAME = "typ"
    }

    fun generateAccessToken(): AuthToken = generateToken(JwtTokenType.ACCESS, jwtProperties.accessLifetime)

    fun generateRefreshToken(): AuthToken = generateToken(JwtTokenType.REFRESH, jwtProperties.refreshLifetime)

    fun validateAccessToken(token: String): Jwt? = validateToken(token, JwtTokenType.ACCESS)

    fun validateRefreshToken(token: String): Jwt? = validateToken(token, JwtTokenType.REFRESH)

    fun createDeletionCookies(): List<Cookie> =
        listOf(
            createDeletionCookie(JwtTokenType.ACCESS.cookieName),
            createDeletionCookie(JwtTokenType.REFRESH.cookieName),
        )

    private fun generateToken(
        type: JwtTokenType,
        lifetime: Duration,
    ): AuthToken {
        val now = Instant.now()
        val claims =
            JwtClaimsSet
                .builder()
                .issuer("http://localhost:9000")
                .subject("username")
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

    private fun validateToken(
        token: String,
        type: JwtTokenType,
    ): Jwt? =
        try {
            val jwt = jwtDecoder.decode(token)
            if (jwt.claims[TOKEN_TYPE_CLAIM_NAME] == type.tokenName) jwt else null
        } catch (e: JwtException) {
            null
        }

    private fun createDeletionCookie(name: String): Cookie =
        Cookie(name, "").apply {
            maxAge = 0
            path = "/"
        }
}
