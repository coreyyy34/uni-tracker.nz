package nz.unitracker.auth.domain.auth.model

import jakarta.servlet.http.Cookie

data class AuthToken(
    val value: String,
    val cookie: Cookie,
)
