package nz.unitracker.auth.domain.user.model

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
)
