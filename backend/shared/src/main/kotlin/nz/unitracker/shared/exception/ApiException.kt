package nz.unitracker.nz.unitracker.shared.exception

open class ApiException(
    override val message: String,
) : RuntimeException(message)
