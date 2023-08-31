package no.nav.cache

import no.nav.cache.utkast.MineSiderProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
    exclude = [
        ErrorMvcAutoConfiguration::class
    ]
)
@EnableScheduling
@ConfigurationPropertiesScan("no.nav.cache")
@EnableConfigurationProperties(MineSiderProperties::class)
class K9BrukerdialogCacheApplication

fun main(args: Array<String>) {
    runApplication<K9BrukerdialogCacheApplication>(*args)
}
