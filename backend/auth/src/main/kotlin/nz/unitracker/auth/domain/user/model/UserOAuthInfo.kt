package nz.unitracker.auth.domain.user.model

data class UserOAuthInfo(
    val email: String,
    val firstName: String,
    val lastName: String,
    val provider: String,
    val providerId: String,
)
