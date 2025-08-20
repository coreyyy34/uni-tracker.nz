package nz.unitracker.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppConfig {
    lateinit var auth: AuthProperties
    lateinit var client: ClientProperties

    class AuthProperties {
        lateinit var baseUrl: String
    }

    class ClientProperties {
        lateinit var redirectUrl: String
    }
}
