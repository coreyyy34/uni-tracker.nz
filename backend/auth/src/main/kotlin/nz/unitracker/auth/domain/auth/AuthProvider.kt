package nz.unitracker.auth.domain.auth

/**
 * Enum representing the supported authentication providers.
 */
enum class AuthProvider {
    /** Local email/password based login. */
    PASSWORD,

    /** Authentication via Google OAuth. */
    GOOGLE,

    /** Authentication via Microsoft OAuth (Azure AD or Microsoft Accounts). */
    MICROSOFT,
}
