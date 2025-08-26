package nz.unitracker.auth.config

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nz.unitracker.auth.config.properties.AppProperties
import nz.unitracker.auth.domain.auth.service.AuthService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val appProperties: AppProperties,
    private val authService: AuthService,
) : AuthenticationSuccessHandler {
    private val logger = KotlinLogging.logger { }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val oauthUser = authentication.principal as? OAuth2User
        val oauthToken = authentication as? OAuth2AuthenticationToken

        if (oauthUser == null || oauthToken == null) {
            logger.warn { "Invalid authentication object: ${authentication::class.simpleName}" }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid authentication")
            return
        }

        try {
            val tokens = authService.handleOAuthLogin(oauthUser, oauthToken)
            tokens.forEach { token -> response.addCookie(token.cookie) }

            response.sendRedirect(appProperties.client.redirectUrl)
        } catch (e: Exception) {
            logger.error(e) { "Failed to process OAuth2 login" }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to extract user info")
        }
    }
}
