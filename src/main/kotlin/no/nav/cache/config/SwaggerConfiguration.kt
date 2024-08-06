package no.nav.cache.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders

@Configuration
@Profile("local", "dev-gcp")
class SwaggerConfiguration {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
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
                    .addSecuritySchemes("Authorization", tokenXApiToken())
            )
            .addSecurityItem(
                SecurityRequirement()
                    .addList("Authorization")
            )
    }

    private fun tokenXApiToken(): SecurityScheme {
        return SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .name(HttpHeaders.AUTHORIZATION)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .description(
                """Eksempel på verdi som skal inn i Value-feltet (Bearer trengs altså ikke å oppgis): 'eyAidH...'
                For nytt token -> https://tokenx-token-generator.intern.dev.nav.no/api/obo?aud=dev-gcp:dusseldorf:k9-brukerdialog-cache
            """.trimMargin()
            )
    }
}
