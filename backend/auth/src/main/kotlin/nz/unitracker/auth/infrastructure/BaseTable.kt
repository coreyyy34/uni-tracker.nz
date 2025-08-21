package nz.unitracker.auth.infrastructure

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

/**
 * Abstract base table providing common audit columns for tables that use a CUID as the primary key.
 *
 * @param name The name of the table in the database.
 */
abstract class BaseTable(
    name: String,
) : Table(name) {
    /**
     * The unique row identifier stored as a CUID (30-char string).
     * */
    val id: Column<String> = varchar("id", 30)

    /**
     * The timestamp with time zone indicating when the row was created.
     * Defaults to the current time when a new row is inserted.
     */
    val createdAt =
        timestampWithTimeZone("created_at")
            .defaultExpression(CurrentTimestampWithTimeZone)

    /**
     * The timestamp with time zone indicating when the row was last updated.
     * Defaults to the current time when a new row is inserted.
     */
    val updatedAt =
        timestampWithTimeZone("updated_at")
            .defaultExpression(CurrentTimestampWithTimeZone)

    override val primaryKey = PrimaryKey(id)
}
