package nz.unitracker.auth.config

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nz.unitracker.auth.config.properties.AppProperties
import nz.unitracker.auth.domain.user.service.UserOAuthInfoExtractorService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val appProperties: AppProperties,
    private val userOauthInfoExtractorService: UserOAuthInfoExtractorService,
) : AuthenticationSuccessHandler {
    private val logger = KotlinLogging.logger { }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val oauthUser = authentication.principal as? OAuth2User
        if (oauthUser == null) {
            logger.warn { "Authentication principal is not an OAuth2User" }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid authentication")
            return
        }

        val providerId =
            (authentication as? OAuth2AuthenticationToken)
                ?.authorizedClientRegistrationId
                ?: run {
                    logger.warn { "No provider ID found in authentication token" }
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown provider")
                    return
                }

        try {
            val userInfo = userOauthInfoExtractorService.extractUserInfo(oauthUser, providerId)
            logger.debug { "Extracted UserInfo: $userInfo" }
            response.sendRedirect(appProperties.client.redirectUrl)
        } catch (e: Exception) {
            logger.error(e) { "Failed to process OAuth2 user for provider $providerId" }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to extract user info")
        }
    }
}
