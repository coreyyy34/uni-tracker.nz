package nz.unitracker.auth.domain.user.service

import nz.unitracker.auth.domain.user.model.UserOAuthInfo
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class UserOAuthInfoExtractorService {
    private val providerAttributeMap =
        mapOf(
            "google" to
                ProviderAttributes(
                    emailKey = "email",
                    firstNameKey = "given_name",
                    lastNameKey = "family_name",
                    idKey = "sub",
                ),
            "microsoft" to
                ProviderAttributes(
                    emailKey = "email",
                    firstNameKey = "givenName",
                    lastNameKey = "surname",
                    idKey = "id",
                ),
        )

    fun extractUserInfo(
        oauthUser: OAuth2User,
        providerId: String,
    ): UserOAuthInfo {
        val attributes =
            providerAttributeMap[providerId.lowercase()]
                ?: throw IllegalArgumentException("Unsupported provider: $providerId")

        val email =
            oauthUser.attributes[attributes.emailKey] as? String
                ?: throw IllegalStateException("Email not found for provider $providerId")

        val firstName = oauthUser.attributes[attributes.firstNameKey] as? String
        val lastName = oauthUser.attributes[attributes.lastNameKey] as? String
        val providerUserId =
            oauthUser.attributes[attributes.idKey] as? String
                ?: throw IllegalStateException("Provider ID not found for provider $providerId")

        return UserOAuthInfo(
            email = email,
            firstName = firstName ?: "",
            lastName = lastName ?: "",
            provider = providerId,
            providerId = providerUserId,
        )
    }

    data class ProviderAttributes(
        val emailKey: String,
        val firstNameKey: String,
        val lastNameKey: String,
        val idKey: String,
    )
}
