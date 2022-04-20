package no.nav.cache.cache

import assertk.assertThat
import assertk.assertions.isEqualTo
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

@DataJpaTest
@ActiveProfiles("test")
@EnableMockOAuth2Server
@ExtendWith(SpringExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CacheRepositoryTest {

    @Autowired
    lateinit var repository: CacheRepository // Repository som brukes til databasekall.

    @BeforeEach
    internal fun tearDown() {
        repository.deleteAll() //Tømmer databasen mellom hver test
    }

    @Test
    fun `gitt 2 utgåtte cache, forvent at begge slettes`() {
        val now = ZonedDateTime.now(UTC)
        val utløpsdato = now.minusMinutes(1)

        repository.saveAll(
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
        assertThat(repository.count()).isEqualTo(3)
        assertThat(repository.deleteAllByUtløpsdatoIsBefore(now)).isEqualTo(2)
        assertThat(repository.count()).isEqualTo(1)
    }
}
