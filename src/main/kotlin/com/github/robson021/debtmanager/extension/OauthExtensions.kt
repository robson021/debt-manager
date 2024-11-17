package com.github.robson021.debtmanager.extension

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.user.DefaultOAuth2User

fun OAuth2AuthenticationToken.getJwtToken() = (principal as DefaultOidcUser).idToken.tokenValue

fun OAuth2AuthenticationToken.userDetails(): GoogleUserDetails {
    val principal = this.principal as DefaultOAuth2User
    val attr = principal.attributes
    val scope = principal.authorities.last().authority

    val email = attr["email"]
    val isGoogle = email != null && ((email as String).endsWith("@gmail.com"))

    return when {
        isGoogle -> fromGoogle(attr, scope, email as String)
        else -> throw RuntimeException("Unknown OAuth2 authentication token: $email")
    }
}

private fun fromGoogle(attr: Map<String, Any>, scope: String, email: String) = GoogleUserDetails(
    attr["sub"] as String,
    attr["name"] as String,
    attr["picture"] as String,
    scope,
    email,
)

data class GoogleUserDetails(
    val sub: String,
    val name: String,
    val avatar: String,
    val scope: String,
    val email: String,
) {
    fun toShortString() = "$name, $email"
}
