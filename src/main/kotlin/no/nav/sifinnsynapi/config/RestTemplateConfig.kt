package no.nav.sifinnsynapi.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import no.nav.security.token.support.spring.validation.interceptor.BearerTokenClientHttpRequestInterceptor
import no.nav.sifinnsynapi.http.MDCValuesPropagatingClienHttpRequesInterceptor
import no.nav.sifinnsynapi.util.Constants.X_CORRELATION_ID
import no.nav.sifinnsynapi.util.Constants.X_NAV_APIKEY
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.util.*

@Configuration
class RestTemplateConfig(
        @Value("\${no.nav.gateways.k9-selvbetjening-oppslag}")
        private val oppslagsUrl: String,
        private val apigwConfig: ApiGwApiKeyConfig
) {

    @Bean(name = ["k9OppslagsKlient"])
    fun restTemplate(builder: RestTemplateBuilder, tokenInterceptor: BearerTokenClientHttpRequestInterceptor, mdcInterceptor: MDCValuesPropagatingClienHttpRequesInterceptor): RestTemplate {
        return builder
                .additionalMessageConverters(MappingJackson2HttpMessageConverter(k9SelvbetjeningOppslagKonfigurert()))
                .defaultHeader(X_NAV_APIKEY, apigwConfig.apiKey)
                .defaultHeader(X_CORRELATION_ID, UUID.randomUUID().toString())
                .rootUri(oppslagsUrl)
                .interceptors(tokenInterceptor, mdcInterceptor)
                .build()
    }
}

internal fun k9SelvbetjeningOppslagKonfigurert(): ObjectMapper {
    return ObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
        registerModule(JavaTimeModule())
    }
}
