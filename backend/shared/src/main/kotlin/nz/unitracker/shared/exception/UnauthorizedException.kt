package nz.unitracker.nz.unitracker.shared.exception

class UnauthorizedException(
    override val message: String,
) : ApiException(message)
