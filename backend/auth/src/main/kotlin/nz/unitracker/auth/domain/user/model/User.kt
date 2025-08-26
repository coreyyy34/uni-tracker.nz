package nz.unitracker.auth.domain.user.model

@JvmInline
value class UserId(
    val id: String,
) {
    override fun toString(): String = id
}

data class User(
    val id: UserId,
    val email: String,
    val firstName: String,
    val lastName: String,
)
