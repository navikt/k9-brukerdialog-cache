package no.nav.cache.util

import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder

object TokenUtils {
    fun SpringTokenValidationContextHolder.personIdentifikator() =
        tokenValidationContext.firstValidToken.get().personIdentifikator()

    fun JwtToken.personIdentifikator(): String =
        jwtTokenClaims["pid"] as String?
            ?: jwtTokenClaims["sub"] as String?
            ?: throw IllegalStateException("Token claims inneholder verken pid eller sub.")
}
