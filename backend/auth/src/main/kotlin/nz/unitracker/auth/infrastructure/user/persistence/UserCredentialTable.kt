package nz.unitracker.auth.infrastructure.user.persistence

import nz.unitracker.auth.infrastructure.BaseTable
import org.jetbrains.exposed.v1.core.ReferenceOption

/**
 * Database table for storing user authentication credentials.
 *
 * Supports both password-based authentication and external OAuth providers.
 */
object UserCredentialTable : BaseTable("auth_user_credential") {

    /**
     * A reference to the owning user.
     */
    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE)

    /**
     * The name of the authentication provider.
     */
    val provider = varchar("provider", 50)

    /**
     * The hashed password, only set when using the "password" provider.
     * Nullable for OAuth-based accounts.
     */
    val password = varchar("password", 255).nullable()

    /**
     * The external provider's user identifier.
     */
    val providerUserId = varchar("provider_user_id", 255).nullable()

    /**
     * The OAuth access token.
     */
    val accessToken = text("access_token").nullable()

    /**
     * The OAuth refresh token.
     */
    val refreshToken = text("refresh_token").nullable()
}