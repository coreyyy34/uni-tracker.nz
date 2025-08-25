package nz.unitracker.auth.domain.auth.service

import nz.unitracker.auth.domain.auth.model.AuthToken
import nz.unitracker.auth.domain.user.model.UserOAuthInfo
import nz.unitracker.auth.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userService: UserService,
) {
    @Transactional
    fun handleOAuthLogin(userInfo: UserOAuthInfo): List<AuthToken> {
        val user =
            userService.findUserByEmail(userInfo.email)
                ?: userService.createUser(userInfo.email, userInfo.firstName, userInfo.lastName)

        return listOf(
            jwtService.generateRefreshToken(),
            jwtService.generateAccessToken(),
        )
    }
}
