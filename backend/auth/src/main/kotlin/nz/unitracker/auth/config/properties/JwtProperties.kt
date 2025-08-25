package nz.unitracker.auth.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    lateinit var accessLifetime: Duration
    lateinit var refreshLifetime: Duration

    /**
     * Base64-encoded PKCS#8 private key used for signing JWTs.
     */
    lateinit var privateKey: String

    /**
     * Base64-encoded X.509 public key used for verifying JWTs.
     */
    lateinit var publicKey: String
}
