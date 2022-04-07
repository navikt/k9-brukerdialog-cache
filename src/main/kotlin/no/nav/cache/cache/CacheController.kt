package no.nav.cache.cache

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.cache.cache.CacheController.Endpoints.CACHE_PATH
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.spring.ProtectedRestController
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*

@ProtectedRestController(issuer = "tokenx")
@SecurityRequirement(name = "bearer-jwt", scopes = ["read", "write"])
class CacheController(
    private val cacheService: CacheService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CacheController::class.java)
    }

    object Endpoints {
        const val CACHE_PATH = "/api/cache"
    }

    @PostMapping(CACHE_PATH)
    @ResponseStatus(CREATED)
    fun lagre(@RequestBody cacheEntryDTO: CacheRequestDTO): CacheResponseDTO {
        logger.info("Oppretter ny cache entry...")
        val cache = this.cacheService.lagre(cacheEntryDTO)
        logger.info("Cache entry opprettet.")
        return cache
    }

    @GetMapping("$CACHE_PATH/{nokkel-prefiks}")
    @Protected
    @ResponseStatus(OK)
    fun hent(@PathVariable("nokkel-prefiks", required = true) nøkkelPrefiks: String): CacheResponseDTO {
        logger.info("Henter entry cache entry...")
        val cache = this.cacheService.hent(nøkkelPrefiks)
        logger.info("Cache entry hentet.")
        return cache
    }

    @PutMapping("$CACHE_PATH/{nokkel-prefiks}")
    @ResponseStatus(OK)
    fun oppdater(@RequestBody cacheEntryDTO: CacheRequestDTO): CacheResponseDTO {
        logger.info("Oppdaterer cache entry...")
        val cache = this.cacheService.oppdater(cacheEntryDTO)
        logger.info("Cache entry oppdatert.")
        return cache
    }

    @DeleteMapping("$CACHE_PATH/{nokkel-prefiks}")
    @Protected
    @ResponseStatus(NO_CONTENT)
    fun slett(@PathVariable("nokkel-prefiks", required = true) nøkkePrefiks: String) {
        logger.info("Sletter cache entry...")
        this.cacheService.slett(nøkkePrefiks)
        logger.info("Cache entry slettet.")
    }
}
