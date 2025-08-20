package nz.unitracker.auth.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val appConfig: AppConfig
) : AuthenticationSuccessHandler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oauthUser = authentication.principal as OAuth2User
        logger.debug("OAuth2 authentication succeeded for user: {}", oauthUser.attributes["email"] ?: "unknown email")

        response.sendRedirect(appConfig.client.redirectUrl)
    }

}