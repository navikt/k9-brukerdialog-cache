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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.RequestEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
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
        val postRequest = RequestEntity
            .post(CACHE_PATH)
            .headers(hentToken().tokenTilHeader())
            .body(
                CacheRequestDTO(
                    nøkkelPrefiks = "mellomlagring_psb",
                    verdi = "verdi-123",
                    utløpsdato = ZonedDateTime.now().plusDays(3)
                )
            )
        val cacheResponseDTO: CacheResponseDTO = restTemplate.postAndAssert(
            request = postRequest,
            expectedStatus = HttpStatus.CREATED
        )

        val cacheInDB = cacheRepository.findByNøkkel(cacheResponseDTO.nøkkel)
        assertThat(cacheInDB).isNotNull()
        assertThat(cacheInDB!!.verdi).isNotEqualTo(postRequest.body!!.verdi)

        restTemplate.getAndAssert(
            request = RequestEntity
                .get("$CACHE_PATH/mellomlagring_psb")
                .headers(hentToken().tokenTilHeader())
                .build(),
            expectedStatus = HttpStatus.OK,
            expectedBody = cacheResponseDTO
        )
    }

    @Test
    fun `gitt cache eksisterer, forvent at den oppdateres som forventet`() {
        val utløpsdato = ZonedDateTime.now(UTC).plusDays(3)

        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            request = RequestEntity
                .post(CACHE_PATH)
                .headers(hentToken().tokenTilHeader())
                .body(
                    CacheRequestDTO(
                        nøkkelPrefiks = "mellomlagring_psb",
                        verdi = "verdi-123",
                        utløpsdato = utløpsdato
                    )
                ),
            expectedStatus = HttpStatus.CREATED
        )

        val body = restTemplate.putAndAssert<CacheRequestDTO, CacheResponseDTO>(
            request = RequestEntity
                .put("$CACHE_PATH/mellomlagring_psb")
                .headers(hentToken().tokenTilHeader())
                .body(
                    CacheRequestDTO(
                        nøkkelPrefiks = "mellomlagring_psb",
                        verdi = "endret-verdi-456",
                        utløpsdato = utløpsdato
                    )
                ),
            expectedStatus = HttpStatus.OK,
            expectedBody = null
        )
        assertThat(body as CacheResponseDTO).isNotNull()
        assertThat(body.nøkkel).isEqualTo("mellomlagring_psb_12345678910")
        assertThat(body.verdi).isEqualTo("endret-verdi-456")
    }

    @Test
    fun `gitt cache eksisterer, forvent at den blir slettet som forventet`() {
        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            request = RequestEntity
                .post(CACHE_PATH)
                .headers(hentToken().tokenTilHeader())
                .body(
                    CacheRequestDTO(
                        nøkkelPrefiks = "mellomlagring_psb",
                        verdi = "verdi-123",
                        utløpsdato = ZonedDateTime.now().plusDays(3)
                    )
                ),
            expectedStatus = HttpStatus.CREATED
        )

        restTemplate.deleteAndAssert(
            request = RequestEntity
                .delete("$CACHE_PATH/mellomlagring_psb")
                .headers(hentToken().tokenTilHeader())
                .build(),
            expectedStatus = HttpStatus.NO_CONTENT
        )
    }

    @Test
    fun `gitt cache ikke eksisterer, forvent ikke-funnet feil`() {
        restTemplate.deleteAndAssert(
            request = RequestEntity
                .delete("$CACHE_PATH/mellomlagring_psb")
                .headers(hentToken().tokenTilHeader())
                .build(),
            expectedStatus = HttpStatus.NOT_FOUND
        )
    }

    @Test
    fun `gitt cache med nøkkelPrefiks eksisterer på person, forvent konfliktfeil`() {
        val cacheRequestDTO = CacheRequestDTO(
            nøkkelPrefiks = "mellomlagring_psb",
            verdi = "verdi-123",
            utløpsdato = ZonedDateTime.now().plusDays(3)
        )

        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            request = RequestEntity
                .post(CACHE_PATH)
                .headers(hentToken().tokenTilHeader())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cacheRequestDTO),
            expectedStatus = HttpStatus.CREATED
        )

        restTemplate.postAndAssert<CacheRequestDTO, ProblemDetail>(
            request = RequestEntity
                .post(CACHE_PATH)
                .headers(hentToken().tokenTilHeader())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cacheRequestDTO),
            expectedStatus = HttpStatus.CONFLICT
        )
    }

    @Test
    fun `gitt lagret cache på en person, forvent ikke funnet når en annen person henter samme prefiks`() {
        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            request = RequestEntity
                .post(CACHE_PATH)
                .headers(hentToken().tokenTilHeader())
                .body(
                    CacheRequestDTO(
                        nøkkelPrefiks = "mellomlagring_psb",
                        ytelse = Ytelse.PLEIEPENGER_SYKT_BARN,
                        verdi = "verdi-123",
                        utløpsdato = ZonedDateTime.now().plusDays(3)
                    )
                ),
            expectedStatus = HttpStatus.CREATED
        )

        restTemplate.getAndAssert(
            request = RequestEntity
                .get("$CACHE_PATH/mellomlagring_psb")
                .headers(hentToken(fnr = "11111111111").tokenTilHeader())
                .build(),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = Unit
        )
    }

    @Test
    fun `Gitt oppdatering av ikke eksisterende cache, forvent feil`() {
        restTemplate.putAndAssert<CacheRequestDTO, Unit>(
            request = RequestEntity
                .put("$CACHE_PATH/mellomlagring_psb")
                .headers(hentToken().tokenTilHeader())
                .body(
                    CacheRequestDTO(
                        nøkkelPrefiks = "ikke-eksisterende-cache",
                        verdi = "verdi-123",
                        ytelse = Ytelse.PLEIEPENGER_SYKT_BARN,
                        utløpsdato = ZonedDateTime.now().plusDays(3)
                    )
                ),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = null
        )
    }

    @Test
    fun `Gitt cache ikke eksisterer, forvent ikke-funnet feil`() {
        restTemplate.getAndAssert(
            request = RequestEntity
                .get("$CACHE_PATH/ikke-eksisterende-cache")
                .headers(hentToken().tokenTilHeader())
                .build(),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = Unit
        )
    }

    @Test
    fun `gitt cache lagres, mens hentes på annen prefiks, forvent null`() {
        restTemplate.postAndAssert<CacheRequestDTO, CacheResponseDTO>(
            request = RequestEntity
                .post(CACHE_PATH)
                .headers(hentToken().tokenTilHeader())
                .body(
                    CacheRequestDTO(
                        nøkkelPrefiks = "mellomlagring_psb",
                        verdi = "verdi-123",
                        utløpsdato = ZonedDateTime.now().plusDays(3)
                    )
                ),
            expectedStatus = HttpStatus.CREATED
        )

        // samt, at hentet oppføring er lik den som ble lagret i db.
        restTemplate.getAndAssert(
            request = RequestEntity
                .get("$CACHE_PATH/mellomlagring_pnn")
                .headers(hentToken().tokenTilHeader())
                .build(),
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedBody = Unit
        )
    }

    @Test
    fun `gitt 2 utgåtte cache, forvent at begge slettes`() {
        val now = ZonedDateTime.now(UTC)
        val utløpsdato = now.minusMinutes(1)

        cacheRepository.saveAll(
            listOf(
                CacheEntryDAO(
                    nøkkel = "nøkkel-1",
                    verdi = "cache som utløper 1",
                    utløpsdato = utløpsdato,
                    opprettet = now.minusHours(1)
                ),
                CacheEntryDAO(
                    nøkkel = "nøkkel-2",
                    verdi = "cache som utløper 2",
                    utløpsdato = utløpsdato,
                    opprettet = now.minusHours(1)
                ),
                CacheEntryDAO(
                    nøkkel = "nøkkel-3",
                    verdi = "skal ikke utløpe enda",
                    utløpsdato = utløpsdato.plusMinutes(30),
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
