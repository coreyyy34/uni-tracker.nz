package nz.unitracker.auth.infrastructure.user.persistence

import nz.unitracker.auth.infrastructure.BaseTable

/**
 * Database table for storing authentication users.
 *
 * Contains core user information such as email and name.
 * Authentication credentials are stored separately in [UserCredentialTable].
 */
object UserTable : BaseTable("auth_user") {
    /**
     * The email address of the user.
     */
    val email = varchar("email", 255).uniqueIndex()

    /**
     * The first name of the user.
     */
    val firstName = varchar("first_name", 64)

    /**
     * The last name of the user.
     */
    val lastName = varchar("last_name", 64)
}
