package nz.unitracker.auth.domain.auth.service

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import nz.unitracker.auth.config.properties.JwtProperties
import nz.unitracker.auth.domain.jwt.model.JwtAuthToken
import nz.unitracker.auth.domain.jwt.model.JwtTokenType
import nz.unitracker.auth.domain.jwt.service.JwtService
import nz.unitracker.auth.domain.user.model.UserId
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import java.time.Clock
import java.time.Duration
import java.time.Instant

@ExtendWith(MockKExtension::class)
class JwtServiceTests {
    val jwtEncoder = mockk<JwtEncoder>()
    val jwtDecoder = mockk<JwtDecoder>()
    val jwtProperties = mockk<JwtProperties>()
    val clock = mockk<Clock>()
    val jwtService = JwtService(jwtEncoder, jwtDecoder, jwtProperties, clock)

    @Nested
    @DisplayName("JwtServiceTests - Access Tokens")
    inner class JwtServiceAccessTokenTests {
        @Test
        fun `should generate access token with correct properties`() {
            val userId = UserId("123456")
            val lifetime = Duration.ofMinutes(15)
            every { jwtProperties.accessLifetime } returns lifetime

            verifyTokenGeneration(
                JwtTokenType.ACCESS,
                userId,
                lifetime,
                { jwtService.generateAccessToken(userId) },
            ) { claims ->
                claims.claims[JwtService.TOKEN_TYPE_CLAIM_NAME] shouldBe JwtTokenType.ACCESS.tokenName
            }
        }

        @Test
        fun `should validate access token when type matches`() {
            val tokenValue = "valid-token"
            val jwt =
                Jwt
                    .withTokenValue(tokenValue)
                    .header("alg", "HS256")
                    .claim(JwtService.TOKEN_TYPE_CLAIM_NAME, JwtTokenType.ACCESS.tokenName)
                    .build()
            every { jwtDecoder.decode(tokenValue) } returns jwt

            val result = jwtService.validateAccessToken(tokenValue)
            result shouldBe jwt
        }

        @Test
        fun `should return null when validating access token with wrong type`() {
            val tokenValue = "invalid-token"
            val jwt =
                Jwt
                    .withTokenValue(tokenValue)
                    .header("alg", "HS256")
                    .claim(JwtService.TOKEN_TYPE_CLAIM_NAME, JwtTokenType.REFRESH.tokenName)
                    .build()
            every { jwtDecoder.decode(tokenValue) } returns jwt

            val result = jwtService.validateAccessToken(tokenValue)
            result shouldBe null
        }

        @Test
        fun `should return null when validating access token with invalid jwt`() {
            val tokenValue = "invalid-token"
            every { jwtDecoder.decode(tokenValue) } throws JwtException("Invalid token")

            val result = jwtService.validateAccessToken(tokenValue)
            result shouldBe null
        }
    }

    @Nested
    @DisplayName("JwtServiceTests - Refresh Tokens")
    inner class JwtServiceRefreshTokenTests {
        @Test
        fun `should generate refresh token with correct properties`() {
            val userId = UserId("123456")
            val lifetime = Duration.ofDays(7)
            every { jwtProperties.refreshLifetime } returns lifetime

            verifyTokenGeneration(
                JwtTokenType.REFRESH,
                userId,
                lifetime,
                { jwtService.generateRefreshToken(userId) },
            ) { claims ->
                claims.claims[JwtService.TOKEN_TYPE_CLAIM_NAME] shouldBe JwtTokenType.REFRESH.tokenName
            }
        }

        @Test
        fun `should validate refresh token when type matches`() {
            val tokenValue = "valid-token"
            val jwt =
                Jwt
                    .withTokenValue(tokenValue)
                    .header("alg", "HS256")
                    .claim(JwtService.TOKEN_TYPE_CLAIM_NAME, JwtTokenType.REFRESH.tokenName)
                    .build()
            every { jwtDecoder.decode(tokenValue) } returns jwt

            val result = jwtService.validateRefreshToken(tokenValue)
            result shouldBe jwt
        }

        @Test
        fun `should return null when validating refresh token with wrong type`() {
            val tokenValue = "invalid-token"
            val jwt =
                Jwt
                    .withTokenValue(tokenValue)
                    .header("alg", "HS256")
                    .claim(JwtService.TOKEN_TYPE_CLAIM_NAME, JwtTokenType.ACCESS.tokenName)
                    .build()
            every { jwtDecoder.decode(tokenValue) } returns jwt

            val result = jwtService.validateRefreshToken(tokenValue)
            result shouldBe null
        }

        @Test
        fun `should return null when validating access token with invalid jwt`() {
            val tokenValue = "invalid-token"
            every { jwtDecoder.decode(tokenValue) } throws JwtException("Invalid token")

            val result = jwtService.validateRefreshToken(tokenValue)
            result shouldBe null
        }
    }

    @Test
    fun `should create deletion cookies with correct attributes`() {
        val cookies = jwtService.createDeletionCookies()
        cookies shouldHaveSize 2

        val accessCookie = cookies.first { it.name == JwtTokenType.ACCESS.cookieName }
        accessCookie.value shouldBe ""
        accessCookie.maxAge shouldBe 0
        accessCookie.path shouldBe "/"

        val refreshCookie = cookies.first { it.name == JwtTokenType.REFRESH.cookieName }
        refreshCookie.value shouldBe ""
        refreshCookie.maxAge shouldBe 0
        refreshCookie.path shouldBe "/"
    }

    private fun verifyTokenGeneration(
        expectedType: JwtTokenType,
        userId: UserId,
        lifetime: Duration,
        tokenGenerator: () -> JwtAuthToken,
        additionalClaimsVerification: (JwtClaimsSet) -> Unit = {},
    ) {
        val expectedTokenValue = "test-token"
        val capturedParams = slot<JwtEncoderParameters>()
        val fixedTime = Instant.parse("2023-12-01T10:00:00Z")
        val expectedExpiry = fixedTime.plus(lifetime)

        every { clock.instant() } returns fixedTime
        every { jwtEncoder.encode(capture(capturedParams)) } returns
            mockk { every { tokenValue } returns expectedTokenValue }

        val (token, cookie) = tokenGenerator()
        token shouldBe expectedTokenValue
        cookie.name shouldBe expectedType.cookieName
        cookie.value shouldBe expectedTokenValue
        cookie.isHttpOnly shouldBe true
        cookie.secure shouldBe false
        cookie.path shouldBe "/"
        cookie.maxAge shouldBe lifetime.toSeconds().toInt()

        val claims = capturedParams.captured.claims
        claims.issuer.toString() shouldBe "http://localhost:9000"
        claims.subject shouldBe userId.id
        claims.issuedAt shouldBe fixedTime
        claims.expiresAt shouldBe expectedExpiry

        additionalClaimsVerification(claims)
    }
}
