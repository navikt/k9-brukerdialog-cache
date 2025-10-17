package no.nav.cache.cache

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Transactional
interface CacheRepository : JpaRepository<CacheEntryDAO, String> {

    fun findByNøkkel(nøkkel: String): CacheEntryDAO?

    @Modifying
    @Query(value = "DELETE FROM cache WHERE utløpsdato < :dato", nativeQuery = true)
    fun deleteAllByUtløpsdatoIsBefore(dato: ZonedDateTime): Int

    @Query(value = "SELECT * FROM cache WHERE utløpsdato < :dato", nativeQuery = true)
    fun findAllByUtløpsdatoIsBefore(dato: ZonedDateTime): List<CacheEntryDAO>
}
