package no.nav.cache.cache

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import com.nimbusds.jwt.SignedJWT
import no.nav.cache.K9BrukerdialogCacheApplication
import no.nav.cache.cache.CacheController.Endpoints.CACHE_PATH
import no.nav.cache.utils.RestTemplateUtils.deleteAndAssert
import no.nav.cache.utils.RestTemplateUtils.getAndAssert
import no.nav.cache.utils.RestTemplateUtils.postAndAssert
import no.nav.cache.utils.RestTemplateUtils.putAndAssert
import no.nav.cache.utils.hentToken
import no.nav.cache.utils.tokenTilHeader
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.awaitility.kotlin.await
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
import org.zalando.problem.Problem
import java.time.Duration
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime


@SpringBootTest(
    classes = [K9BrukerdialogCacheApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(SpringExtension::class)
@EnableMockOAuth2Server
@ActiveProfiles("test")
internal class IntegrationTest {

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
    fun `gitt cache lagres, forvent samme verdi ved henting`() {
        val requestDTO = CacheRequestDTO(
            n??kkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utl??psdato = ZonedDateTime.now().plusDays(3)
        )

        val cacheResponseDTO: CacheResponseDTO = restTemplate.postAndAssert(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        val cacheInDB = cacheRepository.findByN??kkel(cacheResponseDTO.n??kkel)
        assertThat(cacheInDB).isNotNull()
        assertThat(cacheInDB!!.verdi).isNotEqualTo(requestDTO.verdi)

        restTemplate.getAndAssert<Any, CacheResponseDTO>(
            uri = "$CACHE_PATH/mellomlagring_psb",
            request = HttpEntity(/* headers = */  hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.OK,
            expectedBody = cacheResponseDTO
        )
    }

    @Test
    fun `gitt cache eksisterer, forvent at den oppdateres som forventet`() {
        val utl??psdato = ZonedDateTime.now(UTC).plusDays(3)
        val requestDTO = CacheRequestDTO(
            n??kkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utl??psdato = utl??psdato
        )

        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        val body = restTemplate.putAndAssert<CacheRequestDTO, CacheResponseDTO>(
            uri = "$CACHE_PATH/mellomlagring_psb",
            request = HttpEntity(
                /* body = */ requestDTO.copy(verdi = "endret-verdi-456"),
                /* headers = */hentToken().tokenTilHeader()
            ),
            expectedStatus = HttpStatus.OK,
            expectedBody = null
        )
        assertThat(body as CacheResponseDTO).isNotNull()
        assertThat(body.n??kkel).isEqualTo("mellomlagring_psb_12345678910")
        assertThat(body.verdi).isEqualTo("endret-verdi-456")

    }

    @Test
    fun `gitt cache eksisterer, forvent at den blir slettet som forventet`() {
        val requestDTO = CacheRequestDTO(
            n??kkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utl??psdato = ZonedDateTime.now().plusDays(3)
        )
        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        restTemplate.deleteAndAssert<Any>(
            uri = "$CACHE_PATH/mellomlagring_psb",
            request = HttpEntity(/* headers = */  hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.NO_CONTENT
        )
    }

    @Test
    fun `gitt cache ikke eksisterer, forvent ikke-funnet feil`() {
        restTemplate.deleteAndAssert<Any>(
            uri = "$CACHE_PATH/mellomlagring_psb",
            request = HttpEntity(/* headers = */  hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.NOT_FOUND
        )
    }

    @Test
    fun `gitt cache med n??kkelPrefiks eksisterer p?? person, forvent konfliktfeil`() {
        val requestDTO = CacheRequestDTO(
            n??kkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utl??psdato = ZonedDateTime.now().plusDays(3)
        )
        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        restTemplate.postAndAssert<CacheRequestDTO, Problem>(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CONFLICT
        )
    }

    @Test
    fun `gitt lagret cache p?? en person, forvent ikke funnet n??r en annen person henter samme prefiks`() {
        val requestDTO = CacheRequestDTO(
            n??kkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utl??psdato = ZonedDateTime.now().plusDays(3)
        )

        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        restTemplate.getAndAssert<Any, Unit>(
            uri = "$CACHE_PATH/mellomlagring_psb",
            request = HttpEntity(/* headers = */  hentToken(fnr = "11111111111").tokenTilHeader()),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = Unit
        )
    }

    @Test
    fun `Gitt oppdatering av ikke eksisterende cache, forvent feil`() {
        val requestDTO = CacheRequestDTO(
            n??kkelPrefiks = "ikke-eksisterende-cache",
            verdi = "verdi-123",
            utl??psdato = ZonedDateTime.now().plusDays(3)
        )

        restTemplate.putAndAssert<CacheRequestDTO, Unit>(
            uri = "$CACHE_PATH/mellomlagring_psb",
            request = HttpEntity(
                /* body = */ requestDTO.copy(verdi = "endret-verdi-456"),
                /* headers = */hentToken().tokenTilHeader()
            ),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = null
        )
    }

    @Test
    fun `Gitt cache ikke eksisterer, forvent ikke-funnet feil`() {

        restTemplate.getAndAssert<Any, Unit>(
            uri = "$CACHE_PATH/ikke-eksisterende-cache",
            request = HttpEntity(/* headers = */  hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = Unit
        )
    }

    @Test
    fun `gitt cache lagres, mens hentes p?? annen prefiks, forvent null`() {
        val requestDTO = CacheRequestDTO(
            n??kkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utl??psdato = ZonedDateTime.now().plusDays(3)
        )

        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            uri = CACHE_PATH,
            request = HttpEntity(/* body = */ requestDTO, /* headers = */ hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.CREATED
        )

        // samt, at hentet oppf??ring er lik den som ble lagret i db.
        restTemplate.getAndAssert<Any, Unit>(
            uri = "$CACHE_PATH/mellomlagring_pnn",
            request = HttpEntity(/* headers = */  hentToken().tokenTilHeader()),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = Unit
        )
    }

    @Test
    fun `gitt 2 utg??tte cache, forvent at begge slettes`() {
        val now = ZonedDateTime.now(UTC)
        val utl??psdato = now.minusMinutes(1)

        cacheRepository.saveAll(
            listOf(
                CacheEntryDAO(
                    n??kkel = "n??kkel-1",
                    verdi = "cache som utl??per 1",
                    utl??psdato = utl??psdato,
                    opprettet = now.minusHours(1)
                ),
                CacheEntryDAO(
                    n??kkel = "n??kkel-2",
                    verdi = "cache som utl??per 2",
                    utl??psdato = utl??psdato,
                    opprettet = now.minusHours(1)
                ),
                CacheEntryDAO(
                    n??kkel = "n??kkel-3",
                    verdi = "skal ikke utl??pe enda",
                    utl??psdato = utl??psdato.plusMinutes(30),
                    opprettet = now.minusHours(1)
                )
            )
        )
        assertThat(cacheRepository.count()).isEqualTo(3)
        await.atMost(Duration.ofSeconds(2)).untilAsserted {
            assertThat(cacheRepository.count()).isEqualTo(1)
        }
    }

    private fun hentToken(fnr: String = "12345678910"): SignedJWT = mockOAuth2Server.hentToken(subject = fnr)
}
