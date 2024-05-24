package com.github.robson021.debtmanager.extensions

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import java.math.BigDecimal

fun OAuth2AuthenticationToken.userDetails(): GoogleUser {
    val principal = this.principal as DefaultOAuth2User
    val attr = principal.attributes
    val scope = principal.authorities.last().authority

    val email = attr["email"]
    val isGoogle = email != null && ((email as String).contains("@gmail."))

    return when {
        isGoogle -> fromGoogle(attr, scope, email as String, (principal as DefaultOidcUser).idToken.tokenValue)
        else -> throw RuntimeException("Unknown OAuth2 authentication token")
    }

}

private fun fromGoogle(attr: Map<String, Any>, scope: String, email: String, token: String) = GoogleUser(
    (attr["sub"] as String).toBigDecimal(),
    attr["name"] as String,
    attr["picture"] as String,
    scope,
    email,
    token,
)

data class GoogleUser(
    val sub: BigDecimal,
    val name: String,
    val avatar: String,
    val scope: String,
    val email: String,
    val token: String,
)
