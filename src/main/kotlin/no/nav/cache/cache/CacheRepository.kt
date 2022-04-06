package no.nav.cache.cache

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CacheRepository : JpaRepository<CacheEntryDAO, UUID> {

    fun findByNøkkel(nøkkel: String): CacheEntryDAO?

}
