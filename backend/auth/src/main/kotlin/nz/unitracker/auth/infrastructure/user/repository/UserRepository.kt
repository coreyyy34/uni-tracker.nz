package nz.unitracker.auth.infrastructure.user.repository

import nz.unitracker.auth.domain.user.model.User
import nz.unitracker.auth.domain.user.model.UserId
import nz.unitracker.auth.infrastructure.user.persistence.UserTable
import nz.unitracker.auth.shared.IdGenerator
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val idGenerator: IdGenerator,
) {
    fun findById(id: UserId): User? =
        UserTable
            .selectAll()
            .where { UserTable.id eq id.toString() }
            .singleOrNull()
            ?.let { toUser(it) }

    fun findByEmail(email: String): User? =
        UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .singleOrNull()
            ?.let { toUser(it) }

    fun createUser(
        email: String,
        firstName: String,
        lastName: String,
    ): User =
        UserTable
            .insert {
                it[UserTable.id] = idGenerator.generateId()
                it[UserTable.email] = email
                it[UserTable.firstName] = firstName
                it[UserTable.lastName] = lastName
            }.resultedValues!!
            .single()
            .let { toUser(it) }

    fun toUser(row: ResultRow): User =
        User(
            id = UserId(row[UserTable.id]),
            email = row[UserTable.email],
            firstName = row[UserTable.firstName],
            lastName = row[UserTable.lastName],
        )
}
