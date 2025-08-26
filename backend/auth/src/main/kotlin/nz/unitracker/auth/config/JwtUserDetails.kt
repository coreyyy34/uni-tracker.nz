package nz.unitracker.auth.config

import nz.unitracker.auth.domain.user.model.UserId
import org.springframework.security.core.GrantedAuthority

class JwtUserDetails(
    val userId: UserId,
    val authorities: Collection<GrantedAuthority> = emptyList(),
)
