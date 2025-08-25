package nz.unitracker.auth.domain.user.service

import nz.unitracker.auth.domain.user.model.User
import nz.unitracker.auth.infrastructure.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun findUserByEmail(email: String): User? = userRepository.findByEmail(email)

    @Transactional
    fun createUser(
        email: String,
        firstName: String,
        lastName: String,
    ) = userRepository.createUser(email, firstName, lastName)
}
