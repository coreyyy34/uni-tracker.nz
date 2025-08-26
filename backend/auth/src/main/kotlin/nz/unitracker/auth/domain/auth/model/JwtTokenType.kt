package nz.unitracker.auth.domain.auth.model

/**
 * Enum representing the supported JWT types used in authentication.
 *
 * @property cookieName The name of the HTTP cookie where this token type is stored on the client.
 * @property tokenName The identifier stored inside the JWT's claims to distinguish between different token types.
 */
enum class JwtTokenType(
    val cookieName: String,
    val tokenName: String,
) {
    /**
     * The access token type which is a short-lived token used for authenticating API requests.
     */
    ACCESS(
        cookieName = "ut_at",
        tokenName = "access",
    ),

    /**
     * The refresh token type which is a longer-lived token used to obtain new access tokens when the old one expires.
     */
    REFRESH(
        cookieName = "ut_rt",
        tokenName = "refresh",
    ),
}
