package nz.unitracker.auth.config.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nz.unitracker.auth.domain.jwt.model.JwtTokenType
import nz.unitracker.auth.domain.jwt.service.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
) : OncePerRequestFilter() {
    private val utLogger = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessToken =
            request.cookies
                ?.firstOrNull { it.name == JwtTokenType.ACCESS.cookieName }
                ?.value

        if (accessToken == null) {
            utLogger.debug { "No access token cookie found on request to ${request.requestURI}" }
        } else {
            val userDetails = jwtService.validateAccessToken(accessToken)
            if (userDetails != null) {
                val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = auth
                utLogger.debug { "JWT validated. User ${userDetails.userId} authenticated." }
            } else {
                utLogger.debug { "Invalid or expired JWT for request to ${request.requestURI}" }
            }
        }

        filterChain.doFilter(request, response)
    }
}
