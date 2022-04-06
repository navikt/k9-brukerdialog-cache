package no.nav.cache.cache

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.nimbusds.jwt.SignedJWT
import no.nav.cache.K9BrukerdialogCacheApplication
import no.nav.cache.cache.CacheController.Endpoints.CACHE_PATH
import no.nav.cache.utils.RestTemplateUtils.getAndAssert
import no.nav.cache.utils.RestTemplateUtils.postAndAssert
import no.nav.cache.utils.hentToken
import no.nav.cache.utils.tokenTilHeader
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZonedDateTime


@SpringBootTest(
    classes = [K9BrukerdialogCacheApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(SpringExtension::class)
@EnableMockOAuth2Server
@ActiveProfiles("test")
internal class CacheControllerTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var cacheRepository: CacheRepository

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    @BeforeEach
    internal fun setUp() {
        assertNotNull(restTemplate)
        cacheRepository.deleteAll()
    }

    @Test
    internal fun `gitt cache lagres, forvent samme verdi ved henting`() {
        // Gitt følgende cache oppføring...
        val requestDTO = CacheRequestDTO(
            nøkkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utløpsdato = ZonedDateTime.now().plusDays(3)
        )

        // forvent at den blir opprettet...
        val cacheResponseDTO: CacheResponseDTO = restTemplate.postAndAssert(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        // og at den eksisterer i db med kryptert verdi...
        val cacheInDB = cacheRepository.findByNøkkel(cacheResponseDTO.nøkkel)
        assertThat(cacheInDB).isNotNull()
        assertThat(cacheInDB!!.verdi).isNotEqualTo(requestDTO.verdi)

        // samt, at hentet oppføring er lik den som ble lagret i db.
        restTemplate.getAndAssert<Any, CacheResponseDTO>(
            uri = "$CACHE_PATH/mellomlagring_psb",
            request = HttpEntity(/* headers = */  hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.OK,
            expectedBody = cacheResponseDTO
        )
    }

    @Test
    internal fun `gitt lagret cache på en person, forvent ikke funnet når en annen person henter samme prefiks`() {
        // Gitt følgende cache oppføring...
        val requestDTO = CacheRequestDTO(
            nøkkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utløpsdato = ZonedDateTime.now().plusDays(3)
        )

        // forvent at den blir opprettet...
        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        // samt, at hentet oppføring er lik den som ble lagret i db.
        restTemplate.getAndAssert<Any, Unit>(
            uri = "$CACHE_PATH/mellomlagring_psb",
            request = HttpEntity(/* headers = */  hentToken(fnr = "11111111111").tokenTilHeader()),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = null
        )
    }

    @Test
    internal fun `gitt cache lagres, mens hentes på annen prefiks, forvent null`() {
        // Gitt følgende cache oppføring...
        val requestDTO = CacheRequestDTO(
            nøkkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utløpsdato = ZonedDateTime.now().plusDays(3)
        )

        // forvent at den blir opprettet...
        val cacheResponseDTO: CacheResponseDTO = restTemplate.postAndAssert(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        // samt, at hentet oppføring er lik den som ble lagret i db.
        restTemplate.getAndAssert<Any, Unit>(
            uri = "$CACHE_PATH/mellomlagring_pnn",
            request = HttpEntity(/* headers = */  hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = null
        )
    }

    private fun hentToken(fnr: String = "12345678910"): SignedJWT = mockOAuth2Server.hentToken(subject = fnr)
}
