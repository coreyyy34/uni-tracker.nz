package nz.unitracker.auth.domain.auth.service

import nz.unitracker.auth.domain.jwt.model.JwtAuthToken
import nz.unitracker.auth.domain.jwt.service.JwtService
import nz.unitracker.auth.domain.user.service.UserOAuthInfoExtractorService
import nz.unitracker.auth.domain.user.service.UserService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userService: UserService,
    private val userOAuthInfoExtractorService: UserOAuthInfoExtractorService,
) {
    @Transactional
    fun handleOAuthLogin(
        oauthUser: OAuth2User,
        authentication: OAuth2AuthenticationToken,
    ): List<JwtAuthToken> {
        val providerId = authentication.authorizedClientRegistrationId
        val userInfo = userOAuthInfoExtractorService.extractUserInfo(oauthUser, providerId)
        val user =
            userService.findUserByEmail(userInfo.email)
                ?: userService.createUser(userInfo.email, userInfo.firstName, userInfo.lastName)

        return listOf(
            jwtService.generateRefreshToken(user.id),
            jwtService.generateAccessToken(user.id, user.email),
        )
    }
}
