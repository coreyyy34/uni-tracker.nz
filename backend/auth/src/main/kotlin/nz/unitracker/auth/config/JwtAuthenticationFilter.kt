package nz.unitracker.auth.config

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nz.unitracker.auth.domain.auth.model.JwtTokenType
import nz.unitracker.auth.domain.auth.service.JwtService
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
            val jwt = jwtService.validateAccessToken(accessToken)
            if (jwt != null) {
                val userId = jwt.userId
                val principal = JwtUserDetails(jwt.userId)
                val auth = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
                SecurityContextHolder.getContext().authentication = auth
                utLogger.debug { "JWT validated. User ${userId.id} authenticated." }
            } else {
                utLogger.debug { "Invalid or expired JWT for request to ${request.requestURI}" }
            }
        }

        filterChain.doFilter(request, response)
    }
}
