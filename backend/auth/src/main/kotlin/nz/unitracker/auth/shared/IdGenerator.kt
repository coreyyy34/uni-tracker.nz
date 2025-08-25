package nz.unitracker.auth.shared

import io.github.thibaultmeyer.cuid.CUID
import org.springframework.stereotype.Component

@Component
class IdGenerator {
    fun generateId(): String = CUID.randomCUID2().toString()
}
