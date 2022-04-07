package no.nav.cache.cache

import org.springframework.data.jpa.repository.JpaRepository

interface CacheRepository : JpaRepository<CacheEntryDAO, String> {

    fun findByNøkkel(nøkkel: String): CacheEntryDAO?

}
