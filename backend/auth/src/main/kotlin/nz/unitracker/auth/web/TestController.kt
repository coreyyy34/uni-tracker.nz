package nz.unitracker.auth.web

import nz.unitracker.auth.config.jwt.JwtAccessUserDetails
import nz.unitracker.nz.unitracker.shared.dto.ApiEnvelope
import nz.unitracker.nz.unitracker.shared.dto.ApiResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/test")
    fun test(
        @AuthenticationPrincipal userDetails: JwtAccessUserDetails,
    ): ApiEnvelope = ApiResponse.now(userDetails)
}
