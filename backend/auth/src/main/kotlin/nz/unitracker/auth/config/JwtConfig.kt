package nz.unitracker.auth.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import nz.unitracker.auth.config.properties.JwtProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64

/**
 * Configuration class for setting up JWT encoding and decoding.
 *
 * This class loads RSA keys from Base64-encoded strings defined in application properties
 * and exposes Spring Beans for [JwtDecoder] and [JwtEncoder].
 */
@Configuration
class JwtConfig(
    private val jwtProperties: JwtProperties,
) {
    private val rsaPrivateKey by lazy { loadPrivateKey() }
    private val rsaPublicKey by lazy { loadPublicKey() }

    /**
     * Creates a [JwtDecoder] bean using the public RSA key.
     *
     * This is used to verify incoming JWT tokens.
     *
     * @return a [JwtDecoder] instance configured with the application’s public key.
     */
    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build()

    /**
     * Creates a [JwtEncoder] bean using the RSA key pair.
     *
     * This is used to sign outgoing JWT tokens.
     *
     * @return a [JwtEncoder] instance configured with the application’s RSA key pair.
     */
    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk =
            RSAKey
                .Builder(rsaPublicKey)
                .privateKey(rsaPrivateKey)
                .build()
        val jwkSource = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
        return NimbusJwtEncoder(jwkSource)
    }

    /**
     * Loads the RSA private key from a Base64-encoded PKCS#8 string.
     *
     * PKCS#8 is a standard syntax for storing private key information.
     * It is widely used because it supports multiple key types and optional encryption.
     *
     * @return the [RSAPrivateKey] instance for signing JWTs.
     */
    private fun loadPrivateKey(): RSAPrivateKey {
        val bytes = Base64.decode(jwtProperties.privateKey)
        val spec = PKCS8EncodedKeySpec(bytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(spec) as RSAPrivateKey
    }

    /**
     * Loads the RSA public key from a Base64-encoded X.509 string.
     *
     * X.509 is a standard format for public key certificates and key encoding.
     * It defines a structure for public keys that includes algorithm information.
     *
     * @return the [RSAPublicKey] instance for verifying JWTs.
     */
    private fun loadPublicKey(): RSAPublicKey {
        val bytes = Base64.decode(jwtProperties.publicKey)
        val spec = X509EncodedKeySpec(bytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(spec) as RSAPublicKey
    }
}
