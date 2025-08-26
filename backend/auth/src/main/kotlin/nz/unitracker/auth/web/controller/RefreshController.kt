package nz.unitracker.auth.web.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nz.unitracker.auth.config.Routes
import nz.unitracker.auth.domain.auth.model.JwtTokenType
import nz.unitracker.auth.domain.auth.service.AuthService
import nz.unitracker.nz.unitracker.shared.dto.ApiEnvelope
import nz.unitracker.nz.unitracker.shared.dto.ApiResponse
import nz.unitracker.nz.unitracker.shared.exception.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.WebUtils.getCookie

@RestController
class RefreshController(
    private val authService: AuthService,
) {
    @PostMapping(Routes.REFRESH)
    fun postRefresh(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<ApiEnvelope> {
        val refreshToken =
            getCookie(request, JwtTokenType.REFRESH.cookieName)
                ?.value
                ?: throw UnauthorizedException("Refresh token not found")

        val tokens = authService.handleTokenRefresh(refreshToken)
        tokens.forEach { token -> response.addCookie(token.cookie) }

        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(ApiResponse.success())
    }
}
