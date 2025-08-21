package nz.unitracker.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .anyRequest()
                    .permitAll()
            }.formLogin { form ->
                form
                    .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/success", true)
            }.oauth2Login { oauth2 ->
                oauth2
                    .successHandler(authenticationSuccessHandler)
            }.oauth2Client(withDefaults())
            .csrf { csrf ->
                csrf.ignoringRequestMatchers("/h2-console/**")
            }
        return http.build()
    }
}
