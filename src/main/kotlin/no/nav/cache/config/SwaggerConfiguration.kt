package no.nav.cache.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("local", "dev-gcp")
class SwaggerConfiguration {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addServersItem(
                Server().url("https://k9-brukerdialog-cache.dev.nav.no/").description("Swagger Server")
            )
            .info(
                Info()
                    .title("K9 Brukerdialog Cache")
                    .description("API spesifikasjon for k9-brukerdialog-cache")
                    .version("v1.0.0")
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("K9 Brukerdialog Cache GitHub repository")
                    .url("https://github.com/navikt/k9-brukerdialog-cache")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearer-jwt", SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .`in`(SecurityScheme.In.HEADER)
                            .name("Authorization")
                            .description("""
                                Må være en gyldig tokenX token scopet for dev-gcp:dusseldorf:k9-brukerdialog-cache.
                                For å teste apiet, kan du bruke debug-dings: https://debug-dings.dev-gcp.nais.io.
                                Velg innloggingsmetode, og deretter velg en testbruker som beskrevet her: https://docs.digdir.no/docs/idporten/idporten/idporten_testbrukere
                                Etter innlogging scroller du ned til `audience` og legger inn `dev-gcp:dusseldorf:k9-brukerdialog-cache` og henter token.
                                Kopier `accessToken` og lim den inn her.
                            """.trimIndent())
                    )
            )
            .addSecurityItem(SecurityRequirement().addList("bearer-jwt", listOf("read", "write")))
    }
}
