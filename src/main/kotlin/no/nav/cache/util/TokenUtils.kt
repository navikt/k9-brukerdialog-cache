package no.nav.cache.util

import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder

object TokenUtils {
    fun SpringTokenValidationContextHolder.personIdentifikator() =
        getTokenValidationContext()
            .firstValidToken?.personIdentifikator()

    fun JwtToken.personIdentifikator(): String =
        if (jwtTokenClaims.allClaims.containsKey("pid")) {
            jwtTokenClaims.getStringClaim("pid")
        } else if (jwtTokenClaims.allClaims.containsKey("sub")) {
            jwtTokenClaims.getStringClaim("sub")
        } else {
            throw IllegalStateException("Token claims inneholder verken pid eller sub.")
        }
}
