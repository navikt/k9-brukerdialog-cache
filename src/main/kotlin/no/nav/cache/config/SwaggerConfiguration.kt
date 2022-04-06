package no.nav.cache.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
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
    }
}
