package nz.unitracker.auth.domain.auth.model

enum class JwtTokenType(
    val cookieName: String,
    val tokenName: String,
) {
    ACCESS(
        cookieName = "ut_at",
        tokenName = "access",
    ),
    REFRESH(
        cookieName = "ut_rt",
        tokenName = "refresh",
    ),
}
