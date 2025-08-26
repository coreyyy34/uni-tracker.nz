package nz.unitracker.auth.domain.auth.model

import nz.unitracker.auth.domain.user.model.UserId

data class ParsedJwt(
    val userId: UserId,
)
