package nz.unitracker.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val authenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
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
            }.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
