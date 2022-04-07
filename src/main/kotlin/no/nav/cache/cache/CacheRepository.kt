package no.nav.cache.cache

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Transactional
interface CacheRepository : JpaRepository<CacheEntryDAO, String> {

    fun findByNøkkel(nøkkel: String): CacheEntryDAO?

    fun deleteAllByUtløpsdatoIsAfter(dato: ZonedDateTime): Int
}
