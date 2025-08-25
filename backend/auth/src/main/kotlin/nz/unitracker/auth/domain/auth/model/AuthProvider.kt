package nz.unitracker.auth.domain.auth.model

/**
 * Enum representing the supported authentication providers.
 */
enum class AuthProvider(
    val registrationId: String,
) {
    /**
     * Local email/password based login.
     */
    PASSWORD("password"),

    /**
     * Authentication via Google OAuth.
     */
    GOOGLE("google"),

    /**
     * Authentication via Microsoft OAuth (Azure AD or Microsoft Accounts).
     */
    MICROSOFT("microsoft"),

    ;

    companion object {
        fun fromRegistrationId(id: String): AuthProvider? = entries.firstOrNull { it.registrationId.equals(id, ignoreCase = true) }
    }
}
