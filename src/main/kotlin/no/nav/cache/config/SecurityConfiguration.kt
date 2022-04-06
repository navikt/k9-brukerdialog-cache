package no.nav.cache.config

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@EnableJwtTokenValidation(ignore = ["org.springframework", "org.springdoc"])
@Configuration
internal class SecurityConfiguration
